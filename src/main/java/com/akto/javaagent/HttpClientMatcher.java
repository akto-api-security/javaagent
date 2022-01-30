package com.akto.javaagent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;

import java.util.HashSet;
import java.util.Set;


import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class HttpClientMatcher extends ElementMatcher.Junction.AbstractBase<TypeDescription> implements Transformer {
    private static ElementMatcher<TypeDescription> elem = ElementMatchers.named("org.apache.http.client.HttpClient");

    private boolean hasInterface(
      TypeDefinition typeDefinition, Set<TypeDescription> checkedInterfaces) {
        for (TypeDefinition interfaceType : typeDefinition.getInterfaces()) {
          TypeDescription erasure = interfaceType.asErasure();
          if (erasure != null) {
            if (debug) {
              System.out.println("check: " + interfaceType.asGenericType().asErasure() + " " + elem.matches(interfaceType.asGenericType().asErasure()));
            }
  
            if (checkedInterfaces.add(interfaceType.asErasure())
                && (elem.matches(interfaceType.asGenericType().asErasure())
                    || hasInterface(interfaceType, checkedInterfaces))) {
              return true;
            }
          }
        }
        return false;
      }

    public boolean matches(TypeDescription target, boolean interfacesOnly) {
      Set<TypeDescription> checkedInterfaces = new HashSet<>(8);
      TypeDefinition typeDefinition = target;
      while (typeDefinition != null) {
        if (debug) {
          System.out.println("typedef: " + typeDefinition + elem.matches(typeDefinition.asGenericType().asErasure()));
        }
        if (((!interfacesOnly || typeDefinition.isInterface())
                && elem.matches(typeDefinition.asGenericType().asErasure()))
            || hasInterface(typeDefinition, checkedInterfaces)) {
          return true;
        }
        typeDefinition = typeDefinition.getSuperClass();
      }
      return false;
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
              ElementMatchers.named("execute")
              .and(ElementMatchers.isMethod())
              .and(ElementMatchers.not(ElementMatchers.isAbstract()))
            . and(ElementMatchers.takesArgument(1, ElementMatchers.named("org.apache.http.HttpRequest")))
            );
        return builder.visit(methodsVisitor);
    }


    private static class EnterAdvice {
      @Advice.OnMethodEnter(suppress = Throwable.class)
      public static void enter(@Advice.Argument(1) HttpRequest request) {
        System.out.println(request.getRequestLine().toString());
      }
    }

    private static class ExitAdviceMethods {
      @Advice.OnMethodExit(suppress = Throwable.class)
      public static void exit(@Advice.Return Object response) {
        if (response instanceof HttpResponse) {
          HttpResponse httpResponse = (HttpResponse) response;
          System.out.println(httpResponse.getStatusLine());
          
        }
      }  
    }

    @Override
    public boolean matches(TypeDescription target) {
      return this.matches(target, true);
    }    
}
