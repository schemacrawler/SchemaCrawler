rm -f *.class
javac -classpath schemacrawler-5.0.jar ApiExample.java
java -classpath schemacrawler-5.0.jar:../../dbserver/hsqldb.jar:. ApiExample
