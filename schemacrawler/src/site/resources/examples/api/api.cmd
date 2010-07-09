@echo off
del /f /q *.class
javac -classpath ..\lib\schemacrawler-8.3.jar ApiExample.java
java -classpath ..\lib\schemacrawler-8.3.jar;..\lib\hsqldb-2.0.0.jar;. ApiExample
