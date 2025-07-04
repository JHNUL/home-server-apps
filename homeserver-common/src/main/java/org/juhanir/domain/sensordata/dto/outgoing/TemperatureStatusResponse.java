package org.juhanir.domain.sensordata.dto.outgoing;

import org.juhanir.domain.sensordata.entity.TemperatureStatus;

import java.time.Instant;

/**
 * Data transfer object for outgoing temperature readings via Websocket or REST API.
 */
public record TemperatureStatusResponse(
        long id,
        int componentId,
        double valueCelsius,
        double valueFahrenheit,
        Instant measurementTime,
        String deviceIdentifier) {

    public static TemperatureStatusResponse fromTemperatureStatus(TemperatureStatus tempStatus) {
        return new TemperatureStatusResponse(
                tempStatus.getId(),
                tempStatus.getComponentId(),
                tempStatus.getValueCelsius().doubleValue(),
                tempStatus.getValueFahrenheit().doubleValue(),
                tempStatus.getMeasurementTime(),
                tempStatus.getDevice().getIdentifier()
        );
    }
}
