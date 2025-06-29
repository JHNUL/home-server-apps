package org.juhanir.message_server.message.processor;

import io.smallrye.mutiny.Uni;
import org.juhanir.domain.sensordata.entity.DeviceStatusMeasurement;
import org.juhanir.message_server.mqtt.StatusMessageType;

public interface StatusMessageProcessor {

    StatusMessageType getType();

    Uni<Void> process(DeviceStatusMeasurement measurement, String deviceIdentifier);

}
