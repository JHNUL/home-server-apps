package org.juhanir.domain.sensordata.dto.outgoing;

import org.juhanir.domain.sensordata.entity.TemperatureStatus;

import java.time.Instant;

/**
 * Data transfer object for outgoing temperature readings via Websocket or REST API.
 */
public record TemperatureStatusResponse(
        int componentId,
        double valueCelsius,
        double valueFahrenheit,
        Instant measurementTime,
        String deviceIdentifier) {

    public static TemperatureStatusResponse fromTemperatureStatus(TemperatureStatus tempStatus) {
        return new TemperatureStatusResponse(
                tempStatus.getComponentId(),
                tempStatus.getValueCelsius(),
                tempStatus.getValueFahrenheit(),
                tempStatus.getMeasurementTime(),
                tempStatus.getDevice().getIdentifier()
        );
    }
}
