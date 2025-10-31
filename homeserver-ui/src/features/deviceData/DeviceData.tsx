import { JSX } from "react";
import { useGetDeviceDataQuery } from "./deviceDataApiSlice";
import { useParams } from "react-router";
import Table from "../../common/table/Table";
import TableRow from "../../common/table/TableRow";
import { TableCell } from "../../common/table/TableCell";

export const DeviceData = (): JSX.Element => {
    const { deviceIdentifier } = useParams<{ deviceIdentifier: string }>();
    const { data } = useGetDeviceDataQuery(deviceIdentifier ?? "");
    return (
        <div>
            <div className="container-card">
                <h2 className="text-lg font-semibold mb-4">
                    {deviceIdentifier} temperature readings
                </h2>
                <Table headers={["Time", "Celsius", "Fahrenheit"]}>
                    {data?.map(status => (
                        <TableRow key={String(status.measurementTime) + status.deviceIdentifier}>
                            <TableCell>{status.measurementTime}</TableCell>
                            <TableCell>{status.valueCelsius}</TableCell>
                            <TableCell>{status.valueFahrenheit}</TableCell>
                        </TableRow>
                    ))}
                </Table>
            </div>
        </div>
    );
};
