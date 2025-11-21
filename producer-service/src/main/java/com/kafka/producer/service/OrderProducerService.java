package com.kafka.producer.service;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.orders}")
    private String ordersTopic;

    private Schema orderSchema;

    public OrderProducerService() throws IOException {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("avro/order.avsc");
        this.orderSchema = new Schema.Parser().parse(schemaStream);
    }

    public CompletableFuture<SendResult<String, Object>> sendOrder(String orderId, String product, float price) {
        GenericRecord order = new GenericRecordBuilder(orderSchema)
                .set("orderId", orderId)
                .set("product", product)
                .set("price", price)
                .build();

        return kafkaTemplate.send(ordersTopic, orderId, order);
    }
}
