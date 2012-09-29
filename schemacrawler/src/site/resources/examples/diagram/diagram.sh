rm -f database-diagram.pdf
java -classpath ../_schemacrawler/lib/*:lib/* schemacrawler.tools.hsqldb.Main -database=schemacrawler -user=sa -password= -infolevel=maximum -command=diagram -outputformat=pdf -outputfile=database-diagram.pdf $*
echo Database diagram is in database-diagram.pdf