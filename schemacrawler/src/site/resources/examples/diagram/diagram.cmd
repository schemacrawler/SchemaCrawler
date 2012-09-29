@echo off
del /q /f database-diagram.pdf
call java -classpath ../_schemacrawler/lib/*;lib/* schemacrawler.tools.hsqldb.Main -database=schemacrawler -user=sa -password= -infolevel=maximum -command=graph -outputformat=pdf -outputfile=database-diagram.pdf %*
echo Database diagram is in database-diagram.pdf
