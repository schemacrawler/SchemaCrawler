#!/bin/bash
set -e

# Download SchemaCrawler _testdb distribution
echo "** Obtaining SchemaCrawler v$SCHEMACRAWLER_VERSION distribution"

echo "Starting to obtain"

cp -rp ../../schemacrawler-distrib/target/_distribution/_testdb .

echo "Done"
