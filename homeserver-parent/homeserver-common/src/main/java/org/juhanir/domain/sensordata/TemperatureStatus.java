package org.juhanir.domain.sensordata;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import org.juhanir.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
public class TemperatureStatus extends BaseEntity {

    @NotNull
    private Integer componentId;

    @NotNull
    private BigDecimal temperatureCelsius;

    @NotNull
    private BigDecimal temperatureFahrenheit;

    @NotNull
    private Instant timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    private Device device;

    public Integer getComponentId() {
        return componentId;
    }

    public TemperatureStatus setComponentId(Integer componentId) {
        this.componentId = componentId;
        return this;
    }

    public BigDecimal getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public TemperatureStatus setTemperatureCelsius(BigDecimal temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
        return this;
    }

    public BigDecimal getTemperatureFahrenheit() {
        return temperatureFahrenheit;
    }

    public TemperatureStatus setTemperatureFahrenheit(BigDecimal temperatureFahrenheit) {
        this.temperatureFahrenheit = temperatureFahrenheit;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public TemperatureStatus setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TemperatureStatus that = (TemperatureStatus) o;
        return Objects.equals(componentId, that.componentId) && Objects.equals(temperatureCelsius, that.temperatureCelsius) && Objects.equals(temperatureFahrenheit, that.temperatureFahrenheit) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, temperatureCelsius, temperatureFahrenheit, timestamp);
    }

    @Override
    public String toString() {
        return "TemperatureStatus{" +
                "componentId=" + componentId +
                ", temperatureCelsius=" + temperatureCelsius +
                ", temperatureFahrenheit=" + temperatureFahrenheit +
                ", timestamp=" + timestamp +
                '}';
    }
}
