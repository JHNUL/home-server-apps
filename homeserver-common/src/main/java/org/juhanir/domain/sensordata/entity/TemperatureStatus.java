package org.juhanir.domain.sensordata.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.juhanir.domain.sensordata.dto.incoming.TemperatureStatusMqttPayload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "temperature_status", schema = "sensor")
public class TemperatureStatus extends BaseEntity implements DeviceStatusMeasurement {

    /**
     * Devices report their component ids per sensor.
     */
    @NotNull
    @Column(name = "component_id")
    private Integer componentId;

    /**
     * The Celsius value of the temperature.
     */
    @NotNull
    @Column(name = "value_celsius")
    private BigDecimal valueCelsius;

    /**
     * The Fahrenheit value of the temperature.
     */
    @NotNull
    @Column(name = "value_fahrenheit")
    private BigDecimal valueFahrenheit;

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

    public TemperatureStatus setComponentId(Integer componentId) {
        this.componentId = componentId;
        return this;
    }

    public BigDecimal getValueCelsius() {
        return valueCelsius;
    }

    public TemperatureStatus setValueCelsius(BigDecimal valueCelsius) {
        this.valueCelsius = valueCelsius;
        return this;
    }

    public BigDecimal getValueFahrenheit() {
        return valueFahrenheit;
    }

    public TemperatureStatus setValueFahrenheit(BigDecimal valueFahrenheit) {
        this.valueFahrenheit = valueFahrenheit;
        return this;
    }

    public Instant getMeasurementTime() {
        return measurementTime;
    }

    public TemperatureStatus setMeasurementTime(Instant measurementTime) {
        this.measurementTime = measurementTime;
        return this;
    }

    public Device getDevice() {
        return device;
    }

    public TemperatureStatus setDevice(Device device) {
        this.device = device;
        return this;
    }

    public static TemperatureStatus fromMqttPayload(TemperatureStatusMqttPayload payload) {
        return new TemperatureStatus()
                .setComponentId(payload.getComponentId())
                .setMeasurementTime(Instant.now())
                .setValueCelsius(BigDecimal.valueOf(payload.getValueCelsius()))
                .setValueFahrenheit(BigDecimal.valueOf(payload.getValueFahrenheit()));
    }

    private Long getDeviceId(Device device) {
        return device == null ? null : device.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TemperatureStatus that = (TemperatureStatus) o;
        return Objects.equals(componentId, that.componentId)
                && Objects.equals(valueCelsius, that.valueCelsius)
                && Objects.equals(valueFahrenheit, that.valueFahrenheit)
                && Objects.equals(measurementTime, that.measurementTime)
                && Objects.equals(getDeviceId(device), getDeviceId(that.device));
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, valueCelsius, valueFahrenheit, measurementTime, getDeviceId(device));
    }

    @Override
    public String toString() {
        return "TemperatureStatus{" +
                "componentId=" + componentId +
                ", valueCelsius=" + valueCelsius +
                ", valueFahrenheit=" + valueFahrenheit +
                ", measurementTime=" + measurementTime +
                ", device=" + device +
                '}';
    }
}
