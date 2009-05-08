rm -f *.class
javac -classpath ../schemacrawler-6.4.jar ApiExample.java
java -classpath ../schemacrawler-6.4.jar:../hsqldb.jar:. ApiExample
