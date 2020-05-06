@echo off
del /f /q *.class
javac -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;. com/example/ExecutableExample.java
java -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;. com.example.ExecutableExample
