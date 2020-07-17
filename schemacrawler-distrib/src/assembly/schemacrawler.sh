#!/usr/bin/env bash
SC_DIR=$(dirname "$0")
java -Djava.util.logging.config.class=sf.util.LoggingConfig -cp "$SC_DIR"/lib/*:"$SC_DIR"/config schemacrawler.Main "$@"
