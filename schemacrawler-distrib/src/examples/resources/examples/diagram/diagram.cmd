@echo off
del /q /f database-diagram.pdf
java -classpath ../../_schemacrawler/config;../../_schemacrawler/lib/*;lib/* schemacrawler.Main -server=hsqldb -database=schemacrawler -user=sa -password= -infolevel=maximum -command=schema -outputformat=pdf -outputfile=database-diagram.pdf %*
echo Database diagram is in database-diagram.pdf
