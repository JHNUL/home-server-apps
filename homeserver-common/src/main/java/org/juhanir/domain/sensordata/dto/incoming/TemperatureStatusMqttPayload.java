package org.juhanir.domain.sensordata.dto.incoming;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for incoming temperature readings via MQTT.
 * Payload example: {"id": 0,"tC":20.1, "tF":68.3}
 */
public class TemperatureStatusMqttPayload {

    public int componentId;
    public double valueCelsius;
    public double valueFahrenheit;

    @JsonCreator
    public TemperatureStatusMqttPayload(
            @JsonProperty(value = "id", required = true) Integer componentId,
            @JsonProperty(value = "tC", required = true) Double valueCelsius,
            @JsonProperty(value = "tF", required = true) Double valueFahrenheit
    ) {
        this.componentId = componentId;
        this.valueCelsius = valueCelsius;
        this.valueFahrenheit = valueFahrenheit;
    }

    public int getComponentId() {
        return componentId;
    }

    public double getValueCelsius() {
        return valueCelsius;
    }

    public double getValueFahrenheit() {
        return valueFahrenheit;
    }

}
