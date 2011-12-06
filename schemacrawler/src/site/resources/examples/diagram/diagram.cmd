@echo off
del /q /f database-diagram.pdf
..\_schemacrawler\sc.cmd -database=schemacrawler -user=sa -password= -infolevel=maximum -command=graph -outputformat=pdf -outputfile=database-diagram.pdf
echo Database diagram is in database-diagram.pdf