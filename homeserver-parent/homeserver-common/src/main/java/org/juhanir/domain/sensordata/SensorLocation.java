package org.juhanir.domain.sensordata;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import org.juhanir.domain.BaseEntity;

import java.time.Instant;

@Entity
public class SensorLocation extends BaseEntity {

    @NotNull
    private String description;

    @NotNull
    private Instant createdAt;

    private Instant modifiedAt;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public String toString() {
        return "SensorLocation{" +
                "description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}
