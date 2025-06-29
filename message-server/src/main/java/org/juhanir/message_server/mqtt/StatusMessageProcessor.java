package org.juhanir.message_server.mqtt;

import io.smallrye.mutiny.Uni;
import org.juhanir.domain.sensordata.entity.Device;

public interface StatusMessageProcessor {

    StatusMessageType getType();
    Uni<Void> process(String messagePayload, Device device);

}
