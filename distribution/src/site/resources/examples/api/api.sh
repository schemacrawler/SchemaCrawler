rm -f *.class
javac -classpath ../schemacrawler-7.0.jar ApiExample.java
java -classpath ../schemacrawler-7.0.jar:../hsqldb.jar:. ApiExample
