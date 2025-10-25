import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { ErrorBoundary } from "react-error-boundary";
import { Provider } from "react-redux";
import { App } from "./App";
import { store } from "./app/store";
import "./index.css";
import { Authentication } from "./utils/Authentication";
import { ConfigLoader } from "./utils/ConfigLoader";
import { ErrorComponent } from "./utils/ErrorComponent";

const container = document.getElementById("root");

if (container) {
    const root = createRoot(container);

    root.render(
        <StrictMode>
            <ErrorBoundary FallbackComponent={ErrorComponent}>
                <Provider store={store}>
                    <ConfigLoader>
                        <Authentication>
                            <App />
                        </Authentication>
                    </ConfigLoader>
                </Provider>
            </ErrorBoundary>
        </StrictMode>,
    );
} else {
    throw new Error(
        "Root element with ID 'root' was not found in the document. Ensure there is a corresponding HTML element with the ID 'root' in your HTML file.",
    );
}
