package com.example.payment_demo.controller;

import com.example.payment_demo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-intent")
    public ResponseEntity<Map<String, String>> createIntent(@RequestBody Map<String, String> request) {
        String productId = request.get("productId");
        String userId = request.get("userId");

        try {
            String clientSecret = paymentService.createPaymentIntent(productId, userId);
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", clientSecret);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}