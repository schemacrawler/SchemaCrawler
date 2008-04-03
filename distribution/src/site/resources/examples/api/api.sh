rm -f *.class
javac -classpath ../schemacrawler-6.0.jar ApiExample.java
java -classpath ../schemacrawler-6.0.jar:../hsqldb.jar:. ApiExample
