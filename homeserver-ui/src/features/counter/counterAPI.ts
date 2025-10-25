import type { Device } from "./types";

const API_URL = import.meta.env.VITE_MESSAGE_SERVER_API_URL as string
const DEVICES_URL = `${API_URL}/devices`


type GenericFetchResponse<T> = {
    data: T;
    errors?: { message: string }[];
};

/**
 * Fetch all devices
 * @returns list of {@link Device}
 */
export const getDevices = async (token: string) => {
    return get<Device[]>(DEVICES_URL, token);
};

async function get<T>(url: string, token: string): Promise<GenericFetchResponse<T>> {
    const result = await fetch(url, {
        headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
        },
        method: "GET",
    });

    if (!result.ok) {
        return Promise.reject(new Error(`Failed to fetch ${String(result.status)}`));
    }

    return (await result.json()) as GenericFetchResponse<T>;
}
