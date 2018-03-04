@echo off
del /q /f database-dump.html
call ..\..\_schemacrawler\schemacrawler.cmd -server=hsqldb -database=schemacrawler -user=sa -password= -infolevel=standard -command=count,dump -outputformat=html -outputfile=database-dump.html %*
echo Database dump is in database-dump.html