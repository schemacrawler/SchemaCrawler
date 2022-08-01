@echo off
..\..\_schemacrawler\bin\schemacrawler.cmd --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=standard -c=dump --output-format=html -o %1
