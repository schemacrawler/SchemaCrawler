#!/usr/bin/env bash
javac -classpath ../../_schemacrawler/lib/*:../../_schemacrawler/config:. com/example/ResultSetExample.java
java -Djava.util.logging.config.class=sf.util.LoggingConfig -classpath ../../_schemacrawler/lib/*:../../_schemacrawler/config:. com.example.ResultSetExample
