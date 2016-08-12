#!/bin/sh
java -cp $(echo lib/*.jar | tr ' ' ':'):lib schemacrawler.Main $*
