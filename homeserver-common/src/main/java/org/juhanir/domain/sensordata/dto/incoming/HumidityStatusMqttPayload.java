package org.juhanir.domain.sensordata.dto.incoming;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for incoming humidity readings via MQTT.
 * Payload example: {"id": 0,"rh":44.1}
 */
public class HumidityStatusMqttPayload {

    public int componentId;
    public double value;

    @JsonCreator
    public HumidityStatusMqttPayload(
            @JsonProperty(value = "id", required = true) Integer componentId,
            @JsonProperty(value = "rh", required = true) Double value
    ) {
        this.componentId = componentId;
        this.value = value;
    }

    public int getComponentId() {
        return componentId;
    }

    public double getValue() {
        return value;
    }

}
