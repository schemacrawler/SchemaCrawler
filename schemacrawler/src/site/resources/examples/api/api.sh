rm -f *.class
javac -classpath ../_schemacrawler/lib/schemacrawler-8.11.jar ApiExample.java
java -classpath ../_schemacrawler/lib/schemacrawler-8.11.jar:../_schemacrawler/lib/hsqldb-2.2.6.jar:. ApiExample
