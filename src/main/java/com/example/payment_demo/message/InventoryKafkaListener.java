package com.example.payment_demo.message;

import com.example.payment_demo.data.ProductReservation;
import com.example.payment_demo.enums.ReservationStatus;
import com.example.payment_demo.repository.ReservationRepository;
import com.example.payment_demo.service.InventoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class InventoryKafkaListener {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ReservationRepository reservationRepository;

    private final ObjectMapper objectMapper;

    public InventoryKafkaListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Transactional
    @KafkaListener(topics = {"${kafka.topic.payment-success}"}, groupId =
            "${kafka.consumer.inventory.group-id}", containerFactory = "listenerContainerFactory")
    public void handleMessage(String message) {
        try {
            Map<String, String> payload = objectMapper.readValue(message, new TypeReference<>() {});

            String productId = payload.get("productId");
            String transactionId = payload.get("transactionId");
            String reservationId = payload.get("reservationId");

            if (productId == null || transactionId == null || reservationId == null) {
                throw new IllegalArgumentException("Missing required fields in Kafka message: " + message);
            }

            log.info("Kafka message received for product {}, transactionId {}, reservationId {}", productId, transactionId, reservationId);

            ProductReservation res = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalStateException("Reservation not found: " + reservationId));

            boolean success = inventoryService.deductStock(productId);
            if (!success) {
                throw new IllegalStateException("Failed to deduct stock for product: " + productId);
            }

            res.setStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(res);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize Kafka message", e);
        }
    }
}