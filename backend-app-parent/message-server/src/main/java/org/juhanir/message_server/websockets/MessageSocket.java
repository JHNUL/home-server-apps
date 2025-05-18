package org.juhanir.message_server.websockets;

import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
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
    public void onOpen() {
        String user = connection.pathParam("user");
        LOG.info("User %s joined".formatted(user));
    }

    @OnClose
    public void onClose() {
        String user = connection.pathParam("user");
        LOG.info("User %s left".formatted(user));
    }

}
