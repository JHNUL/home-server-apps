package org.juhanir.message_server.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.juhanir.domain.sensordata.dto.incoming.HumidityStatusMqttPayload;
import org.juhanir.domain.sensordata.entity.DeviceStatusMeasurement;
import org.juhanir.domain.sensordata.entity.HumidityStatus;

@ApplicationScoped
public class HumidityStatusMapper implements StatusMessageMapper {

    private final ObjectMapper mapper;

    @Inject
    public HumidityStatusMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public StatusMessageType getType() {
        return StatusMessageType.HUMIDITY;
    }

    @Override
    public DeviceStatusMeasurement mapFromMqtt(String payload) throws JsonProcessingException {
        return HumidityStatus.fromMqttPayload(mapper.readValue(payload, HumidityStatusMqttPayload.class));
    }
}
