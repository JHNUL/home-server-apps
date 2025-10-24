package org.juhanir.message_server;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class MessageServerTestResource implements QuarkusTestResourceLifecycleManager {

    private static ComposeContainer containers;

    private static final Duration WAIT_TIME = Duration.of(30, ChronoUnit.SECONDS);
    private static final String MQTT_CONTAINER = "broker";
    private static final String LIQUIBASE_CONTAINER = "liquibase";
    private static final String KEYCLOAK_CONTAINER = "keycloak";

    @Override
    public Map<String, String> start() {
        var mosquittoWaitStrategy = Wait
                .forLogMessage(".*mosquitto version [0-9]{0,1}\\.[0-9]{0,1}\\.[0-9]{0,3} running.*\\n", 1)
                .withStartupTimeout(WAIT_TIME);
        var liquibaseWaitStrategy = Wait
                .forLogMessage(".*Liquibase command 'update' was executed successfully.*\\n", 1)
                .withStartupTimeout(WAIT_TIME);
        var keycloakWaitStrategy = Wait
                .forLogMessage(".*Realm 'homeserver' imported.*\\n", 1)
                .withStartupTimeout(WAIT_TIME);
        containers = new ComposeContainer(MessageServerTestResource.getComposeFile())
                .waitingFor(MQTT_CONTAINER, mosquittoWaitStrategy)
                .waitingFor(LIQUIBASE_CONTAINER, liquibaseWaitStrategy)
                .waitingFor(KEYCLOAK_CONTAINER, keycloakWaitStrategy)
                .withLocalCompose(false)
                .withBuild(false);

        containers.start();

        return Map.of();
    }

    @Override
    public void stop() {
        containers.stop();
    }

    /**
     * Tests can be run from 'message-server' or parent.
     *
     * @return the compose file
     */
    private static File getComposeFile() {
        Path here = Paths.get("").toAbsolutePath();
        Path messageServerPath = here.getParent().resolve("docker/docker-compose.yml");
        boolean messageServerWd = Files.isReadable(messageServerPath);
        if (messageServerWd) {
            return new File(messageServerPath.toUri());
        }

        Path rootPath = here.resolve("docker/docker-compose.yml");
        boolean rootWd = Files.isReadable(rootPath);
        if (rootWd) {
            return new File(rootPath.toUri());
        }
        throw new UncheckedIOException(new IOException("Could not resolve docker compose file"));
    }
}
