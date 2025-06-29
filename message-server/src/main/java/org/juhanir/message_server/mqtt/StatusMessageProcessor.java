package org.juhanir.message_server.mqtt;

import io.smallrye.mutiny.Uni;

public interface StatusMessageProcessor {

    StatusMessageType getType();

    Uni<Void> process(String messagePayload, String deviceIdentifier);

}
