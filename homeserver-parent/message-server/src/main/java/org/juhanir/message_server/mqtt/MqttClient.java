package org.juhanir.message_server.mqtt;

import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MqttClient {

    private static final Logger LOG = Logger.getLogger(MqttClient.class);
    private final EventBus bus;
    // TODO: inject reactive Panache repository and write incoming msg to database

    @Inject
    public MqttClient(EventBus bus) {
        this.bus = bus;
    }

    @Incoming("message")
    public void process(String payload) {
        LOG.info("Got incoming MQTT message: %s".formatted(payload));
        bus.send("message", "AAAAA");
    }
}
