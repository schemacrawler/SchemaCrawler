@echo off
..\..\_schemacrawler\schemacrawler.cmd --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=standard -c script --sort-tables=false --script %1
