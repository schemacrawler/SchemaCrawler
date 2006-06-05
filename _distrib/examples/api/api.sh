rm -f *.class
javac -classpath schemacrawler-3.7.jar ApiExample.java
java -classpath .:schemacrawler-3.7.jar ApiExample
