package org.juhanir.message_server.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.juhanir.domain.sensordata.TemperatureStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class TemperatureResponse {

    @JsonProperty("id")
    private long id;

    @JsonProperty("unit")
    private TemperatureUnit unit;

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("location")
    private SensorLocation location;

    public TemperatureResponse setId(long id) {
        this.id = id;
        return this;
    }

    public TemperatureResponse setUnit(TemperatureUnit unit) {
        this.unit = unit;
        return this;
    }

    public TemperatureResponse setValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public TemperatureResponse setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public TemperatureResponse setLocation(SensorLocation location) {
        this.location = location;
        return this;
    }

    public static TemperatureResponse from(Temperature temperature) {
        return new TemperatureResponse()
                .setId(temperature.getId())
                .setValue(temperature.getValue())
                .setCreatedAt(temperature.getTimestamp())
                .setLocation(temperature.getLocation())
                .setUnit(temperature.getUnit());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TemperatureResponse that = (TemperatureResponse) o;
        return id == that.id && unit == that.unit && Objects.equals(value, that.value) && Objects.equals(createdAt, that.createdAt) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, unit, value, createdAt, location);
    }

    @Override
    public String toString() {
        return "TemperatureResponse{" +
                "id=" + id +
                ", unit=" + unit +
                ", value=" + value +
                ", createdAt=" + createdAt +
                ", location=" + location +
                '}';
    }
}
