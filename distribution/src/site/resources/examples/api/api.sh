rm -f *.class
javac -classpath ../schemacrawler-7.6.jar ApiExample.java
java -classpath ../schemacrawler-7.6.jar:../hsqldb.jar:. ApiExample
