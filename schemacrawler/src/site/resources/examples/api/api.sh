rm -f *.class
javac -classpath ../_schemacrawler/lib/schemacrawler-8.10.jar ApiExample.java
java -classpath ../_schemacrawler/lib/schemacrawler-8.10.jar:../_schemacrawler/lib/hsqldb-2.2.6.jar:. ApiExample
