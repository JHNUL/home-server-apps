import { getKeycloak } from "../../utils/keycloakSingleton";
import type { Device } from "./types";

type GenericFetchResponse<T> = {
    data: T;
    errors?: { message: string }[];
};

/**
 * Fetch all devices
 * @returns list of {@link Device}
 */
export const getDevices = async (apiUrl: string) => {
    return get<Device[]>(`${apiUrl}/devices`);
};

async function get<T>(url: string): Promise<GenericFetchResponse<T>> {
    const kc = getKeycloak();
    await kc.updateToken(30);

    if (!kc.token) {
        return Promise.reject(Error("Keycloak client error: No bearer token available."));
    }

    const result = await fetch(url, {
        headers: {
            Authorization: `Bearer ${kc.token}`,
            Accept: "application/json",
        },
        method: "GET",
    });

    if (!result.ok) {
        return Promise.reject(new Error(`Failed to fetch ${String(result.status)}`));
    }

    const data = (await result.json()) as T;
    return { data };
}
