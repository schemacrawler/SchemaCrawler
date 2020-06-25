#!/usr/bin/env bash
SC_DIR=$(dirname "$0")
java -cp "$SC_DIR"/lib/*:"$SC_DIR"/config schemacrawler.Main "$@"
