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
        containers = new ComposeContainer(new File("src/test/resources/docker-compose-test.yml"))
                .withExposedService("db-1", 54333, Wait
                        .forLogMessage(".*database system is ready to accept connections.*\\n", 1)
                )
                .withExposedService("broker-1", 1899, Wait
                        .forLogMessage(".*mosquitto version [0-9]{0,1}\\.[0-9]{0,1}\\.[0-9]{0,3} running.*\\n", 1)
                )
                .withBuild(false);

        containers.start();

        return Map.of(
                "quarkus.datasource.reactive.url", "postgresql://localhost:54333/homeserver",
                "mp.messaging.incoming.message.connector", "smallrye-mqtt",
                "mp.messaging.incoming.message.topic", "message",
                "mp.messaging.incoming.message.host", "localhost",
                "mp.messaging.incoming.message.port", "1899",
                "mp.messaging.incoming.message.auto-generated-client-id", "true"
        );
    }

    @Override
    public void stop() {
        containers.stop();
    }
}
