import type React from "react";
import { Link } from "react-router";
import { useAppSelector } from "../app/hooks/hooks";
import { selectUsername } from "../utils/authSlice";

export const Header: React.FC = () => {
    const userName = useAppSelector(selectUsername);

    const initials = (userName.givenName ?? "").charAt(0) + 
        (userName.familyName ?? "").charAt(0)

    return (
        <header className="sticky top-0 z-40 bg-white/80 backdrop-blur-md border-b dark:bg-slate-900/75 dark:border-slate-800">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    {/* Left: brand */}
                    <div className="flex items-center gap-4">
                        <Link to="/" className="flex items-center gap-3">
                            <div className="w-9 h-9 rounded-lg bg-linear-to-br from-indigo-500 to-violet-500 flex items-center justify-center text-white font-bold">
                                HM
                            </div>
                            <div className="hidden sm:block">
                                <div className="text-lg font-semibold text-slate-900 dark:text-slate-100">
                                    Home Management
                                </div>
                                <div className="text-xs text-slate-500 dark:text-slate-400">
                                    control your crib
                                </div>
                            </div>
                        </Link>
                    </div>

                    {/* Center: search (hidden on very small screens) */}
                    <div className="flex-1 px-4 hidden md:block">
                        <div className="max-w-xl mx-auto">
                            <label htmlFor="top-search" className="sr-only">
                                Search
                            </label>
                            <div className="relative">
                                <input
                                    id="top-search"
                                    className="block w-full rounded-md border border-slate-200 bg-white py-2 pl-10 pr-4 text-sm placeholder-slate-400 shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-400 dark:bg-slate-800 dark:border-slate-700 dark:placeholder-slate-500"
                                    placeholder="Search devices, rooms, automations..."
                                />
                                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        className="h-4 w-4 text-slate-400"
                                        fill="none"
                                        viewBox="0 0 24 24"
                                        strokeWidth="2"
                                        stroke="currentColor"
                                    >
                                        <path
                                            strokeLinecap="round"
                                            strokeLinejoin="round"
                                            d="M21 21l-4.35-4.35m0 0A7.5 7.5 0 103.65 3.65a7.5 7.5 0 0012.7 12.7z"
                                        />
                                    </svg>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Right: actions */}
                    <div className="flex items-center gap-3">
                        <nav className="hidden sm:flex items-center gap-2">
                            <a className="text-sm px-3 py-1 rounded-md hover:bg-slate-100 dark:hover:bg-slate-800">
                                Devices
                            </a>
                            <a className="text-sm px-3 py-1 rounded-md hover:bg-slate-100 dark:hover:bg-slate-800">
                                Rooms
                            </a>
                            <a className="text-sm px-3 py-1 rounded-md hover:bg-slate-100 dark:hover:bg-slate-800">
                                Automations
                            </a>
                        </nav>

                        <button
                            aria-label="Toggle theme"
                            onClick={() => {
                                const html = document.documentElement;
                                html.classList.toggle("dark");
                            }}
                            className="p-2 rounded-md hover:bg-slate-100 dark:hover:bg-slate-800"
                        >
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                className="h-5 w-5 text-slate-700 dark:text-slate-200"
                                viewBox="0 0 20 20"
                                fill="currentColor"
                            >
                                <path d="M10 2a1 1 0 00-1 1v1a1 1 0 102 0V3a1 1 0 00-1-1zM4.22 4.22a1 1 0 00-1.44 1.44l.71.71a1 1 0 001.44-1.44l-.71-.71zM2 10a1 1 0 011-1h1a1 1 0 110 2H3a1 1 0 01-1-1zm8 6a1 1 0 00-1 1v1a1 1 0 102 0v-1a1 1 0 00-1-1zM15.78 4.22l-.71.71a1 1 0 001.44 1.44l.71-.71a1 1 0 10-1.44-1.44zM17 9a1 1 0 100 2h1a1 1 0 100-2h-1zM4.22 15.78l.71-.71a1 1 0 10-1.44-1.44l-.71.71a1 1 0 001.44 1.44zM15.78 15.78a1 1 0 001.44-1.44l-.71-.71a1 1 0 10-1.44 1.44l.71.71z" />
                            </svg>
                        </button>

                        <div className="relative">
                            <button className="flex items-center gap-2 px-2 py-1 rounded-md hover:bg-slate-100 dark:hover:bg-slate-800">
                                <span className="w-8 h-8 rounded-full bg-slate-200 dark:bg-slate-700 flex items-center justify-center text-slate-700 dark:text-slate-100 font-medium">
                                    {initials}
                                </span>
                                <span className="hidden sm:block text-sm text-slate-700 dark:text-slate-200">
                                    {userName.givenName ?? "Unknown"}
                                </span>
                            </button>
                        </div>

                        {/* Mobile hamburger */}
                        <div className="sm:hidden">
                            <button className="p-2 rounded-md hover:bg-slate-100 dark:hover:bg-slate-800">
                                <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    className="h-6 w-6 text-slate-700 dark:text-slate-200"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    strokeWidth="2"
                                    stroke="currentColor"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        d="M4 6h16M4 12h16M4 18h16"
                                    />
                                </svg>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </header>
    );
};

export default Header;
