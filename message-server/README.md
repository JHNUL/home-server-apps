# Message-Server

This project implements an MQTT message listener that listens to certain topics, saves messages to the database and publishes the messages
via WebSocket to subscribed clients. It also provides a REST API.

## Developing

Start the environment with docker compose as explained in the [repo root](../README.md).

You can run your application in dev mode:

```shell
mvn quarkus:dev
```

## Running tests

Quarkus tests spin up the same containers defined in the docker-compose.yml for development. Liquibase runs the migrations to an empty database and the tests wait until migration is ready.

To run all integration and unit tests:
```shell
mvn --projects message-server --also-make clean test
```

Performance tests are run by including the group, this runs only the tests tagged `performance`:
```shell
mvn --projects message-server --also-make clean test -Dgroups=performance
```

Performance tests require that the database is seeded with measurements. Currently, this can be done with a script
in `src/test/scripts/generate_csv.sh`.

## Health check

Health endpoint is exposed at `/q/health` and it has the following paths in addition to just health:

- `/live`
- `/ready`
- `/started`

The root `/q/health` path shows all the above-mentioned info. Health check implemented
by [smallrye health](https://quarkus.io/guides/smallrye-health).

## Packaging the application

The application can be packaged using:

```shell
mvn --projects message-server --also-make clean package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell
mvn --projects message-server --also-make clean package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## OpenAPI specification

Packaging the application also creates the OpenAPI specifications from the API interface. Generated specs are
placed in the `target/generated` directory.

### Native executable

You can create a native executable using:

```shell
mvn package -Dnative
```

## MQTT topics

The application supports the following topics:

### {shelly-device-id}/status/#

The shelly-device-id part is supposed to be unique to each device. The status messages are listed [here](./messages.md). The status messages
do not contain any device information so the device part must be extracted from the topic.