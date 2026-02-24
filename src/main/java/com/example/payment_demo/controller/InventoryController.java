package com.example.payment_demo.controller;

import com.example.payment_demo.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Object>> checkStock(@PathVariable String productId) {
        int stock = inventoryService.getCurrentStock(productId);
        boolean available = stock > 0;

        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("available", available);
        response.put("stock", stock);

        return ResponseEntity.ok(response);
    }
}