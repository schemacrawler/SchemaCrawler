FROM mcr.microsoft.com/devcontainers/java:21

RUN \
  apt-get update && \
  export DEBIAN_FRONTEND=noninteractive && \
  apt-get -y install --no-install-recommends gnupg2 graphviz fonts-freefont-ttf && \
  apt-get clean && \
  apt-get autoremove --purge && \
  rm -rf /var/lib/apt/lists/*
