#!/usr/bin/env bash

set -euo pipefail

package_json_version=$(grep version package.json | head -n 1 | cut -d ":" -f2 | tr -d "\",[:space:]")
push="${1:-false}"

DIR=$(dirname $0)

rm -rf ${DIR}/dist

cd $DIR

npm test && npm run build

docker build . -t "juhanir/homeserver-ui:${package_json_version}"

if [ "$push" = "true" ]; then
    docker push "juhanir/homeserver-ui:${package_json_version}"
fi
