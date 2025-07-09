package org.juhanir.domain.sensordata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;

/**
 * Composite id class for all time-series data records.
 */
@Embeddable
public class DeviceTimeSeriesDataId implements Serializable {

    /**
     * Spatial component of the composite primary key of a time-series data record.
     */
    @NotNull
    @Column(name = "device_id")
    private Long deviceId;

    /**
     * Temporal component of the composite primary key of a time-series data record.
     */
    @NotNull
    @Column(name = "measurement_time")
    private Instant measurementTime;

    public DeviceTimeSeriesDataId setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public DeviceTimeSeriesDataId setMeasurementTime(Instant measurementTime) {
        this.measurementTime = measurementTime;
        return this;
    }
}
