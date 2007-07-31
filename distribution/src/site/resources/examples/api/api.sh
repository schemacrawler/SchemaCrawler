rm -f *.class
javac -classpath ../../schemacrawler-5.1.jar ApiExample.java
java -classpath ../../schemacrawler-5.1.jar:../../hsqldb.jar:. ApiExample
