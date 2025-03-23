#!/usr/bin/env bash
echo Downloading "$1"
java -jar ivy-2.5.3.jar -ivy "$1"_ivy.xml -settings ivysettings.xml -retrieve "../_schemacrawler/lib/[artifact]-[revision](-[classifier]).[ext]"
