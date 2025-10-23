package org.juhanir.domain.sensordata.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.juhanir.domain.sensordata.dto.incoming.HumidityStatusMqttPayload;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "humidity_status", schema = "sensor")
public class HumidityStatus implements DeviceStatusMeasurement {

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
     * Relative humidity value in percentage.
     */
    @NotNull
    @Column(name = "value")
    private Double value;

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
    @JoinColumn(name = "device_id", nullable = false, insertable = false, updatable = false)
    private Device device;

    public Integer getComponentId() {
        return componentId;
    }

    public HumidityStatus setComponentId(Integer componentId) {
        this.componentId = componentId;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public HumidityStatus setValue(Double value) {
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

    public HumidityStatus setId(DeviceTimeSeriesDataId id) {
        this.id = id;
        return this;
    }

    public static HumidityStatus fromMqttPayload(HumidityStatusMqttPayload humidityStatusMqttPayload) {
        return new HumidityStatus()
                .setComponentId(humidityStatusMqttPayload.getComponentId())
                .setMeasurementTime(Instant.now())
                .setValue(humidityStatusMqttPayload.getValue());
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
