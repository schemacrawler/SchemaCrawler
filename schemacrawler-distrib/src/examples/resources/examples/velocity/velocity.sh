#!/usr/bin/env bash
../../_schemacrawler/bin/schemacrawler.sh --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=standard -c template --sort-tables=false --templating-language=velocity --template "$1"
