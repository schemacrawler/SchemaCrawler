#!/bin/bash
docker run \
  --rm -it \
  --mount type=bind,source="$(pwd)",target=/data \
  cytopia/yamllint \
  --config-file .github/linters/.yaml-lint.yml \
  .
