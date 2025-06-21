package org.juhanir.message_server.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.juhanir.domain.sensordata.entity.Device;

@ApplicationScoped
public class DeviceRepository implements PanacheRepository<Device> {

    public Uni<Device> findByIdentifier(String identifier) {
        return find("identifier", identifier).firstResult();
    }

}
