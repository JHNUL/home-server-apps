import { createAppSlice } from "../../app/createAppSlice";
import { getDevices } from "./counterAPI";
import type { Device } from "./types";

export type CounterSliceState = {
    devices: Device[];
    status: "idle" | "loading" | "failed";
};

const initialState: CounterSliceState = {
    devices: [],
    status: "idle",
};

// If you are not using async thunks you can use the standalone `createSlice`.
export const counterSlice = createAppSlice({
    name: "counter",
    // `createSlice` will infer the state type from the `initialState` argument
    initialState,
    // The `reducers` field lets us define reducers and generate associated actions
    reducers: create => ({
        // // Use the `PayloadAction` type to declare the contents of `action.payload`
        // incrementByAmount: create.reducer((state, action: PayloadAction<number>) => {
        //     state.value += action.payload;
        // }),
        fetchDevices: create.asyncThunk(
            async (token: string) => {
                const response = await getDevices(token);
                return response.data;
            },
            {
                pending: state => {
                    state.status = "loading";
                },
                fulfilled: (state, action) => {
                    state.status = "idle";
                    state.devices = action.payload;
                },
                rejected: state => {
                    state.status = "failed";
                },
            },
        ),
    }),
    // You can define your selectors here. These selectors receive the slice
    // state as their first argument.
    selectors: {
        selectDevices: counter => counter.devices,
        selectStatus: counter => counter.status,
    },
});

// Action creators are generated for each case reducer function.
export const { fetchDevices } = counterSlice.actions;

// Selectors returned by `slice.selectors` take the root state as their first argument.
export const { selectDevices, selectStatus } = counterSlice.selectors;

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
