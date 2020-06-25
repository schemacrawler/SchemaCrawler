#!/usr/bin/env bash
javac -classpath ../../_schemacrawler/lib/*:../../_schemacrawler/config:. com/example/ApiExample.java
java -classpath ../../_schemacrawler/lib/*:../../_schemacrawler/config:. com.example.ApiExample
