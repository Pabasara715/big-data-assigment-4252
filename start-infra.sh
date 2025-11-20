#!/bin/bash

echo "Starting All Services (Kafka Infrastructure + Producer + Consumer)..."
cd infrastructure/docker
docker-compose up -d

echo ""
echo "Waiting for services to be ready..."
echo "This may take 30-60 seconds..."
sleep 45

echo ""
echo "All services started successfully!"
echo ""
echo "Services:"
echo "  - Zookeeper: localhost:2181"
echo "  - Kafka: localhost:9092"
echo "  - Schema Registry: http://localhost:8085"
echo "  - Kafka UI: http://localhost:8080"
echo "  - Producer Service: http://localhost:8081"
echo "  - Consumer Service: http://localhost:8082"
echo ""
echo "Check service health:"
echo "  curl http://localhost:8081/api/orders/health"
echo "  curl http://localhost:8082/api/stats/health"
echo ""
echo "Run 'docker-compose logs -f' to view logs"
echo "Run './demo.sh' to send test orders"
