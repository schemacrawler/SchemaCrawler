java -classpath ../../_schemacrawler/config:$(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':') schemacrawler.Main -server=sqlite -database=$1 -user=sa -password= -infolevel=maximum -command=schema -outputformat=pdf -outputfile=$2
echo Database diagram is in $2
