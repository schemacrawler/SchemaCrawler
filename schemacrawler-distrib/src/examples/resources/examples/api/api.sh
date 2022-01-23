#!/usr/bin/env bash
javac -classpath ../../_schemacrawler/bin/lib/*:../../_schemacrawler/config:. com/example/ApiExample.java
java -classpath ../../_schemacrawler/bin/lib/*:../../_schemacrawler/config:. com.example.ApiExample
