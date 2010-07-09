rm -f *.class
javac -classpath ../lib/schemacrawler-8.3.jar ApiExample.java
java -classpath ../lib/schemacrawler-8.3.jar:../lib/hsqldb.jar:. ApiExample
