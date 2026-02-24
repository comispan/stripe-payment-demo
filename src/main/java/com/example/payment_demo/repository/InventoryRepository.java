package com.example.payment_demo.repository;

import com.example.payment_demo.data.ProductInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<ProductInventory, String> {
    // Look up stock by product ID (e.g., 'tshirt_001')
    Optional<ProductInventory> findByProductId(String productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductInventory p WHERE p.productId = :productId")
    Optional<ProductInventory> findByProductIdWithLock(@Param("productId") String productId);
}
