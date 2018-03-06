package com.ovoenergy.offer.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestKafkaListeners {

    @KafkaListener(topics = "${kafka.comms.topic.cancellation}")
    public void cancellation(ConsumerRecord<String, ?> cr) {
        System.out.println(cr.key());
        System.out.println(cr.value());
        System.out.println(cr.topic());
    }

    @KafkaListener(topics = "${kafka.comms.topic.orchestration}")
    public void orchestration(ConsumerRecord<String, ?> cr) {
        System.out.println(cr.key());
        System.out.println(cr.value());
        System.out.println(cr.topic());
    }

    @KafkaListener(topics = "${kafka.comms.topic.failed}")
    public void failed(ConsumerRecord<String, ?> cr) {
        System.out.println(cr.key());
        System.out.println(cr.value());
        System.out.println(cr.topic());
    }
}
