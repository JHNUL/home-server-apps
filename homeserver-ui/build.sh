#!/usr/bin/env bash

set -euo pipefail

: "${1:?version argument required}"
version="$1"
push="${2:-false}"

DIR=$(dirname $0)

rm -rf ${DIR}/dist

cd $DIR

npm test && npm run build

docker build . -t "juhanir/homeserver-ui:${version}"

if [ "$push" = "true" ]; then
    docker push "juhanir/homeserver-ui:${version}"
fi
