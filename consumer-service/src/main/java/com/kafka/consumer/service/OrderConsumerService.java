package com.kafka.consumer.service;

import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumerService {

    @Autowired
    private PriceAggregatorService priceAggregatorService;

    @KafkaListener(topics = "${spring.kafka.topic.orders}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrder(@Payload GenericRecord order,
                            @Header(KafkaHeaders.RECEIVED_KEY) String key,
                            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                            @Header(KafkaHeaders.OFFSET) long offset) {
        
        try {
            String orderId = order.get("orderId").toString();
            String product = order.get("product").toString();
            Float price = (Float) order.get("price");

            System.out.println("Received Order:");
            System.out.println("  Order ID: " + orderId);
            System.out.println("  Product: " + product);
            System.out.println("  Price: $" + price);
            System.out.println("  Partition: " + partition + ", Offset: " + offset);

            // Update running average
            priceAggregatorService.addPrice(product, price);

        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
            throw e; // Re-throw to trigger retry and DLQ
        }
    }
}
