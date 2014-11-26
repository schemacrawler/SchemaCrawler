#!/bin/sh
java -cp $(echo ../_schemacrawler/lib/*.jar | tr ' ' ':') schemacrawler.Main $*
