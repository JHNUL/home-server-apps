import type { JSX } from "react";
import { useAppDispatch } from "../../app/hooks";
import { useKeycloak } from "../../utils/KeycloakProvider";
import styles from "./Counter.module.css";
import { fetchDevices } from "./counterSlice";

export const Counter = (): JSX.Element => {
    const dispatch = useAppDispatch();
    const { token } = useKeycloak();

    return (
        <div>
            <div className={styles.row}>
                <button
                    className={styles.button}
                    aria-label="Fetch devices"
                    onClick={() => {
                        void dispatch(fetchDevices(token ?? ""));
                    }}
                >
                    Get
                </button>
            </div>
        </div>
    );
};
