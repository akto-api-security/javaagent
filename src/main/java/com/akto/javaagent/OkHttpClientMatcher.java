package com.akto.javaagent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import okhttp3.Call;
import okhttp3.Response;


import net.bytebuddy.agent.builder.AgentBuilder.Transformer;

public class OkHttpClientMatcher extends ElementMatcher.Junction.AbstractBase<TypeDescription> implements Transformer {
    private static ElementMatcher<TypeDescription> elem = ElementMatchers.named("okhttp3.RealCall");

    public boolean matches(TypeDescription target, boolean interfacesOnly) {
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
        System.out.println("done with: " + typeDescription); 
        return builder.visit(methodsVisitor);
    }

    private static class EnterAdvice {
        @Advice.OnMethodEnter()
        public static void enter(@Advice.This Call call) {
            try { 
                System.out.println("enter");
                System.out.println(call.request());
            
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

      private static class ExitAdviceMethods {
        @Advice.OnMethodExit
        public static void exit(@Advice.This Call call, @Advice.Return Response response) {
            try { 
                System.out.println("exit");
                System.out.println(call.request());
                System.out.println(response);
            
            } catch (Exception e) {
                e.printStackTrace();
            }

        }  
      }
  

    @Override
    public boolean matches(TypeDescription target) {
      return this.matches(target, true);
    }    
}
