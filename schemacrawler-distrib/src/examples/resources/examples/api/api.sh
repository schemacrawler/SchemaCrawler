#!/usr/bin/env bash
# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

javac -classpath ../../_schemacrawler/lib/*:../../_schemacrawler/config:. com/example/ApiExample.java
java -classpath ../../_schemacrawler/lib/*:../../_schemacrawler/config:. com.example.ApiExample
