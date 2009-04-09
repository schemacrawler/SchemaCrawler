@echo off
del /f /q *.class
javac -classpath ../schemacrawler-6.2.jar ApiExample.java
java -classpath ../schemacrawler-6.2.jar;../hsqldb.jar;. ApiExample
