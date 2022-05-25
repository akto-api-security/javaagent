package com.akto.javaagent;


import net.bytebuddy.agent.builder.AgentBuilder;
import java.lang.instrument.Instrumentation;

import com.akto.utils.RecordConsumer;

public class AgentMain {

  public static RecordConsumer recordConsumer = null;

  public static void premain(final String agentArgs,
                 final Instrumentation inst) throws Exception {

    recordConsumer = new RecordConsumer.QueueRecorder();

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
    
    System.out.println("[Akto] Kafka IP : "+ agentArgs);
    recordConsumer = new RecordConsumer.QueueRecorder();
    
    new AgentBuilder.Default()
        .type(new HttpClientMatcher())
        .transform(new HttpClientMatcher())
        .type(new OkHttpClientMatcher())
        .transform(new OkHttpClientMatcher())
        .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
        .installOn(inst);
  }
}