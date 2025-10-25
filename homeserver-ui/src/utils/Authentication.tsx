import type React from "react";
import type { ReactNode } from "react";
import { useEffect, useState } from "react";
import { useErrorBoundary } from "react-error-boundary";
import { useAppSelector } from "../app/hooks/hooks";
import { selectConfig } from "./configSlice";
import { initKeycloak } from "./keycloakSingleton";

/**
 * Authentication component. Unauthenticated users see nothing currently. The
 * Keycloak client requires login on init, so the auth code flow is started
 * immediately. Sets token and user info to state for API calls to use.
 */
export const Authentication: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [ready, setReady] = useState<boolean>(false);
    const { showBoundary } = useErrorBoundary();
    const config = useAppSelector(selectConfig);
    console.log("ready", ready);
    useEffect(() => {
        const doInit = async () => {
            try {
                const conn = {
                    url: config.appVars.KEYCLOAK_URL,
                    realm: config.appVars.KEYCLOAK_REALM,
                    clientId: config.appVars.KEYCLOAK_CLIENT_ID,
                };
                const keycloak = await initKeycloak(conn);
                setReady(keycloak.authenticated);
            } catch (error: unknown) {
                const msg = error instanceof Error ? `: ${error.message}` : "";
                showBoundary(Error(`Keycloak client init error${msg}`));
            }
        };
        void doInit();
    }, [
        showBoundary,
        config.appVars.KEYCLOAK_URL,
        config.appVars.KEYCLOAK_REALM,
        config.appVars.KEYCLOAK_CLIENT_ID,
    ]);
    console.log("ready", ready);
    // TODO: is this needed? If yes, better UI
    if (!ready) return <div>Initializing keycloak client...</div>;

    return <>{children}</>;
};
