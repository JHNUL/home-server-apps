package org.juhanir.domain.sensordata.dto.outgoing;

import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;

import java.time.Instant;

public record DeviceResponse(
        long id,
        String identifier,
        DeviceTypeName deviceType,
        Instant createdAt,
        Instant latestCommunication) {

    public static DeviceResponse fromDevice(Device device) {
        return new DeviceResponse(
                device.getId(),
                device.getIdentifier(),
                device.getDeviceType().getName(),
                device.getCreatedAt(),
                device.getLatestCommunication());
    }

}
