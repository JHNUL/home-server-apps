#!/bin/bash

# Usage: ./generate_csv.sh N START_TIMESTAMP INTERVAL_MINUTES DIRECTION
# Example: ./generate_csv.sh 100 "2025-06-06T06:06:06Z" 5 inc

N=${1:-10000}
START_TS=${2:-"2025-06-06T06:06:06Z"}
INTERVAL_MINUTES=${3:-1}
DIRECTION=${4:-"inc"}

# Convert ISO8601 to epoch
START_EPOCH=$(date -u -d "$START_TS" +"%s")

TARGET_DIR="$(dirname "$(dirname "$(realpath "$0")")")/resources/seed_data"
TARGET_FILE="humidity_status.csv"

rm -f "$TARGET_DIR/$TARGET_FILE"

for ((i=0; i<N; i++)); do
  if [ "$DIRECTION" == "inc" ]; then
    TS_EPOCH=$((START_EPOCH + i * INTERVAL_MINUTES * 60))
  else
    TS_EPOCH=$((START_EPOCH - i * INTERVAL_MINUTES * 60))
  fi

  # Convert epoch back to ISO 8601
  ISO_TS=$(date -u -d "@$TS_EPOCH" +"%Y-%m-%dT%H:%M:%SZ")

  DEVICE_ID=1
  COMPONENT_ID=0
  VALUE=26.7

  echo "$DEVICE_ID,$ISO_TS,$COMPONENT_ID,$VALUE" >> "$TARGET_DIR/$TARGET_FILE"
done
