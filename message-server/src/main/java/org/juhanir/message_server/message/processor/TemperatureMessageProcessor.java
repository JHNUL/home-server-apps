package org.juhanir.message_server.message.processor;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.juhanir.domain.sensordata.entity.DeviceStatusMeasurement;
import org.juhanir.domain.sensordata.entity.DeviceTimeSeriesDataId;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;
import org.juhanir.message_server.mqtt.StatusMessageType;
import org.juhanir.message_server.repository.TemperatureRepository;
import org.juhanir.message_server.service.DeviceService;

@ApplicationScoped
public class TemperatureMessageProcessor implements StatusMessageProcessor {

    private final TemperatureRepository temperatureRepository;
    private final EventBus eventBus;
    private final DeviceService deviceService;

    @Inject
    public TemperatureMessageProcessor(
            TemperatureRepository temperatureRepository,
            EventBus eventBus,
            DeviceService deviceService) {
        this.temperatureRepository = temperatureRepository;
        this.eventBus = eventBus;
        this.deviceService = deviceService;
    }

    @Override
    public StatusMessageType getType() {
        return StatusMessageType.TEMPERATURE;
    }

    @Override
    public Uni<Void> process(DeviceStatusMeasurement measurement, String deviceIdentifier) {
        return deviceService.findOrCreateDevice(deviceIdentifier)
                .onItem().transformToUni(device -> {
                    TemperatureStatus tStatus = (TemperatureStatus) measurement;
                    tStatus.setDevice(device);
                    DeviceTimeSeriesDataId dId = new DeviceTimeSeriesDataId()
                            .setDeviceId(device.getId())
                            .setMeasurementTime(tStatus.getMeasurementTime());
                    tStatus.setId(dId);
                    return deviceService.updateDeviceCommunication(device)
                            .onItem()
                            .ignore()
                            .andSwitchTo(() -> temperatureRepository.persist(tStatus));
                })
                .onItem().transform(ts -> eventBus.send("message", String.valueOf(ts.getMeasurementTime())))
                .replaceWithVoid();
    }
}
