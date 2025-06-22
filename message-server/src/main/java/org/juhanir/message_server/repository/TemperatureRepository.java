package org.juhanir.message_server.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;

import java.util.List;

@ApplicationScoped
public class TemperatureRepository implements PanacheRepository<TemperatureStatus> {

    public Uni<List<TemperatureStatus>> findByDeviceId(long deviceId) {
        return find("device.id", deviceId).list();
    }

}
