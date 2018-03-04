@echo off
..\..\_schemacrawler\schemacrawler.cmd -server=hsqldb -database=schemacrawler -user=sa -password= -infolevel=standard -command script -infolevel=maximum -sorttables=false -outputformat %1