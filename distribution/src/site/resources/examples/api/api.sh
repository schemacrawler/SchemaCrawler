rm -f *.class
javac -classpath ../../schemacrawler-5.4.jar ApiExample.java
java -classpath ../../schemacrawler-5.4.jar:../../hsqldb.jar:. ApiExample
