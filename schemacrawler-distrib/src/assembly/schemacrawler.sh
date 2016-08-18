#!/bin/sh
java -cp $(echo lib/*.jar | tr ' ' ':'):config schemacrawler.Main "$@"
