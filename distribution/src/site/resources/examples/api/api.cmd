@echo off
del /f /q *.class
javac -classpath ../../schemacrawler-5.2.jar ApiExample.java
java -classpath ../../schemacrawler-5.2.jar;../../hsqldb.jar;. ApiExample
