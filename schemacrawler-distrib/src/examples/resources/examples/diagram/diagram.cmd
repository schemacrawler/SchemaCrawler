@echo off
..\..\_schemacrawler\bin\schemacrawler.cmd --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=maximum -c=schema -o %1
