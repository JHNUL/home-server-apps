package org.juhanir.domain.sensordata.dto.outgoing;

import org.juhanir.domain.sensordata.entity.HumidityStatus;

import java.time.Instant;

/**
 * Data transfer object for outgoing humidity readings via Websocket or REST API.
 */
public record HumidityStatusResponse(
        long id,
        int componentId,
        double value,
        Instant measurementTime,
        long deviceId) {

    public static HumidityStatusResponse fromHumidityStatus(HumidityStatus humidStatus) {
        return new HumidityStatusResponse(
                humidStatus.getId(),
                humidStatus.getComponentId(),
                humidStatus.getValue().doubleValue(),
                humidStatus.getMeasurementTime(),
                humidStatus.getDevice().getId()
        );
    }
}
