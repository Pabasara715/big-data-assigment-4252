#!/bin/bash

echo "Stopping All Services..."
cd infrastructure/docker
docker-compose down

echo ""
echo "All services stopped successfully!"
