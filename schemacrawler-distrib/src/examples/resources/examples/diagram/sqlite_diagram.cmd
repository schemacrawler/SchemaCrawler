@echo off
..\..\_schemacrawler\bin\schemacrawler.cmd --server=sqlite "--database=%1" --user=sa --password= --info-level=maximum -c=schema --output-format=pdf "-o=%2"
echo Database diagram is in %2
