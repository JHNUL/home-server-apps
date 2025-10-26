import type { PayloadAction } from "@reduxjs/toolkit";
import { createAppSlice } from "../app/createAppSlice";
import type { KeycloakResourceAccess } from "keycloak-js";

export type UserName = {
    familyName?: string;
    givenName?: string;
};

type AuthSliceState = {
    /**
     * Username from given_name and family_name fields.
     */
    userName: UserName;
    /**
     * User email
     */
    email: string;
    /**
     * The realm roles associated with the token. User needs
     * to have at least one role.
     */
    roles: string[];
    /**
     * The resource roles associated with the token. Zero or more.
     */
    resourceRoles: KeycloakResourceAccess;
};

const initialState: AuthSliceState = {
    userName: {
        familyName: "",
        givenName: "",
    },
    email: "",
    roles: [],
    resourceRoles: {},
};

// If you are not using async thunks you can use the standalone `createSlice`.
export const authSlice = createAppSlice({
    name: "auth",
    // `createSlice` will infer the state type from the `initialState` argument
    initialState,
    // The `reducers` field lets us define reducers and generate associated actions
    reducers: create => ({
        setUsername: create.reducer((state, action: PayloadAction<UserName>) => {
            state.userName = action.payload;
        }),
        setEmail: create.reducer((state, action: PayloadAction<string>) => {
            state.email = action.payload;
        }),
        setRoles: create.reducer((state, action: PayloadAction<string[]>) => {
            state.roles = action.payload;
        }),
        setResourceRoles: create.reducer((state, action: PayloadAction<KeycloakResourceAccess>) => {
            state.resourceRoles = action.payload;
        }),
    }),
    selectors: {
        selectUsername: auth => auth.userName,
        selectEmail: auth => auth.email,
        selectRoles: auth => auth.roles,
        selectResourceRoles: auth => auth.resourceRoles,
    },
});

// Action creators are generated for each case reducer function.
export const { setEmail, setResourceRoles, setRoles, setUsername } = authSlice.actions;

// Selectors returned by `slice.selectors` take the root state as their first argument.
export const { selectEmail, selectResourceRoles, selectRoles, selectUsername } =
    authSlice.selectors;
