package com.akto.javaagent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.bytecode.assign.Assigner.Typing;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

import java.io.IOException;

import com.mongodb.BasicDBObject;

import net.bytebuddy.agent.builder.AgentBuilder.Transformer;

public class OkHttpClientMatcher extends ElementMatcher.Junction.AbstractBase<TypeDescription> implements Transformer {
    private static ElementMatcher<TypeDescription> elem = ElementMatchers.named("okhttp3.RealCall");

    public boolean matches(TypeDescription target) {
        return elem.matches(target.asGenericType().asErasure());
    }
  
    boolean debug = false;

    @Override
    public DynamicType.Builder<?> transform(
        final DynamicType.Builder<?> builder,
        final TypeDescription typeDescription,
        final ClassLoader classLoader,
        final JavaModule module) {

        System.out.println(typeDescription);  
        debug = true;
        
        final AsmVisitorWrapper methodsVisitor =
        Advice.to(EnterAdvice.class, ExitAdviceMethods.class)
            .on(
              ElementMatchers.isMethod().and(ElementMatchers.named("execute"))
            );

        final AsmVisitorWrapper enqueueVisitor =
        Advice.to(EnterAdviceEnqueue.class)
            .on(
              ElementMatchers.isMethod().and(ElementMatchers.named("enqueue"))
            );
        System.out.println("done with: " + typeDescription); 
        return builder.visit(methodsVisitor).visit(enqueueVisitor);
    }

    public static String toJsonStr(Headers headers) {
        BasicDBObject ret = new BasicDBObject();
  
        if (headers == null) {
          return ret.toJson();
        }
  

        headers.forEach(h -> {
            ret.put(h.getFirst(), h.getSecond());
        });

        return ret.toJson();
  
      }
  
      public static void addConstants(BasicDBObject obj) {
        obj.put("ip", "");
        obj.put("time", ""+(int)(System.currentTimeMillis()/1000l));
        obj.put("akto_account_id", "" + 1_000_000);
        obj.put("akto_vxlan_id", 123);
        obj.put("source", "OTHER");
      }
  
      public static String readEntity(RequestBody entity) {
        String ret = "";
  
        try {
            if (entity == null || entity.contentLength() == 0 || entity.isOneShot()) {
              return ret;
            }

            Buffer buffer = new Buffer();
            entity.writeTo(buffer);
            return buffer.readUtf8();

        } catch (IOException e) {
  
        }
        
        return ret;
      }  

      public static String readEntity(ResponseBody entity) {
        String ret = "";
  
        try {
            if (entity == null || entity.contentLength() == 0) {
              return ret;
            }
            
            return entity.string();

        } catch (IOException e) {
  
        }
        
        return ret;
      }  

      private static class EnterAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static BasicDBObject enter(@Advice.This Call call) {
            BasicDBObject ret = new BasicDBObject();
            try { 
                Request request = call.request();
                ret.put("path", request.url().uri().getPath());
                ret.put("method", request.method());
                ret.put("type", "HTTP/1.1");
                ret.put("requestHeaders", toJsonStr(request.headers()));

                String requestPayload = "";

                requestPayload = readEntity(request.body());

                ret.put("requestPayload", requestPayload);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
            
        }
    }

    private static class ExitAdviceMethods {
        @Advice.OnMethodExit
        public static void exit(@Advice.Enter BasicDBObject returnFromEnter, @Advice.Return Object responseObj) {
            try { 

                if (returnFromEnter == null) {
                    return;
                  }
                  if (!(responseObj instanceof Response)) {
                    return;
                  }
          
                  Response response = (Response) responseObj;

                  returnFromEnter.put("statusCode", response.code()+"");
                  returnFromEnter.put("responseHeaders", toJsonStr(response.headers()));
                  returnFromEnter.put("status", response.message());
                  returnFromEnter.put("responsePayload", "");        
                  addConstants(returnFromEnter);
                  System.out.println(returnFromEnter);
          
                  AgentMain.recordConsumer.consume(returnFromEnter.toJson());
                } catch (Exception e) {
                e.printStackTrace();
            }

        }  
      }
  
      private static class EnterAdviceEnqueue {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static BasicDBObject enter(@Advice.This Call call, @Advice.Argument(value=0, readOnly = false, typing = Typing.DYNAMIC) Callback responseCallback
      ) {
            BasicDBObject ret = new BasicDBObject();
            try { 
                Request request = call.request();
                ret.put("path", request.url().uri().getPath());
                ret.put("method", request.method());
                ret.put("type", "HTTP/1.1");
                ret.put("requestHeaders", toJsonStr(request.headers()));

                String requestPayload = "";

                requestPayload = readEntity(request.body());

                ret.put("requestPayload", requestPayload);
                Callback origResponseCallback = responseCallback;
                responseCallback = new OkHttpTracingInterceptor(ret, origResponseCallback);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
            
        }
    }
}
