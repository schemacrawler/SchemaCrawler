rm -f database-dump.html
java -classpath ../_schemacrawler/lib/schemacrawler-8.9.jar:../_schemacrawler/lib/hsqldb-2.2.4.jar schemacrawler.Main -c hsqldb -infolevel=standard -command=count,dump -outputformat=html -outputfile=database-dump.html
echo Database dump is in database-dump.html