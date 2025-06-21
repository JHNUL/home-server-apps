package org.juhanir.message_server.rest.resource;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.juhanir.message_server.mqtt.MqttClient;
import org.juhanir.message_server.repository.TemperatureRepository;
import org.juhanir.message_server.rest.api.TemperatureApi;

import java.util.List;

public class TemperatureResource implements TemperatureApi {

    private static final Logger LOG = Logger.getLogger(MqttClient.class);

    private final TemperatureRepository storage;

    @Inject
    public TemperatureResource(TemperatureRepository storage) {
        this.storage = storage;
    }

    @Override
    @WithSession
    public Uni<Response> getTemperatures() {
        return Uni.createFrom().item(Response.noContent().build());
//        return storage
//                .findAll()
//                .list()
//                .map(temperatures -> {
//                            List<TemperatureResponse> body = temperatures
//                                    .stream()
//                                    .map(TemperatureResponse::from)
//                                    .toList();
//                            return Response.ok(body).build();
//                        }
//                )
//                // TODO: fine-grained exception handling per Exception type
//                .onFailure()
//                .recoverWithItem(throwable -> {
//                    LOG.error("Failed to fetch temperatures", throwable);
//                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//                });
    }
}
