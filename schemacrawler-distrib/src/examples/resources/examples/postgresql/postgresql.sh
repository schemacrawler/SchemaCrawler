java -classpath ../../_schemacrawler/config:$(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':') schemacrawler.Main -server=postgresql -user=sa -password= $*
