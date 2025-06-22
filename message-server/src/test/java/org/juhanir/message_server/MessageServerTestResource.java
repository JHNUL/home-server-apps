package org.juhanir.message_server;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.util.Map;

public class MessageServerTestResource implements QuarkusTestResourceLifecycleManager {

    ComposeContainer containers;

    @Override
    public Map<String, String> start() {
        containers = new ComposeContainer(new File("src/docker/docker-compose.yml"))
                .waitingFor("mqtt_broker", Wait.forLogMessage(".*mosquitto version [0-9]{0,1}\\.[0-9]{0,1}\\.[0-9]{0,3} running.*\\n", 1))
                .waitingFor("liquibase", Wait.forLogMessage(".*Liquibase command 'update' was executed successfully.*\\n", 1))
                .withBuild(false);

        containers.start();

        return Map.of(
                "quarkus.datasource.reactive.url", "postgresql://localhost:54322/homeserver",
                "quarkus.datasource.db-kind", "postgresql",
                "quarkus.datasource.username", "verysecretuser",
                "quarkus.datasource.password", "verysecretpassword",
                "mp.messaging.incoming.shelly-status.connector", "smallrye-mqtt",
                "mp.messaging.incoming.shelly-status.topic", "+/status/#",
                "mp.messaging.incoming.shelly-status.host", "localhost",
                "mp.messaging.incoming.shelly-status.port", "1883",
                "mp.messaging.incoming.shelly-status.auto-generated-client-id", "true"
        );
    }

    @Override
    public void stop() {
        containers.stop();
    }
}
