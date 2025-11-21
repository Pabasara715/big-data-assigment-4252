# Kafka Order Processing System

A complete Kafka-based order processing system with producer and consumer services, featuring Avro serialization, retry logic, Dead Letter Queue (DLQ), and running price aggregation.

## Features

- **Avro Serialization**: Schema-based message serialization with Schema Registry
- **Producer Service**: REST API to send orders to Kafka
- **Consumer Service**: Consumes orders and calculates running price averages
- **Retry Mechanism**: Automatic retry on failures (3 attempts with 1-second interval)
- **Dead Letter Queue**: Failed messages after retries go to DLQ topic
- **Running Aggregation**: Real-time calculation of average prices per product
- **Kafka UI**: Web interface to monitor topics, messages, and consumers

## Prerequisites

- Docker & Docker Compose
- Java 21+
- Maven 3.6+
- curl (for testing)
- jq (optional, for JSON formatting)

## Quick Start

### 1. Start All Services with Docker Compose

```bash
cd infrastructure/docker
docker-compose up -d
```

This starts:
- Zookeeper (port 2181)
- Kafka (port 9092)
- Schema Registry (port 8085)
- Kafka UI (port 8080)
- Producer Service (port 8081)
- Consumer Service (port 8082)

Wait about 30-60 seconds for all services to be ready.

Access Kafka UI at: http://localhost:8080

### 2. Run Demo

```bash
./demo.sh
```

### Alternative: Run Services Locally (Development)

If you want to run services locally instead of Docker:

**Terminal 1 - Start Infrastructure Only:**
```bash
cd infrastructure/docker
docker-compose up -d zookeeper kafka schema-registry kafka-ui
```

**Terminal 2 - Producer Service:**
```bash
cd producer-service
mvn spring-boot:run
```

**Terminal 3 - Consumer Service:**
```bash
cd consumer-service
mvn spring-boot:run
```

## API Endpoints

### Producer Service (Port 8081)

**Create Order**
```bash
POST /api/orders?orderId={orderId}&product={product}&price={price}

# Example
curl -X POST "http://localhost:8081/api/orders?orderId=1001&product=Item1&price=99.99"
```

**Note**: `orderId` is a unique identifier you provide for each order (e.g., "1001", "1002", "1003")

**Health Check**
```bash
GET /api/orders/health

curl http://localhost:8081/api/orders/health
```

### Consumer Service (Port 8082)

**Get Product Average**
```bash
GET /api/stats/average/{product}

# Example
curl http://localhost:8082/api/stats/average/Item1
```

**Get All Averages**
```bash
GET /api/stats/averages

curl http://localhost:8082/api/stats/averages
```

**Health Check**
```bash
GET /api/stats/health

curl http://localhost:8082/api/stats/health
```

## Manual Testing

### Send Orders

```bash
# Item1 - $99.99 (Order ID: 1001)
curl -X POST "http://localhost:8081/api/orders?orderId=1001&product=Item1&price=99.99"

# Item1 - $89.99 (Order ID: 1002)
curl -X POST "http://localhost:8081/api/orders?orderId=1002&product=Item1&price=89.99"

# Item2 - $149.99 (Order ID: 1003)
curl -X POST "http://localhost:8081/api/orders?orderId=1003&product=Item2&price=149.99"

# Item1 - $109.99 (Order ID: 1004)
curl -X POST "http://localhost:8081/api/orders?orderId=1004&product=Item1&price=109.99"
```

### Check Statistics

```bash
# Average for Item1 (should be ~99.99)
curl http://localhost:8082/api/stats/average/Item1 | jq .

# Average for Item2 (should be 149.99)
curl http://localhost:8082/api/stats/average/Item2 | jq .

# All averages
curl http://localhost:8082/api/stats/averages | jq .
```

## Configuration

### Environment Variables

Copy and configure `.env` file:

```bash
cd infrastructure/docker
cp .env.example .env
```

### Application Configuration

**Producer**: `producer-service/src/main/resources/application.yml`
**Consumer**: `consumer-service/src/main/resources/application.yml`

## Topics

- `orders`: Main topic for order messages
- `orders-dlq`: Dead Letter Queue for failed messages

## Monitoring

### Kafka UI

Access at http://localhost:8080

Features:
- View topics and messages
- Monitor consumer groups
- Check consumer lag
- Browse schemas in Schema Registry

### Logs

**Producer logs**:
```bash
cd producer-service
mvn spring-boot:run
```

**Consumer logs**:
```bash
cd consumer-service
mvn spring-boot:run
```

**Infrastructure logs**:
```bash
cd infrastructure/docker
docker-compose logs -f
```

## Error Handling

### Retry Logic

Consumer automatically retries failed messages:
- 3 retry attempts
- 1-second interval between retries
- After 3 failures, message goes to DLQ

### Dead Letter Queue

Failed messages are sent to `orders-dlq` topic. View them in Kafka UI or consume manually:

```bash
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic orders-dlq \
  --from-beginning
```

## Stopping Services

### Stop All Services

```bash
cd infrastructure/docker
docker-compose down
```

### Stop and Clean Up (Remove volumes)

```bash
cd infrastructure/docker
docker-compose down -v
```

## Project Structure

```
BigdataAssigment/
├── infrastructure/docker/
│   ├── docker-compose.yml
│   └── .env.example
├── producer-service/
│   ├── src/main/java/com/kafka/producer/
│   │   ├── config/KafkaProducerConfig.java
│   │   ├── service/OrderProducerService.java
│   │   ├── controller/OrderController.java
│   │   └── ProducerApplication.java
│   ├── src/main/resources/
│   │   ├── avro/order.avsc
│   │   └── application.yml
│   └── pom.xml
├── consumer-service/
│   ├── src/main/java/com/kafka/consumer/
│   │   ├── config/KafkaConsumerConfig.java
│   │   ├── service/OrderConsumerService.java
│   │   ├── service/PriceAggregatorService.java
│   │   ├── controller/StatsController.java
│   │   └── ConsumerApplication.java
│   ├── src/main/resources/
│   │   ├── avro/order.avsc
│   │   └── application.yml
│   └── pom.xml
├── start-infra.sh
├── stop-infra.sh
├── build-all.sh
├── demo.sh
└── README.md
```

## Troubleshooting

### Port Already in Use

Check and kill processes using required ports:

```bash
# Windows PowerShell
Get-NetTCPConnection -LocalPort 8080,8081,8082,9092 | Select-Object OwningProcess | Stop-Process

# Linux/Mac
lsof -ti:8080,8081,8082,9092 | xargs kill -9
```

### Schema Registry Connection Issues

Ensure Schema Registry is running:

```bash
curl http://localhost:8085/subjects
```
