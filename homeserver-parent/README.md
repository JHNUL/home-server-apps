# Homeserver

Homeserver monorepo including the following components:

| Name                | Type      | Description                                                      | Maven module |
|---------------------|-----------|------------------------------------------------------------------|--------------|
| message-server      | service   | Service that listens to events and provides a rest api.          | Yes          |
| homeserver-common   | library   | Includes common classes that might be used by multiple services. | Yes          |
| homeserver-database | migration | Liquibase database migration scripts. Not a maven module.        | No  |

## How to?

See `message-server` to get going.