package org.juhanir.domain.sensordata;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.juhanir.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "humidity_status", schema = "sensor")
public class HumidityStatus extends BaseEntity {

    /**
     * Devices report their component ids per sensor.
     */
    @NotNull
    @Column(name = "component_id")
    private Integer componentId;

    /**
     * Relative humidity value in percentage.
     */
    @NotNull
    @Column(name = "value")
    private BigDecimal value;

    /**
     * The measurement time.
     */
    @NotNull
    @Column(name = "measurement_time")
    private Instant measurementTime;

    /**
     * Reference to the device the reading came from.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    public Integer getComponentId() {
        return componentId;
    }

    public HumidityStatus setComponentId(Integer componentId) {
        this.componentId = componentId;
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public HumidityStatus setValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public Instant getMeasurementTime() {
        return measurementTime;
    }

    public HumidityStatus setMeasurementTime(Instant measurementTime) {
        this.measurementTime = measurementTime;
        return this;
    }

    public Device getDevice() {
        return device;
    }

    public HumidityStatus setDevice(Device device) {
        this.device = device;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HumidityStatus that = (HumidityStatus) o;
        return Objects.equals(componentId, that.componentId) && Objects.equals(value, that.value) && Objects.equals(measurementTime, that.measurementTime) && Objects.equals(device, that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, value, measurementTime, device);
    }

    @Override
    public String toString() {
        return "HumidityStatus{" +
                "componentId=" + componentId +
                ", value=" + value +
                ", measurementTime=" + measurementTime +
                ", device=" + device +
                '}';
    }
}
