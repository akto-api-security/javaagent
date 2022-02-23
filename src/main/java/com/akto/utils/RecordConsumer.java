package com.akto.utils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface RecordConsumer {
    public void consume(String s);

    public static class KafkaSender implements RecordConsumer {

        Kafka kafka;
        String topic = "akto.api.logs";
        ConcurrentLinkedQueue<String> q;
        int MAX_QUEUE_SIZE = 500;
        int MAX_PAYLOAD_SIZE = 2000000;
        int count;
        public KafkaSender(String brokerIp) {
            this.kafka = new Kafka(brokerIp);
            this.count = 0;
            q = new ConcurrentLinkedQueue<>();
            Thread kafkaThread = new Thread() {
                public void run() {
                    try {
                        while(true) {
                            while(q.size() != 0) {
                                kafka.send(q.remove().toString(),topic);
                            }
                            Thread.sleep(100);
                        }
                    }
                    catch(InterruptedException v) {
                        System.out.println(v);
                    }
                }
            };
            kafkaThread.start();
        }

        @Override
        public void consume(String s) {
            if(this.q.size()<MAX_QUEUE_SIZE && s.length()<MAX_PAYLOAD_SIZE) {
                this.q.add(s);
                if(this.count < 100) {
                    System.out.println("[Akto] "+s);
                }
            }
            this.count++;
            if(this.count % 100 == 0) {
                System.out.println("[Akto] Requests Recieved : " + count);
            }
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
