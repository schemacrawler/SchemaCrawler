#!/usr/bin/env bash
# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

../../_schemacrawler/bin/schemacrawler.sh --server=sqlite --database="$1" --user=sa --password= --info-level=maximum -c=schema --output-format=pdf -o="$2"
echo Database diagram is in "$2"
