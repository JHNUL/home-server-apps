package org.juhanir.message_server.websockets;

import io.quarkus.vertx.ConsumeEvent;
import io.quarkus.websockets.next.*;
import io.smallrye.common.annotation.NonBlocking;
import io.vertx.core.eventbus.Message;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@WebSocket(path = "/messages/{user}")
public class MessageSocket {

    private static final Logger LOG = Logger.getLogger(MessageSocket.class);
    private final WebSocketConnection connection;

    @Inject
    public MessageSocket(WebSocketConnection connection) {
        this.connection = connection;
    }

    @OnOpen
    @NonBlocking
    public void onOpen() {
        String user = connection.pathParam("user");
        LOG.info("User %s joined".formatted(user));
    }

    @OnTextMessage
    @NonBlocking
    public void onMessage(TextMessage message, WebSocketConnection conn) {
        LOG.info("Received message %s from %s".formatted(message, conn.pathParam("user")));
    }

    @OnClose
    @NonBlocking
    public void onClose() {
        String user = connection.pathParam("user");
        LOG.info("User %s left".formatted(user));
    }

    @ConsumeEvent("message")
    public void consume(String msg) {
        LOG.info("Got message via event bus %s".formatted(msg));
    }

    public record TextMessage(String topic, String content) {}

}
