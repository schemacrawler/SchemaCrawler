rm -f *.class
javac -classpath ../_schemacrawler/lib/schemacrawler-8.3.jar ApiExample.java
java -classpath ../_schemacrawler/lib/schemacrawler-8.3.jar:../_schemacrawler/lib/hsqldb.jar:. ApiExample
