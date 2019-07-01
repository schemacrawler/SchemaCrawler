@echo off
..\..\_schemacrawler\schemacrawler.cmd --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=standard -c template --info-level=maximum --sort-tables=false --template-language=mustache --template %1
