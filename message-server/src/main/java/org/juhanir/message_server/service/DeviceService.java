package org.juhanir.message_server.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;
import org.juhanir.message_server.repository.DeviceRepository;
import org.juhanir.message_server.repository.DeviceTypeRepository;

import java.time.Instant;

@ApplicationScoped
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceTypeRepository deviceTypeRepository;

    @Inject
    public DeviceService(DeviceRepository deviceRepository, DeviceTypeRepository deviceTypeRepository) {
        this.deviceRepository = deviceRepository;
        this.deviceTypeRepository = deviceTypeRepository;
    }

    public Uni<Device> findOrCreateDevice(String deviceIdentifier) {
        return deviceRepository.findByIdentifier(deviceIdentifier)
                .onItem().ifNotNull().transform(device -> device)
                .onItem().ifNull().switchTo(() ->
                        // Right now only one type of device exists
                        deviceTypeRepository.findByName(DeviceTypeName.TEMPERATURE_HUMIDITY_SENSOR)
                                .onItem().ifNull().failWith(() -> new NotFoundException("Device type " + DeviceTypeName.TEMPERATURE_HUMIDITY_SENSOR + " not found."))
                                .onItem().transformToUni(deviceType -> {
                                    var now = Instant.now();
                                    Device device = new Device()
                                            .setDeviceType(deviceType)
                                            .setIdentifier(deviceIdentifier)
                                            .setCreatedAt(now)
                                            .setLatestCommunication(now);
                                    return deviceRepository.persist(device);
                                }));
    }

    public Uni<Void> updateDeviceCommunication(Device device) {
        device.setLatestCommunication(Instant.now());
        return deviceRepository.persist(device).replaceWithVoid();
    }

}
