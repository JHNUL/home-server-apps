package org.juhanir.domain.sensordata.dto.outgoing;

import org.juhanir.domain.sensordata.entity.SignalData;

import java.time.Instant;

/**
 * Data transfer object for outgoing humidity readings via Websocket or REST API.
 */
public record HumidityStatusResponse(
        Double value,
        Instant measurementTime,
        String deviceIdentifier) {

    public static HumidityStatusResponse fromHumidityStatus(SignalData humidStatus) {
        return new HumidityStatusResponse(
                humidStatus.getRelativeHumidity(),
                humidStatus.getMeasurementTime(),
                humidStatus.getDevice().getIdentifier()
        );
    }
}
