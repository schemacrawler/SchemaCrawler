#!/bin/sh

DIR=$(dirname $(readlink -f "$0"))

java -cp $(echo $DIR/lib/*.jar | tr ' ' ':'):config schemacrawler.Main "$@"
