#!/usr/bin/env bash
# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

../../_schemacrawler/bin/schemacrawler.sh --server=hsqldb --database=schemacrawler --schemas "PUBLIC\.BOOKS" --tables ".*AUTHORS|.*BOOKS" --user=sa --password= --info-level=standard -c script --sort-tables=false --script "$1"
