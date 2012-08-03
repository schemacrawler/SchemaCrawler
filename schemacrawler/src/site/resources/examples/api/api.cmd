@echo off
del /f /q *.class
javac -classpath ..\_schemacrawler\lib\schemacrawler-9.0.jar ApiExample.java
java -classpath ..\_schemacrawler\lib\schemacrawler-9.0.jar;..\_schemacrawler\lib\hsqldb-2.2.8.jar;. ApiExample
