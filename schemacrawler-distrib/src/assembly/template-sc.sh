#!/bin/sh
java -cp $(echo /opt/schemacrawler/lib/*.jar | tr ' ' ':') @package@.Main $*
