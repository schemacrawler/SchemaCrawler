@echo off
java -classpath ../_schemacrawler/lib/*;lib/* schemacrawler.tools.hsqldb.Main -server=hsqldb -database=schemacrawler -user=sa -password= -infolevel=standard -command=hsqldb.tables %*