@echo off
del /f /q *.class
javac -classpath ../schemacrawler-6.5.jar ApiExample.java
java -classpath ../schemacrawler-6.5.jar;../hsqldb.jar;. ApiExample
