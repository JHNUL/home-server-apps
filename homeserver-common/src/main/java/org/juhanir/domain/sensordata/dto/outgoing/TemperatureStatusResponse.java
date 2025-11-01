package org.juhanir.domain.sensordata.dto.outgoing;

import org.juhanir.domain.sensordata.entity.SignalData;

import java.time.Instant;

/**
 * Data transfer object for outgoing temperature readings via Websocket or REST API.
 */
public record TemperatureStatusResponse(
        Double valueCelsius,
        Double valueFahrenheit,
        Instant measurementTime,
        String deviceIdentifier) {

    public static TemperatureStatusResponse fromTemperatureStatus(SignalData tempStatus) {
        return new TemperatureStatusResponse(
                tempStatus.getTemperatureCelsius(),
                tempStatus.getTemperatureFahrenheit(),
                tempStatus.getMeasurementTime(),
                tempStatus.getDevice().getIdentifier()
        );
    }
}
