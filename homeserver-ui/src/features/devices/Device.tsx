import type { JSX } from "react";
import Table from "../../common/table/Table";
import { TableCell } from "../../common/table/TableCell";
import TableRow from "../../common/table/TableRow";
import { useGetDevicesQuery } from "./deviceApiSlice";
import { Device as IoTDevice } from "./types";
import { useNavigate } from "react-router";

export const Device = (): JSX.Element => {
    const { data, isError, isLoading, isSuccess } = useGetDevicesQuery();
    const navigate = useNavigate();

    const renderTable = (devices: IoTDevice[]) => {
        return (
            <div className="container-card">
                <h2 className="text-lg font-semibold mb-4">Devices</h2>
                <Table headers={["Identifier", "Device Type", "Created", "Last Online"]}>
                    {devices.map(device => (
                        <TableRow
                            key={device.id}
                            onClick={() => {
                                navigate(`/device/${device.identifier}`);
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

    if (isError) {
        console.error("ERROR");
    }

    if (isLoading) {
        console.log("LOADING");
    }

    if (isSuccess) {
        return renderTable(data);
    }

    return renderTable([]);
};
