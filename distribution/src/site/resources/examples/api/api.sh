rm -f *.class
javac -classpath schemacrawler-4.1.jar ApiExample.java
java -classpath .:schemacrawler-4.1.jar ApiExample
