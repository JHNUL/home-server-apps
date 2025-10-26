import type { ReactNode } from "react";
import Header from "./Header";
import { SidePane } from "./SidePane";

export const Layout: React.FC<{ children: ReactNode }> = ({ children }) => {
    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-900 text-slate-900 dark:text-slate-100">
            <Header />
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className="grid grid-cols-12 gap-6">
                    <SidePane />
                    <main className="col-span-12 lg:col-span-9 xl:col-span-10">{children}</main>
                </div>
            </div>
        </div>
    );
};

export default Layout;
