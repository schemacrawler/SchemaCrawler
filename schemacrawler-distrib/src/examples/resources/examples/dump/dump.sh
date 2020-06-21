#!/usr/bin/env bash
rm -f database-dump.html
../../_schemacrawler/schemacrawler.sh --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=standard -c=count,dump --output-format=html -o=database-dump.html "$*"
echo Database dump is in database-dump.html
