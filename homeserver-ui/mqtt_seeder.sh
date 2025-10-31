#!/usr/bin/env bash

if ! command -v mosquitto_pub >/dev/null 2>&1; then
    echo mosquitto cli tools not found
    exit 1
fi

for i in {1..100}; do
    suffix=$((i % 10))
    mosquitto_pub --host localhost \
      --port 1883 \
      --qos 1 \
      --topic "iot-device-${suffix}/status/humidity:0" \
      --message "{\"id\":1, \"rh\": $i}"

    mosquitto_pub --host localhost \
      --port 1883 \
      --qos 1 \
      --topic "iot-device-${suffix}/status/temperature:0" \
      --message "{\"id\":1, \"tC\": $i, \"tF\": $i}"
done;
