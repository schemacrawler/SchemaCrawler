#!/bin/sh
java -cp $(echo /opt/schemacrawler/lib/*.jar | tr ' ' ':') schemacrawler.Main $*
