import { useEffect, type ReactNode } from "react";
import { useAppDispatch, useAppSelector } from "../app/hooks/hooks";
import type { Browser, OperatingSystem } from "./configSlice";
import {
    selectConfig,
    setAppVars,
    setConfigInitialized,
    setUnreliableUserAgentInfo,
} from "./configSlice";

// Any one of these missing will fallback to an error ui
const allRequiredVariables: string[] = [
    "VITE_KEYCLOAK_URL",
    "VITE_KEYCLOAK_REALM",
    "VITE_KEYCLOAK_CLIENT_ID",
    "VITE_MESSAGE_SERVER_API_URL",
];

/**
 * A gate component. The app is totally broken if config cannot load and we only
 * render children when config is fully loaded.
 */
export const ConfigLoader: React.FC<{ children: ReactNode }> = ({ children }) => {
    const dispatch = useAppDispatch();
    const config = useAppSelector(selectConfig);

    const detectOS = (ua: string): OperatingSystem => {
        if (/windows nt/i.test(ua)) return "Windows";
        if (/mac os x/i.test(ua) && !/like mac os x/i.test(ua)) return "macOS";
        if (/android/i.test(ua)) return "Android";
        if (/iphone|ipad|ipod/i.test(ua)) return "iOS";
        if (/linux/i.test(ua)) return "Linux";
        return "Unknown";
    };

    const detectBrowser = (ua: string): Browser => {
        if (/edg\//i.test(ua)) return "Edge";
        if (/opr\//i.test(ua)) return "Opera";
        if (/vivaldi/i.test(ua)) return "Vivaldi";
        if (/chrome\//i.test(ua)) return "Chrome";
        if (/firefox\//i.test(ua)) return "Firefox";
        if (/safari\//i.test(ua) && /version\//i.test(ua)) return "Safari";
        if (/msie|trident/i.test(ua)) return "Internet Explorer";
        return "Unknown";
    };

    allRequiredVariables.forEach(envVar => {
        if (typeof import.meta.env[envVar] !== "string") {
            throw Error(`Cannot start application, missing variable ${envVar.replace("VITE_", "")}`);
        }
    });

    useEffect(() => {
        const appVars = {
            KEYCLOAK_URL: import.meta.env.VITE_KEYCLOAK_URL as string,
            KEYCLOAK_REALM: import.meta.env.VITE_KEYCLOAK_REALM as string,
            KEYCLOAK_CLIENT_ID: import.meta.env.VITE_KEYCLOAK_CLIENT_ID as string,
            MESSAGE_SERVER_API_URL: import.meta.env.VITE_MESSAGE_SERVER_API_URL as string,
        };
        const unreliableUserAgentInfo = {
            os: detectOS(window.navigator.userAgent),
            browser: detectBrowser(window.navigator.userAgent),
        };
        dispatch(setAppVars(appVars));
        dispatch(setUnreliableUserAgentInfo(unreliableUserAgentInfo));
        dispatch(setConfigInitialized(true));
    }, [dispatch]);

    // Do not render any UI while config is loading.
    // Now this is synchronous so it's no problem. If there is
    // async logic added, then consider some fallback.
    if (!config.initialized) {
        return null;
    }

    console.info(`Config initialized \n${JSON.stringify(config, null, 2)}`)

    return <>{children}</>;
};
