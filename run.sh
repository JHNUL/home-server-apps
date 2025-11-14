#!/usr/bin/env bash

docker compose -f docker/docker-compose-ui.yml up -d --remove-orphans --force-recreate