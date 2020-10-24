#!/bin/bash
set -e

SCHEMACRAWLER_VERSION=16.11.3

# Download SchemaCrawler distribution
echo "** Downloading SchemaCrawler v$SCHEMACRAWLER_VERSION distribution"

echo "Starting to download"

wget -N -nv https://github.com/schemacrawler/SchemaCrawler/releases/download/v"$SCHEMACRAWLER_VERSION"/schemacrawler-"$SCHEMACRAWLER_VERSION"-distribution.zip
unzip -q -u schemacrawler-"$SCHEMACRAWLER_VERSION"-distribution.zip

echo "Done"
