import type { ReactNode } from "react";
import type React from "react";
import { createContext, useContext, useEffect, useState } from "react";
import { useErrorBoundary } from "react-error-boundary";
import Keycloak from "keycloak-js";

const keycloakVariables: string[] = [
    "VITE_KEYCLOAK_URL",
    "VITE_KEYCLOAK_REALM",
    "VITE_KEYCLOAK_CLIENT_ID",
];

type KeycloakContextValue = {
    keycloak: Keycloak | null;
    token?: string;
    username?: string;
    logout: () => Promise<void> | undefined;
};

const KeycloakContext = createContext<KeycloakContextValue | null>(null);

export const useKeycloak = (): KeycloakContextValue => {
    const ctx = useContext(KeycloakContext);
    if (!ctx) {
        throw new Error("useKeycloak must be used within a KeycloakProvider");
    }
    return ctx;
};

export const KeycloakProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [keycloak, setKeycloak] = useState<Keycloak | null>(null);
    const { showBoundary } = useErrorBoundary();

    useEffect(() => {
        // Do this here to throw inside ErrorBoundary
        keycloakVariables.forEach(kcVar => {
            if (typeof import.meta.env[kcVar] !== "string") {
                throw Error("Cannot start application, missing variable " + kcVar);
            }
        });

        const doInit = async () => {
            const client = new Keycloak({
                url: import.meta.env.VITE_KEYCLOAK_URL as string,
                realm: import.meta.env.VITE_KEYCLOAK_REALM as string,
                clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID as string,
            });
            await client.init({
                flow: "standard",
                pkceMethod: "S256",
                checkLoginIframe: true,
                checkLoginIframeInterval: 30,
                enableLogging: true,
                onLoad: "login-required",
            });
            setKeycloak(client);
        };

        // eslint-disable-next-line @typescript-eslint/use-unknown-in-catch-callback-variable
        doInit().catch((err: Error) => {
            showBoundary(Error("Keycloak client init error :" + err.message));
        });
    }, [showBoundary]);

    const contextValue: KeycloakContextValue = {
        keycloak: keycloak,
        token: keycloak?.token,
        username: keycloak?.tokenParsed?.preferred_username as string,
        logout: () => keycloak?.logout(),
    };

    if (!keycloak?.authenticated) return <div>Authenticating...</div>;

    return <KeycloakContext.Provider value={contextValue}>{children}</KeycloakContext.Provider>;
};
