import { Link } from "react-router";

export const SidePane: React.FC = () => {
    return (
        <aside className="hidden lg:block lg:col-span-3 xl:col-span-2">
            <div className="sticky top-20 rounded-md border bg-white dark:bg-slate-800 border-slate-100 dark:border-slate-700 p-4">
                <nav className="flex flex-col gap-2 text-sm">
                    <Link
                        to="/"
                        className="px-2 py-2 rounded hover:bg-slate-100 dark:hover:bg-slate-700"
                    >
                        Devices
                    </Link>
                </nav>
            </div>
        </aside>
    );
};
