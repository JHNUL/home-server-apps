package org.juhanir.message_server.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.juhanir.domain.sensordata.entity.DeviceType;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;

@ApplicationScoped
public class DeviceTypeRepository implements PanacheRepository<DeviceType> {

    public Uni<DeviceType> findByName(DeviceTypeName name) {
        return find("name", name).firstResult();
    }

}
