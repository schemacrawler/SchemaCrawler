# ========================================================================
# SchemaCrawler
# http://www.schemacrawler.com
# Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

FROM eclipse-temurin:11-alpine

LABEL \
  "maintainer"="Sualeh Fatehi <sualeh@hotmail.com>" \
  "org.opencontainers.image.authors"="Sualeh Fatehi <sualeh@hotmail.com>" \
  "org.opencontainers.image.title"="SchemaCrawler" \
  "org.opencontainers.image.description"="Free database schema discovery and comprehension tool" \
  "org.opencontainers.image.url"="https://www.schemacrawler.com/" \
  "org.opencontainers.image.source"="https://github.com/schemacrawler/SchemaCrawler" \
  "org.opencontainers.image.vendor"="SchemaCrawler" \
  "org.opencontainers.image.license"="(GPL-3.0 OR OR LGPL-3.0+ EPL-1.0)"


# Install Graphviz as root user
RUN \
  apk add --update --no-cache \
  bash \
  bash-completion \
  graphviz \
  ttf-freefont

# Copy SchemaCrawler distribution from the build directory
COPY \
    . /opt/schemacrawler/
# Ensure that the SchemaCrawler script is executable
RUN \
    chmod +rx /opt/schemacrawler/bin/schemacrawler.sh

# Run the image as a non-root user, "schcrwlr"
RUN \
    adduser -u 1000 -S schcrwlr -G users
USER schcrwlr
WORKDIR /home/schcrwlr

# Copy configuration and example files with rw-rw-r-- for the current user
COPY \
    --chown=schcrwlr:users \
    ./config/* \
    ./sc.db \
    ./*.py \
    /home/schcrwlr/

# Create aliases for SchemaCrawler
RUN \
    echo 'alias schemacrawler="/opt/schemacrawler/bin/schemacrawler.sh"' \
    >> /home/schcrwlr/.profile \
 && echo 'export schemacrawler' \
    >> /home/schcrwlr/.profile \
 && cp /home/schcrwlr/.profile /home/schcrwlr/.bashrc
