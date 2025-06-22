package org.juhanir.message_server.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.juhanir.domain.sensordata.entity.HumidityStatus;

import java.util.List;

@ApplicationScoped
public class HumidityRepository implements PanacheRepository<HumidityStatus> {

    public Uni<List<HumidityStatus>> findByDeviceId(long deviceId) {
        return find("device.id", deviceId).list();
    }

}
