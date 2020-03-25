#!/bin/bash
set -e

# Download SchemaCrawler _testdb distribution
echo "** Obtaining SchemaCrawler v$SCHEMACRAWLER_VERSION distribution"

echo "Starting to obtain"

cp -r ../../schemacrawler-distrib/target/_distribution/_testdb .
wget -O _testdb/lib/ojdbc8_g-19.3.0.0.jar https://search.maven.org/remotecontent?filepath=com/oracle/database/jdbc/debug/ojdbc8_g/19.3.0.0/ojdbc8_g-19.3.0.0.jar
wget -O _testdb/lib/jcc-11.5.0.0.jar https://search.maven.org/remotecontent?filepath=com/ibm/db2/jcc/11.5.0.0/jcc-11.5.0.0.jar

echo "Done"
