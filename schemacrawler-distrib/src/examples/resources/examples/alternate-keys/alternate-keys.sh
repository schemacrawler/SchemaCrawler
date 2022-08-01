#!/usr/bin/env bash
../../_schemacrawler/bin/schemacrawler.sh --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=standard --attributes-file "$1" -c schema -o "$2"
