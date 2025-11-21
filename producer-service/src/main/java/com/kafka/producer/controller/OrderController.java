package com.kafka.producer.controller;

import com.kafka.producer.service.OrderProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderProducerService producerService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(
            @RequestParam String orderId,
            @RequestParam String product,
            @RequestParam float price) {
        
        producerService.sendOrder(orderId, product, price)
                .thenAccept(result -> {
                    System.out.println("Order sent successfully: " + result.getProducerRecord().key());
                })
                .exceptionally(ex -> {
                    System.err.println("Failed to send order: " + ex.getMessage());
                    return null;
                });

        Map<String, String> response = new HashMap<>();
        response.put("status", "Order submitted");
        response.put("orderId", orderId);
        response.put("product", product);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Producer service is running");
    }
}
