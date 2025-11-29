import { JSX } from "react";
import { useParams } from "react-router";
import { HMLineChart } from "../../common/chart/HMLineChart";
import { useGetDeviceDataQuery } from "./deviceDataApiSlice";

export const DeviceData = (): JSX.Element => {
    const { deviceIdentifier } = useParams<{ deviceIdentifier: string }>();
    const { data } = useGetDeviceDataQuery(deviceIdentifier ?? "");

    return (
        <div>
            <div className="container-card">
                <h2 className="text-lg font-semibold mb-4">Signal Data for {deviceIdentifier}</h2>
                {data && <HMLineChart data={data}/>}
            </div>
        </div>
    );
};
