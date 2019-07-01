@echo off
del /q /f database-dump.html
call ..\..\_schemacrawler\schemacrawler.cmd --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=standard -c=count,dump --output-format=html -o=database-dump.html %*
echo Database dump is in database-dump.html
