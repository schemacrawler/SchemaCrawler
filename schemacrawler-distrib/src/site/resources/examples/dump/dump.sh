rm -f database-dump.html
java -classpath $(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':') schemacrawler.Main -server=hsqldb -database=schemacrawler -user=sa -password= -infolevel=standard -command=count,dump -outputformat=html -outputfile=database-dump.html $*
echo Database dump is in database-dump.html