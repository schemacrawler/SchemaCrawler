#!/bin/sh
java -cp $(echo lib/*.jar | tr ' ' ':') schemacrawler.Main $*
