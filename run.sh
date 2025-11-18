#!/usr/bin/env bash

set -euo pipefail

docker compose -f docker/docker-compose-ui.yml up -d --remove-orphans --force-recreate

echo "To shut down cleanly run 'docker compose -f docker/docker-compose-ui.yml down --remove-orphans --volumes'"