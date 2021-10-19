#!/usr/bin/env bash
../../_schemacrawler/schemacrawler.sh --server=hsqldb --database=schemacrawler --schemas "PUBLIC\.BOOKS" --tables ".*AUTHORS|.*BOOKS" --user=sa --password= --info-level=standard -c script --sort-tables=false --script "$1"
