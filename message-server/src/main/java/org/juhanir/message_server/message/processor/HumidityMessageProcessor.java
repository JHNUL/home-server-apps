package org.juhanir.message_server.message.processor;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.juhanir.domain.sensordata.entity.DeviceStatusMeasurement;
import org.juhanir.domain.sensordata.entity.HumidityStatus;
import org.juhanir.message_server.mqtt.StatusMessageType;
import org.juhanir.message_server.repository.HumidityRepository;
import org.juhanir.message_server.service.DeviceService;

@ApplicationScoped
public class HumidityMessageProcessor implements StatusMessageProcessor {

    private final HumidityRepository humidityRepository;
    private final EventBus eventBus;
    private final DeviceService deviceService;

    @Inject
    public HumidityMessageProcessor(
            HumidityRepository humidityRepository,
            EventBus eventBus,
            DeviceService deviceService) {
        this.humidityRepository = humidityRepository;
        this.eventBus = eventBus;
        this.deviceService = deviceService;
    }

    @Override
    public StatusMessageType getType() {
        return StatusMessageType.HUMIDITY;
    }

    @Override
    public Uni<Void> process(DeviceStatusMeasurement measurement, String deviceIdentifier) {
        return deviceService.findOrCreateDevice(deviceIdentifier)
                .onItem().transformToUni(device -> {
                    measurement.setDevice(device);
                    return deviceService.updateDeviceCommunication(device)
                            .onItem()
                            .ignore()
                            .andSwitchTo(() -> humidityRepository.persist((HumidityStatus) measurement));
                })
                .onItem().transform(ts -> eventBus.send("message", String.valueOf(ts.getId())))
                .replaceWithVoid();
    }
}
