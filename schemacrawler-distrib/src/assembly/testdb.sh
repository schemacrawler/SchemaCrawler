#!/usr/bin/env bash
# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

SC_DIR=$(dirname "$0")/..
java -cp "$SC_DIR"/lib/* schemacrawler.testdb.TestSchemaCreatorMain "$@"
