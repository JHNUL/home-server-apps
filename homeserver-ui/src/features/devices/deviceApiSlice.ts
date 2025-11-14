// Need to use the React-specific entry point to import `createApi`
import { createApi } from "@reduxjs/toolkit/query/react";
import { authenticatedFetchBaseQuery } from "../../common/authentication/authQuery";
import type { Device } from "./types";

export const deviceApiSlice = createApi({
    baseQuery: authenticatedFetchBaseQuery,
    reducerPath: "deviceApi",
    tagTypes: ["Devices"],
    endpoints: build => ({
        getDevices: build.query<Device[], void>({
            query: () => ({ url: "devices" }),
        }),
        getDevice: build.query<Device, string>({
            query: identifier => ({ url: `devices/${identifier}` }),
        }),
    }),
});

export const { useGetDevicesQuery, useGetDeviceQuery } = deviceApiSlice;
