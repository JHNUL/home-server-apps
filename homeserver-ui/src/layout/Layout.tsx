import type { ReactNode } from "react";

export const Layout: React.FC<{ children: ReactNode }> = ({ children }) => {
    return (
        <div>
            <header>This is a header</header>
            <div>{children}</div>
        </div>
    );
};
