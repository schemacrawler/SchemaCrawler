@echo off
del /f /q *.class
javac -classpath schemacrawler-4.2.jar ApiExample.java
java -classpath schemacrawler-4.2.jar;../../dbserver/hsqldb.jar;. ApiExample
