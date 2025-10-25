type GenericFetchResponse<T> = {
    data: T;
    errors?: { message: string }[];
};

/**
 * Fetch all devices
 * @returns list of {@link Device}
 */
export async function get<T>(token: string): Promise<GenericFetchResponse<T>> {
    const result = await fetch("http://localhost:8080/devices", {
        headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
        },
        method: "GET",
    });

    if (!result.ok) {
        return Promise.reject(new Error(`Failed to fetch ${String(result.status)}`));
    }

    const { data } = (await result.json()) as GenericFetchResponse<T>;

    return { data };
}
