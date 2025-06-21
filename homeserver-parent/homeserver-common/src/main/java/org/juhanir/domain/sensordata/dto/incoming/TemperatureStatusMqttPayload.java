package org.juhanir.domain.sensordata.dto.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for incoming temperature readings via MQTT.
 * Payload example: {"id": 0,"tC":20.1, "tF":68.3}
 */
public class TemperatureStatusMqttPayload {

    @JsonProperty("id")
    public int componentId;

    @JsonProperty("tC")
    public double valueCelsius;

    @JsonProperty("tF")
    public double valueFahrenheit;

    public int getComponentId() {
        return componentId;
    }

    public TemperatureStatusMqttPayload setComponentId(int componentId) {
        this.componentId = componentId;
        return this;
    }

    public double getValueCelsius() {
        return valueCelsius;
    }

    public TemperatureStatusMqttPayload setValueCelsius(double valueCelsius) {
        this.valueCelsius = valueCelsius;
        return this;
    }

    public double getValueFahrenheit() {
        return valueFahrenheit;
    }

    public TemperatureStatusMqttPayload setValueFahrenheit(double valueFahrenheit) {
        this.valueFahrenheit = valueFahrenheit;
        return this;
    }
}
