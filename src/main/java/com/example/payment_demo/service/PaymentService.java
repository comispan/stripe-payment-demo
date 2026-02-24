package com.example.payment_demo.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Autowired
    private InventoryService inventoryService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public String createPaymentIntent(String productId, String userId) throws StripeException {
        boolean isAvailable = inventoryService.getCurrentStock(productId) != 0;
        if (!isAvailable) {
            throw new RuntimeException("Item " + productId + " is currently out of stock.");
        }

        // Look up price based on ID
        long amount = lookupPrice(productId);

        // Reserve specifically for this user session
        String reservationId = inventoryService.reserveStockForUser(productId, userId);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("sgd")
                .putMetadata("productId", productId)
                .putMetadata("reservationId", reservationId)
                .putMetadata("userId", userId)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret();
    }

    private long lookupPrice(String productId) {
        // Mock database lookup
        if ("tshirt_001".equals(productId)) return 2000L; // $20.00
        if ("hat_001".equals(productId)) return 1500L;    // $15.00
        return 0L;
    }
}
