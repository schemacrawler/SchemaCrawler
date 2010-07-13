@echo off
del /q /f database-diagram.pdf
java -classpath ..\_schemacrawler\lib\schemacrawler-8.3.jar;..\_schemacrawler\lib\hsqldb.jar schemacrawler.Main -c hsqldb -infolevel=lint -command=graph -outputformat=pdf -outputfile=database-diagram.pdf
echo Database diagram is in database-diagram.pdf