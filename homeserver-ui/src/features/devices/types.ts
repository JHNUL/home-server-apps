export type DeviceTypeName = "TEMPERATURE_HUMIDITY_SENSOR" | "TEMPERATURE_SENSOR"

export type Device = {
    id: number;
    identifier: string;
    deviceType: DeviceTypeName;
    createdAt: string;
    latestCommunication: string;
};

