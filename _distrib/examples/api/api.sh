rm -f *.class
javac -classpath schemacrawler-4.0.jar ApiExample.java
java -classpath .:schemacrawler-4.0.jar ApiExample
