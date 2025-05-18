package org.juhanir.domain.sensordata;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import org.juhanir.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Temperature extends BaseEntity {

    @NotNull
    private TemperatureUnit unit;

    @NotNull
    private BigDecimal value;

    @NotNull
    private Instant timestamp;

    @ManyToOne
    private SensorLocation location;

    public TemperatureUnit getUnit() {
        return unit;
    }

    public void setUnit(TemperatureUnit unit) {
        this.unit = unit;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public SensorLocation getLocation() {
        return location;
    }

    public void setLocation(SensorLocation location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Temperature{" +
                "unit=" + unit +
                ", value=" + value +
                ", timestamp=" + timestamp +
                ", location=" + location +
                '}';
    }
}
