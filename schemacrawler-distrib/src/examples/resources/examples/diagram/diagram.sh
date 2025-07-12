#!/usr/bin/env bash
# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

../../_schemacrawler/bin/schemacrawler.sh --server=hsqldb --database=schemacrawler --user=sa --password= --info-level=maximum -c=schema -o "$1"
