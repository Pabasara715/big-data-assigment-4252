package com.kafka.consumer.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PriceAggregatorService {

    private final Map<String, PriceStats> priceStatsMap = new ConcurrentHashMap<>();

    public void addPrice(String product, float price) {
        priceStatsMap.compute(product, (key, stats) -> {
            if (stats == null) {
                stats = new PriceStats();
            }
            stats.addPrice(price);
            return stats;
        });

        System.out.println("Running Average for " + product + ": $" + 
                          String.format("%.2f", getRunningAverage(product)));
    }

    public double getRunningAverage(String product) {
        PriceStats stats = priceStatsMap.get(product);
        return stats != null ? stats.getAverage() : 0.0;
    }

    public Map<String, Double> getAllAverages() {
        Map<String, Double> averages = new ConcurrentHashMap<>();
        priceStatsMap.forEach((product, stats) -> 
            averages.put(product, stats.getAverage())
        );
        return averages;
    }

    public long getCount(String product) {
        PriceStats stats = priceStatsMap.get(product);
        return stats != null ? stats.getCount() : 0;
    }

    private static class PriceStats {
        private double sum = 0.0;
        private long count = 0;

        public synchronized void addPrice(double price) {
            sum += price;
            count++;
        }

        public synchronized double getAverage() {
            return count > 0 ? sum / count : 0.0;
        }

        public synchronized long getCount() {
            return count;
        }
    }
}
