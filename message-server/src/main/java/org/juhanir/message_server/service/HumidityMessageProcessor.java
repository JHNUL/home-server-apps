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
import org.juhanir.message_server.mqtt.StatusMessageProcessor;
import org.juhanir.message_server.mqtt.StatusMessageType;
import org.juhanir.message_server.repository.HumidityRepository;

@ApplicationScoped
public class HumidityMessageProcessor implements StatusMessageProcessor {

    private final ObjectMapper mapper;
    private final HumidityRepository humidityRepository;
    private final EventBus eventBus;
    private final DeviceService deviceService;

    @Inject
    public HumidityMessageProcessor(
            ObjectMapper mapper,
            HumidityRepository humidityRepository,
            EventBus eventBus,
            DeviceService deviceService) {
        this.mapper = mapper;
        this.humidityRepository = humidityRepository;
        this.eventBus = eventBus;
        this.deviceService = deviceService;
    }

    @Override
    public StatusMessageType getType() {
        return StatusMessageType.HUMIDITY;
    }

    @Override
    public Uni<Void> process(String payload, String deviceIdentifier) {
        Uni<HumidityStatus> hStatus = Uni.createFrom().item(Unchecked.supplier(() ->
                HumidityStatus.fromMqttPayload(mapper.readValue(payload, HumidityStatusMqttPayload.class))
        ));
        Uni<Device> deviceUni = deviceService.findOrCreateDevice(deviceIdentifier);
        return Uni.combine()
                .all()
                .unis(hStatus, deviceUni)
                .asTuple()
                .onItem().transformToUni(tuple -> {
                    tuple.getItem1().setDevice(tuple.getItem2());
                    return humidityRepository.persist(tuple.getItem1());
                })
                .onItem().transform(ts -> eventBus.send("message", String.valueOf(ts.getId())))
                .replaceWithVoid();
    }
}
