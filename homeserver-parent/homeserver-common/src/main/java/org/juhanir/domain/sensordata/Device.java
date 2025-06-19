package org.juhanir.domain.sensordata;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import org.juhanir.domain.BaseEntity;

@Entity
public class Device extends BaseEntity {

    /**
     * Device identifier, should be unique
     */
    @NotNull
    private String identifier;

    /**
     * Type of the device; e.g. temperature sensor
     */
    @NotNull
    private DeviceType deviceType;

    /**
     * Unique id coming from the device itself if available
     */
    private String deviceId;

}
