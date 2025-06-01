package org.juhanir.message_server.rest.resource;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.juhanir.message_server.repository.TemperatureRepository;
import org.juhanir.message_server.rest.api.TemperatureApi;
import org.juhanir.message_server.rest.dto.TemperatureResponse;

import java.util.List;

public class TemperatureResource implements TemperatureApi {

    private final TemperatureRepository storage;

    @Inject
    public TemperatureResource(TemperatureRepository storage) {
        this.storage = storage;
    }

    @Override
    @WithSession
    public Uni<List<TemperatureResponse>> getTemperatures() {
        return storage
                .findAll()
                .list()
                .map(temperatures -> temperatures
                        .stream()
                        .map(TemperatureResponse::from)
                        .toList()
                );
    }
}
