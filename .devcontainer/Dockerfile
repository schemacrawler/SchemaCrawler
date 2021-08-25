FROM mcr.microsoft.com/vscode/devcontainers/java:0-8

ARG MAVEN_VERSION=""
RUN \
  su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven \"${MAVEN_VERSION}\""

RUN \
  apt-get update && \
  export DEBIAN_FRONTEND=noninteractive && \
  apt-get -y install --no-install-recommends gnupg2 graphviz fonts-freefont-ttf && \
  apt-get clean && \
  apt-get autoremove --purge && \
  rm -rf /var/lib/apt/lists/*
