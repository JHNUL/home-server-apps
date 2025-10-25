import { createRoot } from "react-dom/client";
import { ErrorBoundary } from "react-error-boundary";
import { Provider } from "react-redux";
import { App } from "./App";
import { store } from "./app/store";
import "./index.css";
import { KeycloakProvider } from "./utils/KeycloakProvider";
import { ErrorComponent } from "./utils/ErrorComponent";
import { StrictMode } from "react";

const container = document.getElementById("root");

if (container) {
    const root = createRoot(container);

    root.render(
        <ErrorBoundary FallbackComponent={ErrorComponent}>
            <KeycloakProvider>
                <StrictMode>
                    <Provider store={store}>
                        <App />
                    </Provider>
                </StrictMode>
            </KeycloakProvider>
        </ErrorBoundary>,
    );
} else {
    throw new Error(
        "Root element with ID 'root' was not found in the document. Ensure there is a corresponding HTML element with the ID 'root' in your HTML file.",
    );
}
