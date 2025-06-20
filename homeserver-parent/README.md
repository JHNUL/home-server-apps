# Homeserver

Homeserver monorepo including the following components:

| Name                | Type      | Description                                                     |
|---------------------|-----------|-----------------------------------------------------------------|
| message-server      | service   | a service that listens to events and provides a rest api        |
| homeserver-common   | library   | includes common classes that might be used by multiple services |
| homeserver-database | migration | liquibase database migration scripts                            |

## How to?

See `message-server` to get going.