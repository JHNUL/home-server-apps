package org.juhanir.message_server.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.juhanir.domain.sensordata.entity.HumidityStatus;
import org.juhanir.message_server.rest.api.TimeSeriesQueryParams;
import org.juhanir.message_server.rest.utils.SensorDataQueryBuilder;

import java.util.List;

@ApplicationScoped
public class HumidityRepository implements PanacheRepository<HumidityStatus> {

    public Uni<List<HumidityStatus>> getMeasurementsFromDevice(long deviceId, TimeSeriesQueryParams params) {

        SensorDataQueryBuilder.QueryAndParams query = SensorDataQueryBuilder
                .create()
                .afterTime(params.from, true)
                .and()
                .beforeTime(params.to, true)
                .and()
                .fromDevice(deviceId)
                .build();

        return find(query.query(), query.params()).list();
    }

}
