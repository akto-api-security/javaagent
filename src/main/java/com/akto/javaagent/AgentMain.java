package com.akto.javaagent;


import net.bytebuddy.agent.builder.AgentBuilder;
import java.lang.instrument.Instrumentation;

public class AgentMain {
  public static void premain(final String agentArgs,
                 final Instrumentation inst) throws Exception {
    System.out.printf("Starting %s\n", AgentMain.class.getSimpleName());

    new AgentBuilder.Default()
        .type(new HttpClientMatcher())
        .transform(new HttpClientMatcher())
        .type(new OkHttpClientMatcher())
        .transform(new OkHttpClientMatcher())
        .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
        .installOn(inst);
  }
  public static void agentmain(final String agentArgs,
                 final Instrumentation inst) throws Exception {
    System.out.printf("Starting %s\n", AgentMain.class.getSimpleName());

    new AgentBuilder.Default()
        .type(new HttpClientMatcher())
        .transform(new HttpClientMatcher())
        .type(new OkHttpClientMatcher())
        .transform(new OkHttpClientMatcher())
        .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
        .installOn(inst);
  }
}