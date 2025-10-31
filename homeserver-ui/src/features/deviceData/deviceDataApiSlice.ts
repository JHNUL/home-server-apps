// Need to use the React-specific entry point to import `createApi`
import { createApi } from "@reduxjs/toolkit/query/react";
import { authenticatedFetchBaseQuery } from "../../common/authentication/authQuery";
import { TemperatureStatus } from "./types";

export const deviceDataApiSlice = createApi({
    baseQuery: authenticatedFetchBaseQuery,
    reducerPath: "deviceDataApi",
    tagTypes: ["DeviceData"],
    endpoints: build => ({
        getDeviceData: build.query<TemperatureStatus[], string>({
            query: (identifier) => ({ url: `devices/${identifier}/temperature` }),
        }),
    }),
});

export const { useGetDeviceDataQuery } = deviceDataApiSlice;
