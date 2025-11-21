package com.kafka.consumer.controller;

import com.kafka.consumer.service.PriceAggregatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private PriceAggregatorService priceAggregatorService;

    @GetMapping("/average/{product}")
    public ResponseEntity<Map<String, Object>> getProductAverage(@PathVariable String product) {
        Map<String, Object> response = new HashMap<>();
        response.put("product", product);
        response.put("averagePrice", priceAggregatorService.getRunningAverage(product));
        response.put("count", priceAggregatorService.getCount(product));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/averages")
    public ResponseEntity<Map<String, Double>> getAllAverages() {
        return ResponseEntity.ok(priceAggregatorService.getAllAverages());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Consumer service is running");
    }
}
