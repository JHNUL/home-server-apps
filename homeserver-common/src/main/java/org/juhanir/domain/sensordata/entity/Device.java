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
     * Device identifier, must be unique
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
     * Is the device disabled
     */
    @NotNull
    @Column(name = "is_disabled")
    private Boolean isDisabled;

    /**
     * The signal data readings belonging to this device.
     */
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<SignalData> signalData;

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

    public List<SignalData> getSignalData() {
        return signalData;
    }

    public Device setSignalData(List<SignalData> signalData) {
        this.signalData = signalData;
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

    public Boolean getDisabled() {
        return isDisabled;
    }

    public Device setDisabled(Boolean disabled) {
        isDisabled = disabled;
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
                ", createdAt=" + createdAt +
                ", latestCommunication=" + latestCommunication +
                ", signalData=" + signalData +
                '}';
    }
}
