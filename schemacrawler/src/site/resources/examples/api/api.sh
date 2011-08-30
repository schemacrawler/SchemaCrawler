rm -f *.class
javac -classpath ../_schemacrawler/lib/schemacrawler-8.7.jar ApiExample.java
java -classpath ../_schemacrawler/lib/schemacrawler-8.7.jar:../_schemacrawler/lib/hsqldb-2.2.4.jar:. ApiExample
