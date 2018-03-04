@echo off
del /q /f %2
..\..\_schemacrawler\schemacrawler.cmd -server=sqlite "-database=%1" -user=sa -password= -infolevel=maximum -command=schema -outputformat=pdf "-outputfile=%2"
echo Database diagram is in %2
