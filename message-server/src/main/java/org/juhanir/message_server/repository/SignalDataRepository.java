package org.juhanir.message_server.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.juhanir.domain.sensordata.entity.DeviceTimeSeriesDataId;
import org.juhanir.domain.sensordata.entity.SignalData;
import org.juhanir.message_server.rest.api.TimeSeriesQueryParams;
import org.juhanir.message_server.rest.utils.ParseUtil;
import org.juhanir.message_server.rest.utils.SensorDataQueryBuilder;

import java.util.List;

@ApplicationScoped
public class SignalDataRepository implements PanacheRepositoryBase<SignalData, DeviceTimeSeriesDataId> {

    public Uni<List<SignalData>> getMeasurementsFromDevice(String identifier, TimeSeriesQueryParams params) {

        SensorDataQueryBuilder.QueryAndParams query = SensorDataQueryBuilder
                .create()
                .afterTime(params.from, true)
                .and()
                .beforeTime(params.to, true)
                .and()
                .fromDevice(identifier)
                .build();

        Sort order = Sort.ascending("measurementTime");
        if (params.sort != null && params.sort.equalsIgnoreCase("desc")) {
            order = Sort.descending("measurementTime");
        }
        int pageSize = ParseUtil.tryParseInt(params.limit, 100);
        return find(query.query(), order, query.params()).page(Page.ofSize(pageSize)).list();
    }

}
