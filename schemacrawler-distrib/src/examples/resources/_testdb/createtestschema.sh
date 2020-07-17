#!/usr/bin/env bash
SC_DIR=$(dirname "$0")
java -Djava.util.logging.config.class=sf.util.LoggingConfig -cp "$(echo "$SC_DIR"/lib/*.jar | tr ' ' ':'):." schemacrawler.testdb.TestSchemaCreatorMain "$@"
