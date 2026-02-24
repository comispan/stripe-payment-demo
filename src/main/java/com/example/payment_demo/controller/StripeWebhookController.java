package com.example.payment_demo.controller;

import com.example.payment_demo.message.PaymentMessageProducer;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@Slf4j
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private PaymentMessageProducer messageProducer;
    
    @PostMapping("/stripe")
    public String handleStripeEvent(@RequestBody String payload,
                                    @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            // Verify the event signature
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            log.error("Error in verifying event signature: {}", e.getMessage());
            return "Invalid signature"; // Return 400
        }

        // Handle specific event types
        switch (event.getType()) {
            case "payment_intent.succeeded":
                event.getDataObjectDeserializer().getObject().ifPresent(stripeObject -> {
                    if (stripeObject instanceof PaymentIntent intent) {

                        String userId = intent.getMetadata().get("userId");
                        String productId = intent.getMetadata().get("productId");
                        String reservationId = intent.getMetadata().get("reservationId");
                        String transactionId = intent.getId();
                        log.info("Processing payment for browser: {}", userId);

                        // CALL THE KAFKA PRODUCER HERE
                        messageProducer.sendPaymentEvent(productId, transactionId, reservationId);
                        log.info("Payment for {} succeeded!", event.getId() + ". Event send to Kafka.");
                    }
                });
                break;
            case "payment_intent.payment_failed":
                log.info("Payment failed.");
                break;
            case "payment_intent.created":
            case "charge.succeeded":
                log.info(event.getType());
                break;
            default:
                log.info("Unhandled event type: {}", event.getType());
        }

        return "Success";
    }
}