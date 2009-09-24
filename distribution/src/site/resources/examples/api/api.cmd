@echo off
del /f /q *.class
javac -classpath ../schemacrawler-7.3.1.jar ApiExample.java
java -classpath ../schemacrawler-7.3.1.jar;../hsqldb.jar;. ApiExample
