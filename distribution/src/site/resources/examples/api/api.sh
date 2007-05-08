rm -f *.class
javac -classpath ../../schemacrawler-5.0.jar ApiExample.java
java -classpath ../../schemacrawler-5.0.jar:../../hsqldb.jar:. ApiExample
