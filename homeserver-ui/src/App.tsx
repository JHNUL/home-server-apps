import "./App.css";
import { useKeycloak } from "./utils/KeycloakProvider";

export const App = () => {
    const { username } = useKeycloak();

    return (
        <div className="App">
            <header className="App-header">
                <p>Logged in as {username}</p>
            </header>
        </div>
    );
};
