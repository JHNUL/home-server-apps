import "./App.css";
import { Counter } from "./features/counter/Counter";
import { useKeycloak } from "./utils/KeycloakProvider";

export const App = () => {
    const { username } = useKeycloak();

    const onFetchButtonClick = () => {
        console.log("foooo")
    }

    return (
        <div className="App">
            <header className="App-header">
                <p>Logged in as {username}</p>
                <button onClick={onFetchButtonClick}>Fetch</button>
                <Counter />
            </header>
        </div>
    );
};
