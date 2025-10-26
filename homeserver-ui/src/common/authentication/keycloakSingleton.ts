import type { KeycloakConfig } from "keycloak-js";
import Keycloak from "keycloak-js";

let keycloak: Keycloak | null = null;

export const initKeycloak = async (connectionConfig: KeycloakConfig): Promise<Keycloak> => {
    if (keycloak) {
        return keycloak;
    }

    keycloak = new Keycloak({ ...connectionConfig });

    await keycloak.init({
        flow: "standard",
        pkceMethod: "S256",
        checkLoginIframe: true,
        checkLoginIframeInterval: 30,
        enableLogging: true,
        onLoad: "login-required",
    });

    return keycloak;
};

export const getKeycloak = (): Keycloak => {
    if (!keycloak) throw new Error("Keycloak not initialized");
    return keycloak;
};
