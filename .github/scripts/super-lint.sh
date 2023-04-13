#!/bin/bash
docker run \
  --rm \
  -e RUN_LOCAL=true \
  -e DEFAULT_BRANCH=lint \
  -e FILTER_REGEX_EXCLUDE=".*\.git/.*" \
  --env-file ".github/super-linter.env" \
  --mount type=bind,source="$(pwd)",target=/tmp/lint \
  github/super-linter
