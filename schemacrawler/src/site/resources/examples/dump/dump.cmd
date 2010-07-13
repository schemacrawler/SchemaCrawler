@echo off
del /q /f database-dump.html
java -classpath ..\_schemacrawler\lib\schemacrawler-8.3.jar;..\_schemacrawler\lib\hsqldb.jar schemacrawler.Main -c hsqldb -command=count,dump -outputformat=html -outputfile=database-dump.html
echo Database dump is in database-dump.html