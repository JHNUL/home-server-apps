package org.juhanir.domain.sensordata.dto.outgoing;

import org.juhanir.domain.sensordata.entity.SignalData;

import java.time.Instant;

public record SignalDataResponse(
        String deviceIdentifier,
        Instant measurementTime,
        Double temperatureCelsius,
        Double temperatureFahrenheit,
        Double relativeHumidity
) {

    public static SignalDataResponse fromSignalData(SignalData data) {
        return new SignalDataResponse(
                data.getDevice().getIdentifier(),
                data.getMeasurementTime(),
                data.getTemperatureCelsius(),
                data.getTemperatureFahrenheit(),
                data.getRelativeHumidity()
        );
    }
}
