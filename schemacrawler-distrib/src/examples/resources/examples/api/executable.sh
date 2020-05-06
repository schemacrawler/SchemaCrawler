rm -f *.class
javac -classpath ../../_schemacrawler/config:$(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':'):. com/example/ExecutableExample.java
java -classpath ../../_schemacrawler/config:$(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':'):. com.example.ExecutableExample
