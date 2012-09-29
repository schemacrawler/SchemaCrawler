@echo off
del /q /f database-dump.html
call java -classpath ../_schemacrawler/lib/*;lib/* schemacrawler.tools.hsqldb.Main -database=schemacrawler -user=sa -password= -infolevel=standard -command=count,dump -outputformat=html -outputfile=database-dump.html %*
echo Database dump is in database-dump.html