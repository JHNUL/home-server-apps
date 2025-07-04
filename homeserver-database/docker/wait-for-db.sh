#!/usr/bin/env bash

MAX_RETRIES=15
RETRY_DELAY=1
COUNT=0
DATABASE_NAME=$(basename "$LIQUIBASE_COMMAND_URL")

echo "Checking TimescaleDB availability..."

while ! PGUSER="$LIQUIBASE_COMMAND_USERNAME" PGPASSWORD="$LIQUIBASE_COMMAND_PASSWORD" psql -h "$HOST" -d "$DATABASE_NAME" -w -c '\q' 2>/dev/null; do
  COUNT=$((COUNT+1))
  echo "Attempt $COUNT/$MAX_RETRIES: TimescaleDB not ready yet..."

  if [ "$COUNT" -ge "$MAX_RETRIES" ]; then
    echo "Retries exhausted, giving up 💀."
    exit 1
  fi

  sleep "$RETRY_DELAY"
done

echo "TimescaleDB is up. Let’s roll."
