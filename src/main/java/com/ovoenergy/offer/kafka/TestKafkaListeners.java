package com.ovoenergy.offer.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestKafkaListeners {

    @KafkaListener(topics = "comms.cancellation.requested.v2")
    public void cancellation(ConsumerRecord<?, ?> cr) {
        System.out.println(cr.key());
        System.out.println(cr.topic());
    }

    @KafkaListener(topics = "comms.orchestration.started.v2")
    public void orchestration(ConsumerRecord<?, ?> cr) {
        System.out.println(cr.key());
        System.out.println(cr.topic());
    }

    @KafkaListener(topics = "comms.failed.cancellation.v2")
    public void failed(ConsumerRecord<?, ?> cr) {
        System.out.println(cr.key());
        System.out.println(cr.topic());
    }
}
