import "./App.css";
import { Device } from "./features/devices/Device";

export const App = () => {
    return (
        <div className="App">
            <header className="App-header">
                <Device />
            </header>
        </div>
    );
};
