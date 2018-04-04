#!/bin/bash

# Print Docker version
docker version  

# Build Docker image
docker build -t schemacrawler/schemacrawler -t schemacrawler/schemacrawler:v14.20.03 -t schemacrawler/schemacrawler:latest -f ./Dockerfile ../schemacrawler-distrib/target/_distribution 

# Deploy image to Docker Hub
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push schemacrawler/schemacrawler
docker logout  

# Remove local image
# docker rm schemacrawler/schemacrawler
