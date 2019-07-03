java -classpath ../../_schemacrawler/config:$(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':') schemacrawler.Main --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=maximum -c=schema --output-format=pdf -o=database-diagram.pdf $*
echo Database diagram is in database-diagram.pdf
