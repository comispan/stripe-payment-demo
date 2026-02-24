package com.example.payment_demo.data;

import com.example.payment_demo.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_reservation")
@Data               // Generates Getters, Setters, toString, etc. (requires Lombok)
@NoArgsConstructor  // Required by JPA
@AllArgsConstructor // Useful for creating objects in tests
public class ProductReservation {

    @Id
    private String id;

    private String productId;

    private String userId;

    // This annotation tells JPA to store 'PENDING'/'COMPLETED' as text in Postgres
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    private LocalDateTime expiresAt;
}
