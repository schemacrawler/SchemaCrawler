#!/bin/bash
set -e

SCHEMACRAWLER_VERSION=16.9.5

# Download SchemaCrawler distribution
echo "** Obtaining SchemaCrawler v$SCHEMACRAWLER_VERSION distribution"

echo "Starting to obtain"

unzip -q -u ../schemacrawler-distrib/target/schemacrawler-"$SCHEMACRAWLER_VERSION"-distribution.zip

echo "Done"
