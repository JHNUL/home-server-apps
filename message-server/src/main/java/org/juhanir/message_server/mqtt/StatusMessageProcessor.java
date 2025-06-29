package org.juhanir.message_server.mqtt;

import io.smallrye.mutiny.Uni;
import org.juhanir.domain.sensordata.entity.DeviceStatusMeasurement;

public interface StatusMessageProcessor {

    StatusMessageType getType();

    Uni<Void> process(DeviceStatusMeasurement measurement, String deviceIdentifier);

}
