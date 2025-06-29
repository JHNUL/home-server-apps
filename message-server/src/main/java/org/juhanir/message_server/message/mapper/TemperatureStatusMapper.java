package org.juhanir.message_server.message.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.juhanir.domain.sensordata.dto.incoming.TemperatureStatusMqttPayload;
import org.juhanir.domain.sensordata.entity.DeviceStatusMeasurement;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;
import org.juhanir.message_server.mqtt.StatusMessageType;

@ApplicationScoped
public class TemperatureStatusMapper implements StatusMessageMapper {

    private final ObjectMapper mapper;

    @Inject
    public TemperatureStatusMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public StatusMessageType getType() {
        return StatusMessageType.TEMPERATURE;
    }

    @Override
    public DeviceStatusMeasurement mapFromMqtt(String payload) throws JsonProcessingException {
        return TemperatureStatus.fromMqttPayload(mapper.readValue(payload, TemperatureStatusMqttPayload.class));
    }
}
