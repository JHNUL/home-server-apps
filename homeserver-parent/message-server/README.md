# Message-Server

This project implements MQTT that listens to certain topics, saves messages to the database and publishes the messages via WebSocket to subscribed clients.

## Prerequisites

- JDK 17+
- Maven 3.9.x
- Docker and docker-compose

## Developing

Start the environment with docker compose at the root of the project
```shell
docker compose -f docker/docker-compose.yml up -d
```
Which exposes the following services:
- Postgres database
- mosquitto MQTT-broker

Check the port numbers and credentials from the docker compose file.

To shut it down cleanly, run
```shell
docker compose -f docker/docker-compose.yml down --remove-orphans -v
```

You can run your application in dev mode that enables live coding using:

```shell
mvn quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

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
