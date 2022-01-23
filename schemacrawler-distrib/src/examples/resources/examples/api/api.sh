#!/usr/bin/env bash
javac -classpath ../../_schemacrawler/bin/lib/*:../../_schemacrawler/bin/config:. com/example/ApiExample.java
java -classpath ../../_schemacrawler/bin/lib/*:../../_schemacrawler/bin/config:. com.example.ApiExample
