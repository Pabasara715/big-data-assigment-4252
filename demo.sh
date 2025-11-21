#!/bin/bash

# Kafka Order Processing System - Demo Script
# This script demonstrates the full workflow of the system

set -e

echo "=========================================="
echo "Kafka Order Processing System - Demo"
echo "=========================================="
echo ""

# Check if services are running
echo "Step 1: Checking if services are running..."
if ! docker ps | grep -q "producer-service"; then
    echo "Error: Producer service is not running."
    echo "Please start services with: cd infrastructure/docker && docker-compose up -d"
    exit 1
fi

if ! docker ps | grep -q "consumer-service"; then
    echo "Error: Consumer service is not running."
    echo "Please start services with: cd infrastructure/docker && docker-compose up -d"
    exit 1
fi

echo "✓ All services are running"
echo ""

# Wait for services to be ready
echo "Step 2: Waiting for services to be ready (30 seconds)..."
sleep 30
echo "✓ Services should be ready"
echo ""

# Check health endpoints
echo "Step 3: Checking service health..."
echo "Producer health:"
curl -s http://localhost:8081/api/orders/health || echo "Producer not responding"
echo ""
echo "Consumer health:"
curl -s http://localhost:8082/api/stats/health || echo "Consumer not responding"
echo ""
echo ""

# Send test orders
echo "Step 4: Sending test orders..."
echo ""

echo "Sending Order 1001: Item1 at $99.99"
curl -X POST "http://localhost:8081/api/orders?orderId=1001&product=Item1&price=99.99"
echo ""

sleep 2

echo "Sending Order 1002: Item1 at $89.99"
curl -X POST "http://localhost:8081/api/orders?orderId=1002&product=Item1&price=89.99"
echo ""

sleep 2

echo "Sending Order 1003: Item2 at $149.99"
curl -X POST "http://localhost:8081/api/orders?orderId=1003&product=Item2&price=149.99"
echo ""

sleep 2

echo "Sending Order 1004: Item1 at $109.99"
curl -X POST "http://localhost:8081/api/orders?orderId=1004&product=Item1&price=109.99"
echo ""

sleep 2

echo "Sending Order 1005: Item2 at $199.99"
curl -X POST "http://localhost:8081/api/orders?orderId=1005&product=Item2&price=199.99"
echo ""

echo ""
echo "✓ All orders sent successfully"
echo ""

# Wait for consumer to process messages
echo "Step 5: Waiting for consumer to process messages (5 seconds)..."
sleep 5
echo ""

# Check statistics
echo "Step 6: Checking price statistics..."
echo ""

echo "Average price for Item1:"
curl -s http://localhost:8082/api/stats/average/Item1 | jq . || curl -s http://localhost:8082/api/stats/average/Item1
echo ""

echo "Average price for Item2:"
curl -s http://localhost:8082/api/stats/average/Item2 | jq . || curl -s http://localhost:8082/api/stats/average/Item2
echo ""

echo "All product averages:"
curl -s http://localhost:8082/api/stats/averages | jq . || curl -s http://localhost:8082/api/stats/averages
echo ""

echo ""
echo "=========================================="
echo "Demo completed successfully!"
echo "=========================================="
echo ""
echo "You can now:"
echo "  - Visit Kafka UI at: http://localhost:8080"
echo "  - View the 'orders' topic to see messages"
echo "  - Check consumer groups and lag"
echo "  - Send more orders with: curl -X POST \"http://localhost:8081/api/orders?orderId=1006&product=Item3&price=79.99\""
echo ""
