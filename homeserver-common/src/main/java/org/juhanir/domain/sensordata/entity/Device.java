package org.juhanir.domain.sensordata.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "device", schema = "sensor")
public class Device extends BaseEntity {

    /**
     * Device identifier, should be unique
     */
    @NotNull
    @Column(name = "identifier")
    private String identifier;

    /**
     * Type of the device; e.g. temperature sensor
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_type", nullable = false)
    private DeviceType deviceType;

    /**
     * When the device was created.
     */
    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * When the device last sent some message.
     */
    @NotNull
    @Column(name = "latest_communication")
    private Instant latestCommunication;

    /**
     * The temperature readings belonging to this device.
     */
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<TemperatureStatus> temperatureStatuses;

    /**
     * The humidity readings belonging to this device.
     */
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<HumidityStatus> humidityStatuses;

    public String getIdentifier() {
        return identifier;
    }

    public Device setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public Device setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public List<TemperatureStatus> getTemperatureStatuses() {
        return temperatureStatuses;
    }

    public Device setTemperatureStatuses(List<TemperatureStatus> temperatureStatuses) {
        this.temperatureStatuses = temperatureStatuses;
        return this;
    }

    public List<HumidityStatus> getHumidityStatuses() {
        return humidityStatuses;
    }

    public Device setHumidityStatuses(List<HumidityStatus> humidityStatuses) {
        this.humidityStatuses = humidityStatuses;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Device setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Instant getLatestCommunication() {
        return latestCommunication;
    }

    public Device setLatestCommunication(Instant latestCommunication) {
        this.latestCommunication = latestCommunication;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(identifier, device.identifier) && Objects.equals(deviceType, device.deviceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, deviceType);
    }

    @Override
    public String toString() {
        return "Device{" +
                "identifier='" + identifier + '\'' +
                ", deviceType=" + deviceType +
                ", temperatureStatuses=" + temperatureStatuses +
                ", humidityStatuses=" + humidityStatuses +
                '}';
    }
}
