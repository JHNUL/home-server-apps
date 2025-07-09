package org.juhanir.message_server;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.util.Map;

public class MessageServerTestResource implements QuarkusTestResourceLifecycleManager {

    private static ComposeContainer containers;

    @Override
    public Map<String, String> start() {
        var mosquittoWaitStrategy = Wait
                .forLogMessage(".*mosquitto version [0-9]{0,1}\\.[0-9]{0,1}\\.[0-9]{0,3} running.*\\n", 1);
        var liquibaseWaitStrategy = Wait
                .forLogMessage(".*Liquibase command 'update' was executed successfully.*\\n", 1);
        containers = new ComposeContainer(new File("src/docker/docker-compose.yml"))
                .waitingFor("mqtt_broker", mosquittoWaitStrategy)
                .waitingFor("liquibase", liquibaseWaitStrategy)
                .withBuild(false);

        containers.start();

        return Map.of();
    }

    @Override
    public void stop() {
        containers.stop();
    }
}
