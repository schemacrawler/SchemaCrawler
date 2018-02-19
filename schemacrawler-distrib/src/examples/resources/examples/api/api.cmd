@echo off
del /f /q *.class
javac -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;. ApiExample.java
java -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;. ApiExample
