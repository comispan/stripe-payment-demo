package com.example.payment_demo.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_inventory")
@Data               // Generates Getters, Setters, toString, etc. (requires Lombok)
@NoArgsConstructor  // Required by JPA
@AllArgsConstructor // Useful for creating objects in tests
public class ProductInventory {

    @Id
    private String productId; // e.g., "tshirt_001" or "hat_001"

    private Integer quantity; // Current stock count

    private String productName; // Optional: Helpful for logging/displaying
}