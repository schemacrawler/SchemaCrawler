#!/bin/bash

SC_DIR=`pwd`/..

# ## DISTRIBUTION
echo "Setting up distribution"

# Download additional libraries to allow SchemaCrawler commands to work
echo "Downloading additional libraries to support SchemaCrawler"
cd $SC_DIR
cd ./schemacrawler-distrib/target/_distribution/_downloader
chmod +x ./download.sh

./download.sh shell
./download.sh offline

./download.sh postgresql-embedded

./download.sh groovy
./download.sh python
./download.sh ruby

./download.sh velocity
./download.sh thymeleaf 

# Additional setup
echo "Performing additional setup of distribution"
cd $SC_DIR
cd ./schemacrawler-distrib/target/_distribution

rm ./_schemacrawler/lib/slf4j-jdk14-*.jar
cp ./examples/shell/schemacrawler-shell.* ./_schemacrawler


# ## DOCKER
echo "Creating Docker container"

# Set up Dockerfile to be within the build context, to support older versions
# of Docker
echo "Setting up Dockerfile to create container"
cd $SC_DIR
cp ./schemacrawler-docker/Dockerfile ./schemacrawler-distrib/target/_distribution

# The build context is the distribution staging directory,
# schemacrawler-distrib/target/_distribution
cd $SC_DIR
cd ./schemacrawler-distrib/target/_distribution

# Print Docker version
pwd
docker version  

# Build Docker image
docker build -t schemacrawler/schemacrawler .
docker tag schemacrawler/schemacrawler schemacrawler/schemacrawler:v15.03.02 
docker tag schemacrawler/schemacrawler schemacrawler/schemacrawler:latest

# Deploy image to Docker Hub
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push schemacrawler/schemacrawler
docker logout  

# Remove local image
# docker rm schemacrawler/schemacrawler

