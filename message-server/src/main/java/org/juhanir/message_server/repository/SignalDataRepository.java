package org.juhanir.message_server.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.juhanir.domain.sensordata.entity.DeviceTimeSeriesDataId;
import org.juhanir.domain.sensordata.entity.SignalData;
import org.juhanir.message_server.rest.api.TimeSeriesQueryParams;
import org.juhanir.message_server.rest.utils.SignalDataQueryBuilder;

import java.util.List;

@ApplicationScoped
public class SignalDataRepository implements PanacheRepositoryBase<SignalData, DeviceTimeSeriesDataId> {

    public Uni<List<SignalData>> getSignalData(TimeSeriesQueryParams params) {

        SignalDataQueryBuilder.QueryAndParams query = SignalDataQueryBuilder
                .create()
                .afterTime(params.from, true)
                .and()
                .beforeTime(params.to, true)
                .and()
                .fromDevices(params.deviceIdentifiers)
                .build();

        Sort order = Sort.descending("measurementTime");
        if (params.sort != null && params.sort.equalsIgnoreCase("asc")) {
            order = Sort.ascending("measurementTime");
        }
        return find(query.query(), order, query.params()).range(params.offset, params.offset + params.limit - 1).list();
    }

}
