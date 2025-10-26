package org.juhanir.domain.sensordata.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.juhanir.domain.sensordata.dto.incoming.TemperatureStatusMqttPayload;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "temperature_status", schema = "sensor")
public class TemperatureStatus implements DeviceStatusMeasurement {

    /**
     * Composite primary key
     */
    @EmbeddedId
    private DeviceTimeSeriesDataId id;

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
    private Double valueCelsius;

    /**
     * The Fahrenheit value of the temperature.
     */
    @NotNull
    @Column(name = "value_fahrenheit")
    private Double valueFahrenheit;

    /**
     * The measurement time.
     * Hibernate requires insertable and updatable false
     * because this field is part of composite primary key.
     */
    @NotNull
    @Column(name = "measurement_time", nullable = false, insertable = false, updatable = false)
    private Instant measurementTime;

    /**
     * Reference to the device the reading came from.
     * Hibernate requires insertable and updatable false
     * because this field is part of composite primary key.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "identifier",
            referencedColumnName = "identifier",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Device device;

    public Integer getComponentId() {
        return componentId;
    }

    public TemperatureStatus setComponentId(Integer componentId) {
        this.componentId = componentId;
        return this;
    }

    public Double getValueCelsius() {
        return valueCelsius;
    }

    public TemperatureStatus setValueCelsius(Double valueCelsius) {
        this.valueCelsius = valueCelsius;
        return this;
    }

    public Double getValueFahrenheit() {
        return valueFahrenheit;
    }

    public TemperatureStatus setValueFahrenheit(Double valueFahrenheit) {
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

    public TemperatureStatus setId(DeviceTimeSeriesDataId id) {
        this.id = id;
        return this;
    }

    public static TemperatureStatus fromMqttPayload(TemperatureStatusMqttPayload payload) {
        return new TemperatureStatus()
                .setComponentId(payload.getComponentId())
                .setMeasurementTime(Instant.now())
                .setValueCelsius(payload.getValueCelsius())
                .setValueFahrenheit(payload.getValueFahrenheit());
    }

    private Integer getDeviceId(Device device) {
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
