@echo off
del /f /q *.class
javac -classpath ../../schemacrawler-5.5.jar ApiExample.java
java -classpath ../../schemacrawler-5.5.jar;../../hsqldb.jar;. ApiExample
