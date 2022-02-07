package com.akto.javaagent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.mongodb.BasicDBObject;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.util.ByteArrayBuffer;


public class HttpClientMatcher extends ElementMatcher.Junction.AbstractBase<TypeDescription> implements Transformer {
    private static ElementMatcher<TypeDescription> elem = ElementMatchers.isSubTypeOf(org.apache.http.client.HttpClient.class);

    boolean debug = false;
    public static final int MAX_ENTITY_LEN = 10_000_000; 

    @Override
    public DynamicType.Builder<?> transform(
        final DynamicType.Builder<?> builder,
        final TypeDescription typeDescription,
        final ClassLoader classLoader,
        final JavaModule module) {

        debug = true;
        
        final AsmVisitorWrapper methodsVisitor =
        Advice.to(EnterAdvice.class, ExitAdviceMethods.class)
            .on(
              ElementMatchers.named("execute")
              .and(ElementMatchers.isMethod())
              .and(ElementMatchers.not(ElementMatchers.isAbstract()))
            . and(ElementMatchers.takesArgument(0, ElementMatchers.isSubTypeOf(org.apache.http.HttpRequest.class)))
            );
        return builder.visit(methodsVisitor);
    }

    public static String toJsonStr(Header[] headers) {
      BasicDBObject ret = new BasicDBObject();

      if (headers == null) {
        return ret.toJson();
      }

      for (Header header: headers) {
        ret.put(header.getName(), header.getValue());
      }

      return ret.toJson();

    }

    public static void addConstants(BasicDBObject obj) {
      obj.put("ip", "");
      obj.put("time", ""+(int)(System.currentTimeMillis()/1000l));
      obj.put("akto_account_id", "" + 1_000_000);
      obj.put("akto_vxlan_id", 123);
      obj.put("source", "OTHER");
    }

    public static String readEntity(HttpEntity entity) {
      String ret = "";

      if (entity == null || entity.getContentLength() == 0 || !entity.isRepeatable()) {
        return ret;
      }

      try {
          final InputStream instream = entity.getContent();
          if (instream == null || entity.getContentLength() > MAX_ENTITY_LEN) {
              return ret;
          }

          int i = (int)entity.getContentLength();
          if (i < 0) {
              i = 4096;
          }
          final ByteArrayBuffer buffer = new ByteArrayBuffer(i);
          final byte[] tmp = new byte[4096];
          int l;
          while((l = instream.read(tmp)) != -1) {
              buffer.append(tmp, 0, l);
          }
          return new String(buffer.toByteArray(), StandardCharsets.ISO_8859_1);
      } catch (IOException e) {

      }
      
      return ret;
    }


    private static class EnterAdvice {


      @Advice.OnMethodEnter(suppress = Throwable.class)
      public static BasicDBObject enter(@Advice.AllArguments Object[] args) {
        BasicDBObject ret = new BasicDBObject();
        try { 
          for(Object object: args) {

            if (object instanceof HttpRequest) {
              HttpRequest request = (HttpRequest) object;
              RequestLine requestLine = request.getRequestLine();

              ret.put("path", requestLine.getUri());
              ret.put("method", requestLine.getMethod());
              ret.put("type", requestLine.getProtocolVersion().toString());
              ret.put("requestHeaders", toJsonStr(request.getAllHeaders()));

              String requestPayload = "";

              if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;
                HttpEntity entity = enclosingRequest.getEntity();
                requestPayload = readEntity(entity);
              }

              ret.put("requestPayload", requestPayload);
            }
          }
        } catch (Exception e) {

        }
        return ret;
        
      }
    }

    private static class ExitAdviceMethods {
      @Advice.OnMethodExit(suppress = Throwable.class)
      public static void exit(@Advice.Return Object responseObj, @Advice.Enter BasicDBObject returnFromEnter) {

        if (returnFromEnter == null) {
          return;
        }

        if (!(responseObj instanceof HttpResponse)) {
          return;
        }

        HttpResponse response = (HttpResponse) responseObj;

        StatusLine statusLine = response.getStatusLine();
        returnFromEnter.put("statusCode", statusLine.getStatusCode()+"");
        returnFromEnter.put("responseHeaders", toJsonStr(response.getAllHeaders()));
        returnFromEnter.put("status", statusLine.getReasonPhrase());
        HttpEntity entity = response.getEntity();
        returnFromEnter.put("responsePayload", readEntity(entity));        

        addConstants(returnFromEnter);

        AgentMain.recordConsumer.consume(returnFromEnter.toJson());
        System.out.println(returnFromEnter);
      }  
    }

    @Override
    public boolean matches(TypeDescription target) {
      return elem.matches(target);
    }    
}
