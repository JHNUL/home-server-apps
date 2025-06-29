package org.juhanir.message_server.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;
import org.juhanir.message_server.repository.DeviceRepository;
import org.juhanir.message_server.repository.DeviceTypeRepository;

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
                .onItem().ifNull().switchTo(() -> deviceTypeRepository.findByName(DeviceTypeName.TEMPERATURE_HUMIDITY_SENSOR)
                        .onItem().ifNull().failWith(() -> new NotFoundException("Device type " + DeviceTypeName.TEMPERATURE_HUMIDITY_SENSOR + " not found."))
                        .onItem().transformToUni(deviceType -> {
                            Device device = new Device()
                                    .setDeviceType(deviceType)
                                    .setIdentifier(deviceIdentifier);
                            return deviceRepository.persist(device);
                        }));
    }

}
