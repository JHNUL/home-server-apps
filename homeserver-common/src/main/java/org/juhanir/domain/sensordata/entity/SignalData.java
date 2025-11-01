package org.juhanir.domain.sensordata.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "signal_data", schema = "sensor")
public class SignalData implements DeviceStatusMeasurement {

    /**
     * Composite primary key
     */
    @EmbeddedId
    private DeviceTimeSeriesDataId id;

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

    /**
     * The measurement time.
     * Hibernate requires insertable and updatable false
     * because this field is part of composite primary key.
     */
    @NotNull
    @Column(name = "measurement_time", nullable = false, insertable = false, updatable = false)
    private Instant measurementTime;

    /**
     * Temperature signal (Celsius)
     */
    @Column(name = "temperature_celsius")
    private Double temperatureCelsius;

    /**
     * Temperature signal (Fahrenheit)
     */
    @Column(name = "temperature_fahrenheit")
    private Double temperatureFahrenheit;

    /**
     * Humidity signal
     */
    @Column(name = "relative_humidity")
    private Double relativeHumidity;

    public Instant getMeasurementTime() {
        return measurementTime;
    }

    public SignalData setMeasurementTime(Instant measurementTime) {
        this.measurementTime = measurementTime;
        return this;
    }

    public Device getDevice() {
        return device;
    }

    public SignalData setDevice(Device device) {
        this.device = device;
        return this;
    }

    public SignalData setId(DeviceTimeSeriesDataId id) {
        this.id = id;
        return this;
    }

    public DeviceTimeSeriesDataId getId() {
        return id;
    }

    public Double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public SignalData setTemperatureCelsius(Double temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
        return this;
    }

    public Double getTemperatureFahrenheit() {
        return temperatureFahrenheit;
    }

    public SignalData setTemperatureFahrenheit(Double temperatureFahrenheit) {
        this.temperatureFahrenheit = temperatureFahrenheit;
        return this;
    }

    public Double getRelativeHumidity() {
        return relativeHumidity;
    }

    public SignalData setRelativeHumidity(Double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SignalData that = (SignalData) o;
        return Objects.equals(device, that.device) && Objects.equals(measurementTime, that.measurementTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(device, measurementTime);
    }

    @Override
    public String toString() {
        return "SignalData{" +
                "id=" + id +
                ", measurementTime=" + measurementTime +
                ", temperatureCelsius=" + temperatureCelsius +
                ", temperatureFahrenheit=" + temperatureFahrenheit +
                ", relativeHumidity=" + relativeHumidity +
                '}';
    }
}
