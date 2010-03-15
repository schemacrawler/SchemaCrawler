rm -f *.class
javac -classpath ../schemacrawler-8.2-SNAPSHOT.jar ApiExample.java
java -classpath ../schemacrawler-8.2-SNAPSHOT.jar:../hsqldb.jar:. ApiExample
