package org.juhanir.message_server.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;
import org.juhanir.message_server.rest.api.TimeSeriesQueryParams;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TemperatureRepository implements PanacheRepository<TemperatureStatus> {

    public Uni<List<TemperatureStatus>> findByDeviceId(long deviceId, TimeSeriesQueryParams params) {

        StringBuilder query = new StringBuilder("device.id = :deviceId");
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("deviceId", deviceId);

        if (params.from != null && !params.from.isBlank()) {
            query.append(" AND measurementTime >= :from");
            queryParams.put("from", Instant.parse(params.from));
        }

        if (params.to != null && !params.to.isBlank()) {
            query.append(" AND measurementTime <= :to");
            queryParams.put("to", Instant.parse(params.to));
        }

        return find(query.toString(), queryParams).list();
    }

}
