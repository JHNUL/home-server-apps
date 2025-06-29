package org.juhanir.message_server.message.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.juhanir.domain.sensordata.entity.DeviceStatusMeasurement;
import org.juhanir.message_server.mqtt.StatusMessageType;

public interface StatusMessageMapper {

    StatusMessageType getType();

    DeviceStatusMeasurement mapFromMqtt(String payload) throws JsonProcessingException;

}
