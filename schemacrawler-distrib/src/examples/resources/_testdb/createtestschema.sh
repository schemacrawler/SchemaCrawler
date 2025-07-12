#!/usr/bin/env bash
# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

SC_DIR=$(dirname "$0")
java -cp "$(echo "$SC_DIR"/lib/*.jar | tr ' ' ':'):." schemacrawler.testdb.TestSchemaCreatorMain "$@"
