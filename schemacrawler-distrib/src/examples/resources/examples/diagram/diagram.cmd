@echo off
..\..\_schemacrawler\schemacrawler.cmd -server=hsqldb -database=schemacrawler -user=sa -password= -infolevel=maximum -command=schema -outputformat=pdf -outputfile=database-diagram.pdf %*
echo Database diagram is in database-diagram.pdf
