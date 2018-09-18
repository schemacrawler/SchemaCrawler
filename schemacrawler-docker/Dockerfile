# ========================================================================
# SchemaCrawler
# http://www.schemacrawler.com
# Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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

# ------------------------------------------------------------------------
# This Dockerfile builds a SchemaCrawler Docker image from a local
# SchemaCrawler build. The build should be run from the distribution
# staging directory,
# schemacrawler-distrib/target/_distribution
# ------------------------------------------------------------------------

FROM openjdk

ARG SCHEMACRAWLER_VERSION=15.01.02

LABEL "us.fatehi.schemacrawler.product-version"="SchemaCrawler ${SCHEMACRAWLER_VERSION}" \
      "us.fatehi.schemacrawler.website"="http://www.schemacrawler.com" \
      "us.fatehi.schemacrawler.docker-hub"="https://hub.docker.com/r/schemacrawler/schemacrawler"

# Install GraphViz
RUN \
    apt-get -y -q update \
 && apt-get -y -q install vim \
 && apt-get -y -q install graphviz \
 && rm -rf /var/lib/apt/lists/*

# Copy SchemaCrawler distribution from the local build
COPY _schemacrawler /opt/schemacrawler
COPY _testdb/sc.db /opt/schemacrawler/sc.db
RUN chmod +x /opt/schemacrawler/schemacrawler.sh

# Run the image as a non-root user
RUN useradd -ms /bin/bash schemacrawler
USER schemacrawler
WORKDIR /home/schemacrawler

COPY --chown=schemacrawler:schemacrawler _testdb/sc.db /home/schemacrawler/sc.db
COPY --chown=schemacrawler:schemacrawler _schemacrawler/config/* /home/schemacrawler/

MAINTAINER Sualeh Fatehi <sualeh@hotmail.com>

