package org.juhanir.message_server.websockets;

import io.quarkus.vertx.ConsumeEvent;
import io.quarkus.websockets.next.OpenConnections;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

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
        LOG.info("Got message via event bus %s".formatted(message));
        openConnections.forEach(conn -> {
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
