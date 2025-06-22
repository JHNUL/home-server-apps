package org.juhanir.message_server.websocket;

import io.quarkus.vertx.ConsumeEvent;
import io.quarkus.websockets.next.OpenConnections;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import static org.juhanir.message_server.Constants.MESSAGE_ENDPOINT_ID;

@ApplicationScoped
public class Broadcaster {

    private static final Logger LOG = Logger.getLogger(Broadcaster.class);
    private final OpenConnections openConnections;

    @Inject
    public Broadcaster(OpenConnections openConnections) {
        this.openConnections = openConnections;
    }

    @ConsumeEvent("message")
    public void broadcast(String message) {
        LOG.info("Broadcasting message to 'Messages' clients %s".formatted(message));
        openConnections
                .findByEndpointId(MESSAGE_ENDPOINT_ID)
                .forEach(conn -> {
                    conn
                            .broadcast()
                            .sendText(message)
                            .subscribe()
                            .with(
                                    result -> LOG.info("Successfully sent message"),
                                    failure -> LOG.error("Failed to send message")
                            );
                });
    }
}
