package com.example.payment_demo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.producer.key-serializer}")
    private String producerKeySerializer;

    @Value(value = "${kafka.producer.value-serializer}")
    private String producerValueSerializer;

    @Value(value = "${kafka.consumer.key-deserializer}")
    private String consumerKeyDeserializer;

    @Value(value = "${kafka.consumer.value-deserializer}")
    private String consumerValueDeserializer;

    @Value(value = "${kafka.consumer.auto-offset-reset}")
    private String consumerAutoOffsetReset;

    @Value(value = "${kafka.consumer.inventory.group-id}")
    private String consumerInventoryGroupId;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerKeySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerValueSerializer);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerKeyDeserializer);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerValueDeserializer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerInventoryGroupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerAutoOffsetReset);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}