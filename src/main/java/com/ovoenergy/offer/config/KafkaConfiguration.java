package com.ovoenergy.offer.config;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfiguration {

//    @Bean
//    public ProducerFactory<String, Object> commsProducerFactory() {
//        Map<String, Object> configParams = ImmutableMap.<String, Object>builder()
//                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "bootstrapAddress")
//                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
//                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "todo object class serializer")
//                .build();
//        return new DefaultKafkaProducerFactory<>(configParams);
//    }
//
//    @Bean
//    public KafkaTemplate<String, Object> commsKafkaTemplate() {
//        return new KafkaTemplate<>(commsProducerFactory());
//    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configParams = ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-uat.ovo-uat.aivencloud.com:13581")
                .put(ConsumerConfig.GROUP_ID_CONFIG, "offer-service-test")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .build();
        return new DefaultKafkaConsumerFactory<>(configParams);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
