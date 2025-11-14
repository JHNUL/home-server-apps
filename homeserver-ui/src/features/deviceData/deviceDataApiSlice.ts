// Need to use the React-specific entry point to import `createApi`
import { createApi } from "@reduxjs/toolkit/query/react";
import { authenticatedFetchBaseQuery } from "../../common/authentication/authQuery";
import { DeviceSignalData } from "./types";

export const deviceDataApiSlice = createApi({
    baseQuery: authenticatedFetchBaseQuery,
    reducerPath: "deviceDataApi",
    tagTypes: ["DeviceData"],
    endpoints: build => ({
        getDeviceData: build.query<DeviceSignalData[], string>({
            query: (identifier) => ({ url: `signaldata?device=${identifier}` }),
        }),
    }),
});

export const { useGetDeviceDataQuery } = deviceDataApiSlice;
