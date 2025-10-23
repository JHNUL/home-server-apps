import type { FallbackProps } from "react-error-boundary";

export const ErrorComponent: React.FC<FallbackProps> = ({ error }) => {
    const message = error instanceof Error ? error.message : "Something went awry!";
    return (
        <div
            style={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                height: "100vh",
            }}
        >
            <div role="alert">
                <div style={{ color: "red" }}>{message}</div>
            </div>
        </div>
    );
};
