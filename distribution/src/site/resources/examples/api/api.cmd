@echo off
del /f /q *.class
javac -classpath ../schemacrawler-8.0.jar ApiExample.java
java -classpath ../schemacrawler-8.0.jar;../hsqldb.jar;. ApiExample
