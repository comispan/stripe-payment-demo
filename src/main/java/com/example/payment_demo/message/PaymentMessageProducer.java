package com.example.payment_demo.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;  // change type
    private final ObjectMapper objectMapper;

    @Value(value = "${kafka.topic.payment-success}")
    private String topic;

    public PaymentMessageProducer(KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendPaymentEvent(String productId, String transactionId, String reservationId) {
        Map<String, String> payload = new HashMap<>();
        payload.put("productId", productId);
        payload.put("transactionId", transactionId);
        payload.put("reservationId", reservationId);

        // Send to Kafka topic
        try {
            String message = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topic, productId, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish payment event for product {}, transactionId {}, reservationId {}" +
                                        ": {}", productId, transactionId, reservationId, ex.getMessage());
                        throw new RuntimeException("Kafka send failed for product: " + productId, ex);
                    }
                    log.info("Published payment event for product {}, transactionId {}, reservationId {}",
                            productId, transactionId, reservationId);
                });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payment event", e);
        }
    }
}