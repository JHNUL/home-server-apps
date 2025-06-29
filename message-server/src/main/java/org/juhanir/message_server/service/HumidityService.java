package org.juhanir.message_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.juhanir.domain.sensordata.dto.incoming.HumidityStatusMqttPayload;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.HumidityStatus;
import org.juhanir.message_server.repository.HumidityRepository;

@ApplicationScoped
public class HumidityService {

    private final ObjectMapper mapper;
    private final HumidityRepository humidityRepository;
    private final EventBus eventBus;

    @Inject
    public HumidityService(ObjectMapper mapper, HumidityRepository humidityRepository, EventBus eventBus) {
        this.mapper = mapper;
        this.humidityRepository = humidityRepository;
        this.eventBus = eventBus;
    }

    public Uni<Void> processHumidity(String payload, Device device) {
        return Uni.createFrom().item(Unchecked.supplier(() ->
                        HumidityStatus.fromMqttPayload(mapper.readValue(payload, HumidityStatusMqttPayload.class))
                ))
                .onItem().invoke(hs -> hs.setDevice(device))
                .onItem().transformToUni(humidityRepository::persistHumidity)
                .onItem().transform(ts -> eventBus.send("message", String.valueOf(ts.getId())))
                .replaceWithVoid();
    }
}
