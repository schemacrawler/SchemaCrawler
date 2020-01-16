@echo off
..\..\_schemacrawler\schemacrawler.cmd --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=standard -c template --templating-language=mustache --template %1
