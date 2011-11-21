@echo off
del /f /q *.class
javac -classpath ..\_schemacrawler\lib\schemacrawler-8.9.jar ApiExample.java
java -classpath ..\_schemacrawler\lib\schemacrawler-8.9.jar;..\_schemacrawler\lib\hsqldb-2.2.4.jar;. ApiExample
