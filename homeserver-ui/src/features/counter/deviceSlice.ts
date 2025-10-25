import type { PayloadAction } from "@reduxjs/toolkit";
import { createAppSlice } from "../../app/createAppSlice";
import type { Device } from "../../features/counter/types";

export type LoadingState = "idle" | "loading" | "failed";

export type CounterSliceState = {
    devices: Device[];
    status: LoadingState;
};

const initialState: CounterSliceState = {
    devices: [],
    status: "idle",
};

// If you are not using async thunks you can use the standalone `createSlice`.
export const deviceSlice = createAppSlice({
    name: "counter",
    // `createSlice` will infer the state type from the `initialState` argument
    initialState,
    // The `reducers` field lets us define reducers and generate associated actions
    reducers: create => ({
        // // Use the `PayloadAction` type to declare the contents of `action.payload`
        // incrementByAmount: create.reducer((state, action: PayloadAction<number>) => {
        //     state.value += action.payload;
        // }),
        setDeviceStatus: create.reducer((state, action: PayloadAction<LoadingState>) => {
            state.status = action.payload;
        }),
        setDevices: create.reducer((state, action: PayloadAction<Device[]>) => {
            state.status = "idle";
            state.devices = action.payload;
        }),
    }),
    // You can define your selectors here. These selectors receive the slice
    // state as their first argument.
    selectors: {
        selectDevices: counter => counter.devices,
        selectStatus: counter => counter.status,
    },
});

// Action creators are generated for each case reducer function.
export const { setDeviceStatus, setDevices } = deviceSlice.actions;

// Selectors returned by `slice.selectors` take the root state as their first argument.
export const { selectDevices, selectStatus } = deviceSlice.selectors;

// We can also write thunks by hand, which may contain both sync and async logic.
// Here's an example of conditionally dispatching actions based on current state.
// export const incrementIfOdd =
//     (amount: number): AppThunk =>
//     (dispatch, getState) => {
//         const currentValue = selectCount(getState());

//         if (currentValue % 2 === 1 || currentValue % 2 === -1) {
//             dispatch(incrementByAmount(amount));
//         }
//     };
