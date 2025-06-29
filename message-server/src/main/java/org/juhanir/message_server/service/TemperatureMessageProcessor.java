package org.juhanir.message_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.juhanir.domain.sensordata.dto.incoming.TemperatureStatusMqttPayload;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;
import org.juhanir.message_server.mqtt.StatusMessageProcessor;
import org.juhanir.message_server.mqtt.StatusMessageType;
import org.juhanir.message_server.repository.TemperatureRepository;

@ApplicationScoped
public class TemperatureMessageProcessor implements StatusMessageProcessor {

    private final ObjectMapper mapper;
    private final TemperatureRepository temperatureRepository;
    private final EventBus eventBus;
    private final DeviceService deviceService;

    @Inject
    public TemperatureMessageProcessor(
            ObjectMapper mapper,
            TemperatureRepository temperatureRepository,
            EventBus eventBus,
            DeviceService deviceService) {
        this.mapper = mapper;
        this.temperatureRepository = temperatureRepository;
        this.eventBus = eventBus;
        this.deviceService = deviceService;
    }

    @Override
    public StatusMessageType getType() {
        return StatusMessageType.TEMPERATURE;
    }

    @Override
    public Uni<Void> process(String payload, String deviceIdentifier) {
        Uni<TemperatureStatus> hStatus = Uni.createFrom().item(Unchecked.supplier(() ->
                TemperatureStatus.fromMqttPayload(mapper.readValue(payload, TemperatureStatusMqttPayload.class))
        ));
        Uni<Device> deviceUni = deviceService.findOrCreateDevice(deviceIdentifier);
        return Uni.combine()
                .all()
                .unis(hStatus, deviceUni)
                .asTuple()
                .onItem().transformToUni(tuple -> {
                    tuple.getItem1().setDevice(tuple.getItem2());
                    return temperatureRepository.persist(tuple.getItem1());
                })
                .onItem().transform(ts -> eventBus.send("message", String.valueOf(ts.getId())))
                .replaceWithVoid();
    }
}
