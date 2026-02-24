package com.example.payment_demo.repository;

import com.example.payment_demo.data.ProductReservation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<ProductReservation, String> {

    /**
     * Counts all reservations that are currently "holding" stock.
     * A reservation is active if it is PENDING and not yet expired.
     */
    @Query("SELECT COUNT(r) FROM ProductReservation r " +
            "WHERE r.productId = :productId " +
            "AND r.status = 'PENDING' " +
            "AND r.expiresAt > :now")
    long countActiveByProductId(@Param("productId") String productId,
                                @Param("now") LocalDateTime now);

    // Finds reservations that have expired but weren't completed or cancelled
    @Modifying
    @Transactional
    @Query("UPDATE ProductReservation r SET r.status = 'EXPIRED' " +
            "WHERE r.status = 'PENDING' AND r.expiresAt < :now")
    int deactivateExpiredReservations(@Param("now") LocalDateTime now);
}
