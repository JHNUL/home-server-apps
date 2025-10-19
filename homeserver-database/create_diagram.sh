#!/usr/bin/env bash

set -euo pipefail

if ! docker ps --filter "name=docker(-|_)database" --format "{{.Names}}" | grep -w database > /dev/null; then
  echo "Timescale container doesn't seem to be running..."
  exit 1
fi

# defaults from $root/docker/docker-compose.yml
NETWORK=${1:-docker_homeserver-network}
HOST_NAME=${2:-database}
DATABASE_NAME=${3:-homeserver}
SCHEMA_NAMES=${4:-sensor}
USERNAME=${5:-verysecretuser}
PASSWORD=${6:-verysecretpassword}

echo "Generating diagram from schema(s) '$SCHEMA_NAMES' in database '$DATABASE_NAME' using network '$NETWORK'"

docker run -u "$(id -u):$(id -g)" \
  --rm -it -v "$(pwd)/schema:/output" \
  --network "${NETWORK}" \
  schemaspy/schemaspy:7.0.2 \
  -t pgsql11 \
  -db "${DATABASE_NAME}" \
  -u "${USERNAME}" \
  -p "${PASSWORD}" \
  --host "${HOST_NAME}" \
  -s "${SCHEMA_NAMES}"

echo "Done"