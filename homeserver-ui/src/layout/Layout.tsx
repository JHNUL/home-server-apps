import type { ReactNode } from "react";
import Header from "./Header";

export const Layout: React.FC<{ children: ReactNode }> = ({ children }) => {
    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-900 text-slate-900 dark:text-slate-100">
            <Header />
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className="grid grid-cols-12 gap-6">
                    <aside className="hidden lg:block lg:col-span-3 xl:col-span-2">
                        <div className="sticky top-20 rounded-md border bg-white dark:bg-slate-800 border-slate-100 dark:border-slate-700 p-4">
                            <nav className="flex flex-col gap-2 text-sm">
                                <a className="px-2 py-2 rounded hover:bg-slate-100 dark:hover:bg-slate-700">
                                    Overview
                                </a>
                                <a className="px-2 py-2 rounded hover:bg-slate-100 dark:hover:bg-slate-700">
                                    Devices
                                </a>
                                <a className="px-2 py-2 rounded hover:bg-slate-100 dark:hover:bg-slate-700">
                                    Rooms
                                </a>
                                <a className="px-2 py-2 rounded hover:bg-slate-100 dark:hover:bg-slate-700">
                                    Settings
                                </a>
                            </nav>
                        </div>
                    </aside>

                    <main className="col-span-12 lg:col-span-9 xl:col-span-10">{children}</main>
                </div>
            </div>
        </div>
    );
};

export default Layout;
