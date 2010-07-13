rm -f *.class
javac -classpath ../_schemacrawler/lib/_schemacrawler/lib/schemacrawler-8.3.jar ApiExample.java
java -classpath ../_schemacrawler/lib/_schemacrawler/lib/schemacrawler-8.3.jar:../_schemacrawler/lib/_schemacrawler/lib/hsqldb-2.0.0.jar:. ApiExample
