# ========================================================================
# SchemaCrawler
# http://www.schemacrawler.com
# Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
# All rights reserved.
# ------------------------------------------------------------------------
#
# SchemaCrawler is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#
# SchemaCrawler and the accompanying materials are made available under
# the terms of the Eclipse Public License v1.0, GNU General Public License
# v3 or GNU Lesser General Public License v3.
#
# You may elect to redistribute this code under any of these licenses.
#
# The Eclipse Public License is available at:
# http://www.eclipse.org/legal/epl-v10.html
#
# The GNU General Public License v3 and the GNU Lesser General Public
# License v3 are available at:
# http://www.gnu.org/licenses/
#
# ========================================================================


FROM openjdk

ARG SCHEMACRAWLER_VERSION=14.16.01

LABEL "us.fatehi.schemacrawler.product-version"="SchemaCrawler ${SCHEMACRAWLER_VERSION}" \
      "us.fatehi.schemacrawler.website"="http://www.schemacrawler.com" \
      "us.fatehi.schemacrawler.docker-hub"="https://hub.docker.com/r/sualeh/schemacrawler"

# Install GraphViz
RUN \
    apt-get update \
 && apt-get install -y graphviz \
 && rm -rf /var/lib/apt/lists/*

# Download SchemaCrawler and prepare install directories
RUN \
    wget -nv https://github.com/sualeh/SchemaCrawler/releases/download/v"$SCHEMACRAWLER_VERSION"/schemacrawler-"$SCHEMACRAWLER_VERSION"-main.zip \
 && unzip -q schemacrawler-"$SCHEMACRAWLER_VERSION"-main.zip \
 && mv schemacrawler-"$SCHEMACRAWLER_VERSION"-main/_schemacrawler schemacrawler \
 && mv schemacrawler-"$SCHEMACRAWLER_VERSION"-main/_testdb/sc.db schemacrawler/sc.db \
 && rm schemacrawler-"$SCHEMACRAWLER_VERSION"-main.zip \
 && rm -rf schemacrawler-"$SCHEMACRAWLER_VERSION"-main

# Mapping directories
VOLUME /share
WORKDIR /schemacrawler

MAINTAINER Sualeh Fatehi <sualeh@hotmail.com>
