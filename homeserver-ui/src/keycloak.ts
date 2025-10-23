import Keycloak from "keycloak-js";
import type { KeycloakInitOptions } from "keycloak-js";

const keycloakVariables: string[] = [
    "VITE_KEYCLOAK_URL",
    "VITE_KEYCLOAK_REALM",
    "VITE_KEYCLOAK_CLIENT_ID",
];

keycloakVariables.forEach(kcVar => {
    console.error(typeof import.meta.env[kcVar]);
    if (typeof import.meta.env[kcVar] !== "string") {
        throw Error("Cannot start application, missing variable " + kcVar);
    }
});

export const keycloak = new Keycloak({
    url: import.meta.env.VITE_KEYCLOAK_URL as string,
    realm: import.meta.env.VITE_KEYCLOAK_REALM as string,
    clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID as string,
});

export const initOptions: KeycloakInitOptions = {
    flow: "standard",
    pkceMethod: "S256",
    checkLoginIframe: true,
    checkLoginIframeInterval: 30,
    enableLogging: true,
    onLoad: "login-required",
};
