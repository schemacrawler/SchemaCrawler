@echo off
javac -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;. com/example/ApiExample.java
java -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;. com.example.ApiExample
