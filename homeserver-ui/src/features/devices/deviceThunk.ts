import type { ThunkAction, UnknownAction } from "@reduxjs/toolkit";
import type { RootState } from "../../app/store";
import { setDevices, setDeviceStatus } from "./deviceSlice";
import { getDevices } from "./deviceAPI";

export const fetchDevicesThunk =
    (): ThunkAction<void, RootState, unknown, UnknownAction> => async (dispatch, getState) => {
        dispatch(setDeviceStatus("loading"));
        try {
            const apiUrl = getState().config.appVars.MESSAGE_SERVER_API_URL;
            const { data } = await getDevices(apiUrl);
            dispatch(setDevices(data));
        } catch (error: unknown) {
            console.error(error instanceof Error ? error.message : "AAAA!");
            dispatch(setDeviceStatus("failed"));
        }
    };
