import type { JSX } from "react";
import { useAppDispatch } from "../../app/hooks/hooks";
import { fetchDevicesThunk } from "./deviceThunk";
import { Button } from "../../common/Button";

export const Device = (): JSX.Element => {
    const dispatch = useAppDispatch();

    return (
            <Button
                aria-label="Fetch devices"
                onClick={() => {
                    dispatch(fetchDevicesThunk());
                }}
            >
                Get
            </Button>
    );
};
