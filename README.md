# Homeserver

[![Java CI with Maven](https://github.com/JHNUL/home-server-apps/actions/workflows/main.yaml/badge.svg?branch=main&event=push)](https://github.com/JHNUL/home-server-apps/actions/workflows/main.yaml)

Homeserver monorepo including the following components.

| Name                | Type      | Description                                                      | Maven module |
|---------------------|-----------|------------------------------------------------------------------|--------------|
| message-server      | service   | Service that listens to events and provides a rest api.          | Yes          |
| homeserver-common   | library   | Includes common classes that might be used by multiple services. | Yes          |
| homeserver-ui       | frontend  | React frontend for homeserver.                                   | No  |
| homeserver-database | migration | Liquibase database migration scripts.                            | No  |
| homeserver-keycloak | service   | Identity and access management service.                          | No  |

## How to?

### Prerequisites

- JDK 17+
- Maven 3.9.x
- Docker and docker-compose

In `./docker`:

- copy the `db.env.example` file as `db.env` and set the database username and password.
- copy the `liquibase.env.example` file as `liquibase.env` and set the same database username and password as above.
- copy the `kc-db.env.example` file as `kc-db.env` and set the database username and password.
- copy the `keycloak.env.example` file as `keycloak.env` and set the values.

### Demo stack

- docker and docker compose required
- bash recommended (bash-isms can and do occur in scripts)

Long story short, use the script `run.sh` at project root. This will spin up the entire environment.
Navigate to `http://localhost`. A test user with credentials testuser:testuser123 is created automatically
that you can log in with.

Use `homeserver-ui/mqtt_seeder.sh` to send some data.

### Development environment

Set up the environment:
```shell
docker compose -f docker/docker-compose.yml up -d
```

This exposes the following services:

- Timescale database
- Mosquitto MQTT-broker
- Keycloak server (with own Postgres database)

Then start the message-server:
- cd to message-server and `mvn quarkus:dev`

To shut the environment down cleanly:
```shell
docker compose -f docker/docker-compose.yml down --remove-orphans -v
```

See [message-server](./message-server/README.md) to get going.

## Dependencies

Keep track of updates with
```shell
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
```
