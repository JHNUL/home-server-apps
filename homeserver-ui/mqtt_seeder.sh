#!/usr/bin/env bash

if ! command -v mosquitto_pub >/dev/null 2>&1; then
    echo "mosquitto cli tools not found, cannot seed data"
    exit 0
fi

for i in {1..100}; do
    c=${RANDOM}
    v=$((c % 100))
    suffix=$((i % 10))
    mosquitto_pub --host localhost \
      --port 1883 \
      --qos 1 \
      --topic "iot-device-${suffix}/status/humidity:0" \
      --message "{\"id\":1, \"rh\": $v}"

    mosquitto_pub --host localhost \
      --port 1883 \
      --qos 1 \
      --topic "iot-device-${suffix}/status/temperature:0" \
      --message "{\"id\":1, \"tC\": $v, \"tF\": $v}"
done;
