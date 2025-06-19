package org.juhanir.domain.sensordata;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import org.juhanir.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class HumidityStatus extends BaseEntity {

    @NotNull
    private Integer componentId;

    @NotNull
    private BigDecimal relativeHumidity;

    public Integer getComponentId() {
        return componentId;
    }

    public HumidityStatus setComponentId(Integer humidityId) {
        this.componentId = humidityId;
        return this;
    }

    public BigDecimal getRelativeHumidity() {
        return relativeHumidity;
    }

    public HumidityStatus setRelativeHumidity(BigDecimal relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HumidityStatus that = (HumidityStatus) o;
        return Objects.equals(componentId, that.componentId) && Objects.equals(relativeHumidity, that.relativeHumidity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, relativeHumidity);
    }

    @Override
    public String toString() {
        return "HumidityStatus{" +
                "componentId=" + componentId +
                ", relativeHumidity=" + relativeHumidity +
                '}';
    }
}
