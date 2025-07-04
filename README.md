# Homeserver

[![Java CI with Maven](https://github.com/JHNUL/home-server-apps/actions/workflows/main.yaml/badge.svg?branch=main&event=push)](https://github.com/JHNUL/home-server-apps/actions/workflows/main.yaml)

Homeserver monorepo including the following components.

| Name                | Type      | Description                                                     | Maven module |
|---------------------|-----------|-----------------------------------------------------------------|--------------|
| message-server      | service   | Service that listens to events and provides a rest api.         | Yes          |
| homeserver-common   | library   | Includes common classes that might be used by multiple services. | Yes          |
| homeserver-database | migration | Liquibase database migration scripts.        | No  |

## How to?

See `message-server` to get going.