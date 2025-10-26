import { BrowserRouter, Route, Routes } from "react-router";
import { Device } from "./features/devices/Device";
import { Layout } from "./layout/Layout";

export const App = () => {
    return (
        <BrowserRouter>
            <Layout>
                <Routes>
                    <Route path="/" element={<Device />} />
                </Routes>
            </Layout>
        </BrowserRouter>
    );
};
