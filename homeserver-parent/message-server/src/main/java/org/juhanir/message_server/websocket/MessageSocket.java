package org.juhanir.message_server.websocket;

import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import static org.juhanir.message_server.Constants.MESSAGE_ENDPOINT_ID;

@WebSocket(path = "/messages/{user}", endpointId = MESSAGE_ENDPOINT_ID)
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
