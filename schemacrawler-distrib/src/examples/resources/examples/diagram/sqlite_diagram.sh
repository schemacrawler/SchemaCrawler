#!/usr/bin/env bash
java -classpath ../../_schemacrawler/config:$(echo ../../_schemacrawler/lib/*.jar | tr ' ' ':') schemacrawler.Main --server=sqlite --database=$1 --user=sa --password= --info-level=maximum -c=schema --output-format=pdf -o=$2
echo Database diagram is in "$2"
