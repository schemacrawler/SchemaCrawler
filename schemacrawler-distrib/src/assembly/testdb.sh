#!/usr/bin/env bash
# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

SCRIPT_PATH=$(readlink -f "$0")
SC_DIR=$(dirname "$SCRIPT_PATH")/..

java -cp "$SC_DIR"/lib/* schemacrawler.testdb.TestSchemaCreatorMain "$@"
