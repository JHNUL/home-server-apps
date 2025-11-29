import { CartesianGrid, Line, LineChart, Tooltip, XAxis, YAxis } from "recharts";
import { DeviceSignalData } from "../../features/deviceData/types";
import { CELSIUS_UNIT_SYMBOL } from "../constants";

export type LineChartProps = {
    data: DeviceSignalData[];
};

type LineChartData = {
    name: string;
    value: any;
};

export const HMLineChart: React.FC<LineChartProps> = ({ data }) => {
    const mappedData: LineChartData[] = data.map(d => {
        return { name: d.measurementTime, value: d.temperatureCelsius };
    });

    return (
        <div style={{ width: "100%" }}>
            <LineChart
                style={{ width: "100%", maxWidth: "700px", maxHeight: "100vh", aspectRatio: 1.618 }}
                responsive
                data={mappedData}
                margin={{
                    top: 10,
                    right: 30,
                    left: 0,
                    bottom: 0,
                }}
            >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" angle={45} label="time" />
                <YAxis dataKey="value" label={CELSIUS_UNIT_SYMBOL} />
                <Tooltip />
                <Line
                    connectNulls
                    type="monotone"
                    dataKey="value"
                    stroke="#8884d8"
                    fill="#8884d8"
                />
            </LineChart>
        </div>
    );
};
