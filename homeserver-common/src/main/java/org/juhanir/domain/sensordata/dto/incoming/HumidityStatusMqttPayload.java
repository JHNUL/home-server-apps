package org.juhanir.domain.sensordata.dto.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for incoming humidity readings via MQTT.
 * Payload example: {"id": 0,"rh":44.1}
 */
public class HumidityStatusMqttPayload {

    @JsonProperty("id")
    public int componentId;

    @JsonProperty("rh")
    public double value;

    public int getComponentId() {
        return componentId;
    }

    public HumidityStatusMqttPayload setComponentId(int componentId) {
        this.componentId = componentId;
        return this;
    }

    public double getValue() {
        return value;
    }

    public HumidityStatusMqttPayload setValue(double value) {
        this.value = value;
        return this;
    }
}
