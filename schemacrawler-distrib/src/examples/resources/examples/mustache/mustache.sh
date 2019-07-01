#!/usr/bin/env bash
java -classpath ../../_schemacrawler/config:$(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':') schemacrawler.Main --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=standard -c template --info-level=maximum --sort-tables=false --template-language=mustache --template $1
