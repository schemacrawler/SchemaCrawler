@echo off
..\..\_schemacrawler\schemacrawler.cmd --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=maximum -c=schema --output-format=pdf -o=database-diagram.pdf %*
echo Database diagram is in database-diagram.pdf
