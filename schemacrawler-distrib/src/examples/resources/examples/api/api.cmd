@echo off
del /f /q *.class
javac -classpath ../../_schemacrawler/lib/*;. ApiExample.java
java -classpath ../../_schemacrawler/lib/*;. ApiExample
