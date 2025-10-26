import type { JSX } from "react";
import { useAppDispatch } from "../../app/hooks/hooks";
import Table from "../../common/table/Table";
import { TableCell } from "../../common/table/TableCell";
import TableRow from "../../common/table/TableRow";

export const Device = (): JSX.Element => {
    const dispatch = useAppDispatch();
    // automatically dispatch(fetchDevicesThunk());

    return (
        <div className="container-card">
            <h2 className="text-lg font-semibold mb-4">Devices</h2>
            <Table headers={["Identifier", "DeviceType", "Created at", "Last online"]}>
                {devices.map(device => (
                    <TableRow
                        key={device.id}
                        onClick={() => {
                            console.log("Clicked", device.identifier);
                        }}
                    >
                        <TableCell>{device.identifier}</TableCell>
                        <TableCell>{device.deviceType}</TableCell>
                        <TableCell>{device.createdAt}</TableCell>
                        <TableCell>{device.latestCommunication}</TableCell>
                    </TableRow>
                ))}
            </Table>
        </div>
    );
};

const devices = [
    {
        id: 1,
        identifier: "shellyfoo1",
        deviceType: "TEMPERATURE_HUMIDITY_SENSOR",
        createdAt: "2025-10-25T08:59:00.682318Z",
        latestCommunication: "2025-10-26T13:13:00.079028Z",
    },
    {
        id: 2,
        identifier: "plugbar2",
        deviceType: "SMART_PLUG",
        createdAt: "2025-10-25T09:10:12.182001Z",
        latestCommunication: "2025-10-26T12:57:45.920013Z",
    },
    {
        id: 3,
        identifier: "doorwatcher3",
        deviceType: "CONTACT_SENSOR",
        createdAt: "2025-10-25T09:33:49.449801Z",
        latestCommunication: "2025-10-26T13:00:23.118009Z",
    },
    {
        id: 4,
        identifier: "thermohub4",
        deviceType: "THERMOSTAT",
        createdAt: "2025-10-25T09:40:04.228210Z",
        latestCommunication: "2025-10-26T13:08:12.772020Z",
    },
    {
        id: 5,
        identifier: "garagebrain5",
        deviceType: "GARAGE_DOOR_CONTROLLER",
        createdAt: "2025-10-25T10:01:19.682110Z",
        latestCommunication: "2025-10-26T13:11:55.372812Z",
    },
    {
        id: 6,
        identifier: "camnode6",
        deviceType: "SECURITY_CAMERA",
        createdAt: "2025-10-25T10:24:33.202014Z",
        latestCommunication: "2025-10-26T13:10:32.991002Z",
    },
    {
        id: 7,
        identifier: "gardenflow7",
        deviceType: "WATER_FLOW_SENSOR",
        createdAt: "2025-10-25T10:55:44.182301Z",
        latestCommunication: "2025-10-26T13:06:41.081902Z",
    },
    {
        id: 8,
        identifier: "motionnode8",
        deviceType: "MOTION_SENSOR",
        createdAt: "2025-10-25T11:12:18.981022Z",
        latestCommunication: "2025-10-26T13:09:29.118818Z",
    },
];
