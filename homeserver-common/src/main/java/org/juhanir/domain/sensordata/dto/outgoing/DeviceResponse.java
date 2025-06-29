package org.juhanir.domain.sensordata.dto.outgoing;

import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;

public record DeviceResponse(long id, String identifier, DeviceTypeName deviceType) {

    public static DeviceResponse fromDevice(Device device) {
        return new DeviceResponse(device.getId(), device.getIdentifier(), device.getDeviceType().getName());
    }

}
