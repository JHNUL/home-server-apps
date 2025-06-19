#!/bin/bash

MAX_RETRIES=10
RETRY_DELAY=1
COUNT=0

echo "Checking TimescaleDB availability..."

while ! psql -h database -U verysecretuser -d homeserver -c '\q' 2>/dev/null; do
  COUNT=$((COUNT+1))
  echo "Attempt $COUNT/$MAX_RETRIES: TimescaleDB not ready yet..."

  if [ "$COUNT" -ge "$MAX_RETRIES" ]; then
    echo "Too many attempts, man. Giving up. 💀"
    exit 1
  fi

  sleep "$RETRY_DELAY"
done

echo "TimescaleDB is up, man. Let’s roll. 🎳"
