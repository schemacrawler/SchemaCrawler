@echo off
..\..\_schemacrawler\schemacrawler.cmd --server=hsqldb --database=schemacrawler --schemas "PUBLIC\.BOOKS" --tables ".*AUTHORS|.*BOOKS" --user=sa --password= --info-level=standard -c script --script %1
