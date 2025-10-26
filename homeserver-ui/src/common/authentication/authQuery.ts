import { FetchArgs, BaseQueryApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { RootState } from "../../app/store";
import { getKeycloak } from "./keycloakSingleton";

export const authenticatedFetchBaseQuery = (args: FetchArgs, api: BaseQueryApi) => {
    const state = api.getState() as RootState;
    const baseUrl = state.config.appVars.MESSAGE_SERVER_API_URL;

    const rawBaseQuery = fetchBaseQuery({
        baseUrl,
        headers: {
            Accept: "application/json",
        },
        prepareHeaders: async headers => {
            try {
                const kc = getKeycloak();
                await kc.updateToken(30);
                if (kc.token) {
                    headers.set("Authorization", `Bearer ${kc.token}`);
                }
            } catch (error: unknown) {
                console.error(
                    `Failed to set headers ${error instanceof Error ? error.message : ""}`,
                );
            }
            return headers;
        },
    });

    return rawBaseQuery(args, api, {});
};
