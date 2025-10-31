import { BrowserRouter, Route, Routes } from "react-router";
import { Device } from "./features/devices/Device";
import { Layout } from "./layout/Layout";
import { DeviceData } from "./features/deviceData/DeviceData";

export const App = () => {
    return (
        <BrowserRouter>
            <Layout>
                <Routes>
                    <Route path="/" element={<Device />} />
                    <Route path="/device/:deviceIdentifier" element={<DeviceData />} />
                </Routes>
            </Layout>
        </BrowserRouter>
    );
};
