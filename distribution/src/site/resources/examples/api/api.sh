rm -f *.class
javac -classpath ../schemacrawler-5.6.jar ApiExample.java
java -classpath ../schemacrawler-5.6.jar:../hsqldb.jar:. ApiExample
