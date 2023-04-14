#!/bin/bash
mvn \
  -Dverbose=true \
  -Ddistrib \
  -DskipSortingImports=true \
  -DdisplayFiles=true \
  com.spotify.fmt:fmt-maven-plugin:format
