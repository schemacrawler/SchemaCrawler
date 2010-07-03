@echo off
del /f /q *.class
javac -classpath ../schemacrawler-8.3.jar ApiExample.java
java -classpath ../schemacrawler-8.3.jar;../hsqldb.jar;. ApiExample
