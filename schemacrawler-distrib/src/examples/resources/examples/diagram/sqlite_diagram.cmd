@echo off
del /q /f %2
call java -classpath ../../_schemacrawler/lib/*;lib/* schemacrawler.Main -server=sqlite "-database=%1" -user=sa -password= -infolevel=maximum -command=schema -outputformat=pdf "-outputfile=%2"
echo Database diagram is in %2
