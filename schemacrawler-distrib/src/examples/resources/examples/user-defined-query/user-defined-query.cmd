@echo off
..\..\_schemacrawler\bin\schemacrawler.cmd --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=standard -c=tables.select %*
