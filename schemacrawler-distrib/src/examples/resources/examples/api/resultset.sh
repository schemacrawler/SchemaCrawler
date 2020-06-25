#!/usr/bin/env bash
javac -classpath ../../_schemacrawler/config:$(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':'):. com/example/ResultSetExample.java
java -classpath ../../_schemacrawler/config:$(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':'):. com.example.ResultSetExample
