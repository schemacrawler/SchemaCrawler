@echo off
del /f /q *.class
javac -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;. ResultSetExample.java
java -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;. ResultSetExample
