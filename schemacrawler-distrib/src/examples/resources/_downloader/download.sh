#!/usr/bin/env bash
# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

echo Downloading "$1"
java -jar ivy-2.5.3.jar -ivy "$1"_ivy.xml -settings ivysettings.xml -retrieve "../_schemacrawler/lib/[artifact]-[revision](-[classifier]).[ext]"
