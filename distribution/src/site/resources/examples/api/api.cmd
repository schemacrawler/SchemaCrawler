@echo off
del /f /q *.class
javac -classpath ../schemacrawler-7.4.jar ApiExample.java
java -classpath ../schemacrawler-7.4.jar;../hsqldb.jar;. ApiExample
