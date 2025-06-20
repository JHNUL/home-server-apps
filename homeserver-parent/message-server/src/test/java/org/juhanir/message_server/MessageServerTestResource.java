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
                .withExposedService("db-1", 54333, Wait
                        .forLogMessage(".*database system is ready to accept connections.*\\n", 2)
                )
                .withExposedService("broker-1", 1899, Wait
                        .forLogMessage(".*mosquitto version [0-9]{0,1}\\.[0-9]{0,1}\\.[0-9]{0,3} running.*\\n", 1)
                )
                .withBuild(false);

        containers.start();

        return Map.of(
                "quarkus.datasource.reactive.url", "postgresql://localhost:54333/homeserver",
                "quarkus.datasource.db-kind", "postgresql",
                "quarkus.datasource.username", "verysecretuser",
                "quarkus.datasource.password", "verysecretpassword",
                "mp.messaging.incoming.shelly-events.connector", "smallrye-mqtt",
                "mp.messaging.incoming.shelly-events.topic", "+/events/rpc",
                "mp.messaging.incoming.shelly-events.host", "localhost",
                "mp.messaging.incoming.shelly-events.port", "1899",
                "mp.messaging.incoming.shelly-events.auto-generated-client-id", "true"
        );
    }

    @Override
    public void stop() {
        containers.stop();
    }
}
