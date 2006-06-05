rm -f database-dump.html
java -jar schemacrawler-3.7.jar -c hsqldb -command=count,dump -outputformat=html -outputfile=database-dump.html
echo Database dump is in database-dump.html