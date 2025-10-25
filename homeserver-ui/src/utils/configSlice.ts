import type { PayloadAction } from "@reduxjs/toolkit";
import { createAppSlice } from "../app/createAppSlice";

export type AppEnvironmentVariables = {
    KEYCLOAK_URL: string;
    KEYCLOAK_REALM: string;
    KEYCLOAK_CLIENT_ID: string;
    MESSAGE_SERVER_API_URL: string;
};

export type OperatingSystem = "Linux" | "Windows" | "macOS" | "Android" | "iOS" | "Unknown";
export type Browser =
    | "Edge"
    | "Opera"
    | "Vivaldi"
    | "Chrome"
    | "Firefox"
    | "Safari"
    | "Internet Explorer"
    | "Unknown";

export type UnreliableUserAgentInfo = {
    os: OperatingSystem;
    browser: Browser;
};

type ConfigSliceState = {
    /**
     * Has the configuration been set.
     */
    initialized: boolean;
    /**
     * Set of environment variables for the application
     */
    appVars: AppEnvironmentVariables;
    /**
     * Boolean flag signifying if app was built with NODE_ENV=production.
     * Provided by framework.
     */
    prod: boolean;
    /**
     * The base url the app is being served from.
     * Provided by framework.
     */
    baseUrl: string;
    /**
     * The user agent info the browser is willing to give.
     * This can be spoofed so it's totally unreliable.
     * {@link https://developer.mozilla.org/en-US/docs/Web/API/Navigator/userAgent}
     */
    unreliableUserAgentInfo: UnreliableUserAgentInfo;
};

const initialState: ConfigSliceState = {
    initialized: false,
    appVars: {
        KEYCLOAK_URL: "",
        KEYCLOAK_REALM: "",
        KEYCLOAK_CLIENT_ID: "",
        MESSAGE_SERVER_API_URL: "",
    },
    prod: import.meta.env.PROD,
    baseUrl: import.meta.env.BASE_URL,
    unreliableUserAgentInfo: {
        os: "Unknown",
        browser: "Unknown",
    },
};

// If you are not using async thunks you can use the standalone `createSlice`.
export const configSlice = createAppSlice({
    name: "config",
    // `createSlice` will infer the state type from the `initialState` argument
    initialState,
    // The `reducers` field lets us define reducers and generate associated actions
    reducers: create => ({
        setConfigInitialized: create.reducer((state, action: PayloadAction<boolean>) => {
            state.initialized = action.payload;
        }),
        setAppVars: create.reducer((state, action: PayloadAction<AppEnvironmentVariables>) => {
            state.appVars = action.payload;
        }),
        setUnreliableUserAgentInfo: create.reducer(
            (state, action: PayloadAction<UnreliableUserAgentInfo>) => {
                state.unreliableUserAgentInfo = action.payload;
            },
        ),
    }),
    selectors: {
        selectConfig: config => config,
    },
});

// Action creators are generated for each case reducer function.
export const { setAppVars, setConfigInitialized, setUnreliableUserAgentInfo } = configSlice.actions;

// Selectors returned by `slice.selectors` take the root state as their first argument.
export const { selectConfig } = configSlice.selectors;
