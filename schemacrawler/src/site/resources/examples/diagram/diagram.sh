rm -f database-diagram.pdf
java -classpath ../_schemacrawler/lib/schemacrawler-8.9.jar:../_schemacrawler/lib/hsqldb-2.2.4.jar schemacrawler.Main -c hsqldb -infolevel=lint -command=diagram -outputformat=pdf -outputfile=database-diagram.pdf
echo Database diagram is in database-diagram.pdf