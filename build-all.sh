#!/bin/bash

echo "Building Docker images for all services..."
echo ""

cd infrastructure/docker
docker-compose build

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo ""
echo "All services built successfully!"
echo ""
echo "Run './start-infra.sh' to start all services"
