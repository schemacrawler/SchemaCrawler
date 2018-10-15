#!/bin/bash

# Set up Dockerfile to be within the build context, to support older versions
# of Docker
# The build context is the distribution staging directory,
# schemacrawler-distrib/target/_distribution
cp ./Dockerfile ../schemacrawler-distrib/target/_distribution
cd ../schemacrawler-distrib/target/_distribution

# Print Docker version
pwd
docker version  

# Build Docker image
docker build -t schemacrawler/schemacrawler .
docker tag schemacrawler/schemacrawler schemacrawler/schemacrawler:v15.01.05 
docker tag schemacrawler/schemacrawler schemacrawler/schemacrawler:latest

# Deploy image to Docker Hub
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push schemacrawler/schemacrawler
docker logout  

# Remove local image
# docker rm schemacrawler/schemacrawler

