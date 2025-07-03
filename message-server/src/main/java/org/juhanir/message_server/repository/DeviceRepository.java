package org.juhanir.message_server.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.juhanir.domain.sensordata.entity.Device;

import java.util.List;

@ApplicationScoped
public class DeviceRepository implements PanacheRepository<Device> {

    public Uni<Device> findByIdentifier(String identifier) {
        return find("identifier", identifier).firstResult();
    }

    public Uni<Device> findByIdentifierWithType(String identifier) {
        return find("select d from Device d join fetch d.deviceType where d.identifier = ?1", identifier).firstResult();
    }

    public Uni<List<Device>> findAllWithType(Sort sort) {
        return find("select d from Device d join fetch d.deviceType", sort).list();
    }

}
