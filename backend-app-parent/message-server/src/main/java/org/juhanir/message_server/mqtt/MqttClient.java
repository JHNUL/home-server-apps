package org.juhanir.message_server.mqtt;

import io.quarkus.websockets.next.OpenConnections;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MqttClient {

    private static final Logger LOG = Logger.getLogger(MqttClient.class);
    private final OpenConnections openWebSockets;

    @Inject
    public MqttClient(OpenConnections openWebSockets) {
        this.openWebSockets = openWebSockets;
    }

    @Incoming("message")
    public void process(String payload) {
        LOG.info("Got incoming MQTT message: %s".formatted(payload));
    }
}
