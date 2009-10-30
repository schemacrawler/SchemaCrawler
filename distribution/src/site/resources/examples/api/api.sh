rm -f *.class
javac -classpath ../schemacrawler-7.5.jar ApiExample.java
java -classpath ../schemacrawler-7.5.jar:../hsqldb.jar:. ApiExample
