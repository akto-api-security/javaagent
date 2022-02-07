package com.akto.utils;

import java.util.ArrayList;

public interface RecordConsumer {
    public void consume(String s);

    public static class KafkaSender implements RecordConsumer {

        Kafka kafka;
        String topic = "akto.api.logs";

        public KafkaSender(String brokerIp) {
            this.kafka = new Kafka(brokerIp);
        }

        @Override
        public void consume(String s) {
            this.kafka.send(s, topic);
        }
    }

    public static class QueueRecorder implements RecordConsumer {

        public static final ArrayList<String> apiCalls = new ArrayList<>();

        public QueueRecorder() {}

        @Override
        public void consume(String s) {
            apiCalls.add(s);
        }
    }
}
