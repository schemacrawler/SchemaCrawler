@echo off
javac -Djava.util.logging.config.class=sf.util.LoggingConfig -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;. com/example/ResultSetExample.java
java -Djava.util.logging.config.class=sf.util.LoggingConfig -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;. com.example.ResultSetExample
