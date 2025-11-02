package org.juhanir.message_server.message.processor;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.juhanir.domain.sensordata.entity.DeviceStatusMeasurement;
import org.juhanir.domain.sensordata.entity.DeviceTimeSeriesDataId;
import org.juhanir.domain.sensordata.entity.SignalData;
import org.juhanir.message_server.mqtt.StatusMessageType;
import org.juhanir.message_server.repository.SignalDataRepository;
import org.juhanir.message_server.service.DeviceService;

@ApplicationScoped
public class TemperatureMessageProcessor implements StatusMessageProcessor {

    private final SignalDataRepository signalDataStorage;
    private final EventBus eventBus;
    private final DeviceService deviceService;

    @Inject
    public TemperatureMessageProcessor(
            SignalDataRepository signalDataStorage,
            EventBus eventBus,
            DeviceService deviceService) {
        this.signalDataStorage = signalDataStorage;
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
                    SignalData signalData = (SignalData) measurement;
                    signalData.setDevice(device);
                    DeviceTimeSeriesDataId dId = new DeviceTimeSeriesDataId()
                            .setIdentifier(device.getIdentifier())
                            .setMeasurementTime(signalData.getMeasurementTime());
                    signalData.setId(dId);
                    return deviceService.updateDeviceCommunication(device)
                            .onItem()
                            .ignore()
                            .andSwitchTo(() -> signalDataStorage.persist(signalData));
                })
                .onItem().transform(ts -> eventBus.send("message", String.valueOf(ts.getMeasurementTime())))
                .replaceWithVoid();
    }
}
