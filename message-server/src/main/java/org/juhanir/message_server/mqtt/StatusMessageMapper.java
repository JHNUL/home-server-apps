package org.juhanir.message_server.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.smallrye.mutiny.Uni;
import org.juhanir.domain.sensordata.entity.DeviceStatusMeasurement;

public interface StatusMessageMapper {

    StatusMessageType getType();

    DeviceStatusMeasurement mapFromMqtt(String payload) throws JsonProcessingException;

}
