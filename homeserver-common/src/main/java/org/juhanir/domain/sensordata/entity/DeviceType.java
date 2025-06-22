package org.juhanir.domain.sensordata.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "device_type", schema = "sensor")
public class DeviceType extends BaseEntity {

    /**
     * The unique name of the device type.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "name", unique = true)
    private DeviceTypeName name;

    public DeviceTypeName getName() {
        return name;
    }

    public DeviceType setName(DeviceTypeName name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DeviceType that = (DeviceType) o;
        return name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "DeviceType{" +
                "name=" + name +
                '}';
    }
}
