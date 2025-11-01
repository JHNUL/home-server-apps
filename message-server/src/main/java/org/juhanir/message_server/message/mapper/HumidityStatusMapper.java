package org.juhanir.message_server.message.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.juhanir.domain.sensordata.dto.incoming.HumidityStatusMqttPayload;
import org.juhanir.domain.sensordata.entity.DeviceStatusMeasurement;
import org.juhanir.domain.sensordata.entity.SignalData;
import org.juhanir.message_server.mqtt.StatusMessageType;

import java.time.Instant;

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
        final HumidityStatusMqttPayload humidityStatus = mapper.readValue(payload, HumidityStatusMqttPayload.class);
        return new SignalData()
                .setRelativeHumidity(humidityStatus.getValue())
                .setMeasurementTime(Instant.now());
    }
}
