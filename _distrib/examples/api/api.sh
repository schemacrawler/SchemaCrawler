rm -f *.class
javac -classpath schemacrawler-3.8.jar ApiExample.java
java -classpath .:schemacrawler-3.8.jar ApiExample
