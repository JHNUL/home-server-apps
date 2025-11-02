package org.juhanir.message_server.rest.resource;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.juhanir.domain.sensordata.dto.outgoing.SignalDataResponse;
import org.juhanir.message_server.repository.SignalDataRepository;
import org.juhanir.message_server.rest.api.SignalDataApi;
import org.juhanir.message_server.rest.api.TimeSeriesQueryParams;

public class SignalDataResource implements SignalDataApi {

    private final SignalDataRepository signalDataStorage;

    @Inject
    public SignalDataResource(SignalDataRepository signalDataStorage) {
        this.signalDataStorage = signalDataStorage;
    }

    @Override
    @WithSession
    public Uni<Response> getSignalData(TimeSeriesQueryParams queryParams) {
        return signalDataStorage.getSignalData(queryParams)
                .map(signals -> signals.stream()
                        .map(SignalDataResponse::fromSignalData)
                        .toList()
                )
                .map(body -> Response.ok(body).build());
    }

}
