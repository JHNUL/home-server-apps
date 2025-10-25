import "./App.css";
import { Counter } from "./features/counter/Counter";

export const App = () => {
    return (
        <div className="App">
            <header className="App-header">
                <Counter />
            </header>
        </div>
    );
};
