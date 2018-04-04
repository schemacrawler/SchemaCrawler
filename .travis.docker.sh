#!/bin/bash

# Print Docker version in Travis logs
docker version  

# Build Docker image
docker build -t schemacrawler/schemacrawler .
docker tag schemacrawler/schemacrawler schemacrawler/schemacrawler:v14.20.03
docker tag schemacrawler/schemacrawler schemacrawler/schemacrawler:latest

# Deploy image to Docker Hub
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push schemacrawler/schemacrawler
docker logout  

# Remove the image, so Travis does not cache it
docker rm schemacrawler/schemacrawler

