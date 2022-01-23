@echo off
..\..\_schemacrawler\bin\schemacrawler.cmd --server=hsqldb --database=schemacrawler --schemas "PUBLIC\.BOOKS" --tables ".*AUTHORS|.*BOOKS" --user=sa --password= --info-level=standard -c script --script %1
