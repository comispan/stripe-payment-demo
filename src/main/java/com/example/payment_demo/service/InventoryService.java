package com.example.payment_demo.service;

import com.example.payment_demo.data.ProductInventory;
import com.example.payment_demo.data.ProductReservation;
import com.example.payment_demo.enums.ReservationStatus;
import com.example.payment_demo.repository.InventoryRepository;
import com.example.payment_demo.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    // Used by the Frontend Pre-Check
    public int getCurrentStock(String productId) {
        return inventoryRepository.findByProductId(productId)
            .map(ProductInventory::getQuantity)
            .orElse(0);
    }

    // Used by the Webhook Message Listener
    @Transactional
    public boolean deductStock(String productId) {
        Optional<ProductInventory> item = inventoryRepository.findByProductId(productId);

        if (item.isPresent() && item.get().getQuantity() > 0) {
            ProductInventory inventory = item.get();
            inventory.setQuantity(inventory.getQuantity() - 1);
            inventoryRepository.save(inventory);
            return true;
        }
        return false; // Out of stock!
    }

    @Transactional
    public String reserveStockForUser(String productId, String userId) {
        // 1. Pessimistic Lock the main inventory row to prevent race conditions
        ProductInventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 2. Calculate actual availability
        long activeReservations = reservationRepository.countActiveByProductId(productId, LocalDateTime.now());

        if (inventory.getQuantity() - activeReservations > 0) {
            // 3. Create a unique reservation for THIS user
            ProductReservation res = new ProductReservation();
            res.setId(UUID.randomUUID().toString());
            res.setProductId(productId);
            res.setUserId(userId);
            res.setStatus(ReservationStatus.PENDING);
            res.setExpiresAt(LocalDateTime.now().plusMinutes(10));

            reservationRepository.save(res);
            return res.getId(); // Return this to be used as Metadata in Stripe
        }

        throw new RuntimeException("Sold out!");
    }
}