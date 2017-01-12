rm -f database-diagram.pdf
java -classpath $(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':') schemacrawler.Main -server=hsqldb -database=schemacrawler -user=sa -password= -infolevel=maximum -command=schema -outputformat=pdf -outputfile=database-diagram.pdf $*
echo Database diagram is in database-diagram.pdf