#!/usr/bin/env bash
SC_DIR=$(dirname "$0")
java -cp $(echo "$SC_DIR"/lib/*.jar | tr ' ' ':'):. schemacrawler.testdb.TestDatabase
