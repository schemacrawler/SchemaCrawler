@echo off
del /q /f database-dump.html
java -classpath ../schemacrawler-6.1.jar;../hsqldb.jar schemacrawler.Main -c hsqldb -command=count,dump -outputformat=html -outputfile=database-dump.html
echo Database dump is in database-dump.html