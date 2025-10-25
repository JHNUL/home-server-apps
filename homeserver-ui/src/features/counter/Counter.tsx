import type { JSX } from "react";
import { useAppDispatch } from "../../app/hooks/hooks";
import styles from "./Counter.module.css";
import { fetchDevicesThunk } from "./deviceThunk";

export const Counter = (): JSX.Element => {
    const dispatch = useAppDispatch();

    return (
        <div>
            <div className={styles.row}>
                <button
                    className={styles.button}
                    aria-label="Fetch devices"
                    onClick={() => {
                        dispatch(fetchDevicesThunk());
                    }}
                >
                    Get
                </button>
            </div>
        </div>
    );
};
