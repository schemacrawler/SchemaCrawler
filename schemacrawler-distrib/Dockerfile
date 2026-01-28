# ========================================================================
# SchemaCrawler AI
# http://www.schemacrawler.com
# Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
# All rights reserved.
# SPDX-License-Identifier: EPL-2.0
# ========================================================================

FROM bellsoft/liberica-openjdk-alpine:21

# Install Graphviz as root user
RUN \
  apk add --update --no-cache \
  bash \
  bash-completion \
  graphviz \
  ttf-freefont

# Copy SchemaCrawler distribution from the build directory
COPY . /opt/schemacrawler/
# Ensure that the SchemaCrawler script is executable
RUN \
    chmod +rx /opt/schemacrawler/bin/schemacrawler.sh \
 && mkdir -p /etc/bash_completion.d \
 && cp \
      /opt/schemacrawler/cli-support/schemacrawler-completion.bash \
      /etc/bash_completion.d/schemacrawler

# Run the image as a non-root user, "schcrwlr"
RUN \
    adduser -S -u 1000 -G users schcrwlr \
 && mkdir -p /home/schcrwlr \
 && chown -R schcrwlr:users /home/schcrwlr

# Copy configuration and example files with rw-rw-r-- for the current user
COPY \
    --chown=schcrwlr:users \
    ./config/* \
    ./sc.db \
    ./*.py \
    /home/schcrwlr/

# Change user
USER schcrwlr
WORKDIR /home/schcrwlr

# Create .bashrc with aliases for SchemaCrawler
RUN \
    echo 'alias schemacrawler="/opt/schemacrawler/bin/schemacrawler.sh"' \
    >> /home/schcrwlr/.profile \
 && echo 'export schemacrawler' \
    >> /home/schcrwlr/.profile \
 && cp /home/schcrwlr/.profile /home/schcrwlr/.bashrc \
 && echo 'PS1="\[\e[0;33m\]SchemaCrawler\[\e[m\]:\w\$ "'  >> /home/schcrwlr/.bashrc \
 && echo 'echo "`schemacrawler --version`"' >> ~/.bashrc \
 && echo 'echo -e "\n\nUse the schemacrawler command.\n"' >> /home/schcrwlr/.bashrc

CMD ["bash"]
