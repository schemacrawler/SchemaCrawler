@echo off
del /f /q *.class
javac -classpath ../schemacrawler-8.1.jar ApiExample.java
java -classpath ../schemacrawler-8.1.jar;../hsqldb.jar;. ApiExample
