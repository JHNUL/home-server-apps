# Message-Server

This project implements an MQTT message listener that listens to certain topics, saves messages to the database and publishes the messages via WebSocket to subscribed clients. It also provides a REST API.

## Prerequisites

- JDK 17+
- Maven 3.9.x
- Docker and docker-compose

In `message-server/src/docker`:
- copy the `db.env.example` file as `db.env` and set the database username and password.
- copy the `liquibase.env.example` file as `liquibase.env` and set the same database username and password as above.

## Developing

Start the environment with docker compose
```shell
docker compose -f src/docker/docker-compose.yml up -d
```

This exposes the following services:
- Postgres database
- Mosquitto MQTT-broker

Check the port numbers from the docker compose file.

To shut it down cleanly, run
```shell
docker compose -f src/docker/docker-compose.yml down --remove-orphans -v
```

You can run your application in dev mode:

```shell
mvn quarkus:dev
```

## Testing

Integration tests can be run from project root with;
```shell
mvn --projects message-server --also-make clean test
```

This way the homeserver-common library, which is a dependency of message-server, is also compiled.

## Packaging the application

The application can be packaged using:

```shell
mvn package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell
mvn package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

### Native executable

You can create a native executable using:

```shell
mvn package -Dnative
```

## MQTT topics

The application supports the following topics:

### {shelly-device-id}/status/#

The shelly-device-id part is supposed to be unique to each device. The status messages are listed [here](./messages.md). The status messages do not contain any device information so the device part must be extracted from the topic.