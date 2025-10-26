import type React from "react";

export type ButtonVariant = "primary" | "secondary" | "ghost";
export type ButtonSize = "sm" | "md" | "lg";

type ButtonProps = {
    variant?: ButtonVariant;
    size?: ButtonSize;
} & React.ButtonHTMLAttributes<HTMLButtonElement>;

export const Button: React.FC<ButtonProps> = ({
    variant = "primary",
    size = "md",
    disabled,
    className = "",
    children,
    ...props
}) => {
    const base =
        "inline-flex items-center justify-center font-medium rounded-md transition-colors focus:outline-none focus:ring-2 focus:ring-indigo-400 disabled:opacity-50 disabled:pointer-events-none";

    let variantClasses = "";
    if (variant === "primary") {
        variantClasses =
            "bg-indigo-600 text-white hover:bg-indigo-700 active:bg-indigo-800 dark:bg-indigo-500 dark:hover:bg-indigo-400 dark:active:bg-indigo-300";
    } else if (variant === "secondary") {
        variantClasses =
            "bg-slate-200 text-slate-800 hover:bg-slate-300 active:bg-slate-400 dark:bg-slate-700 dark:text-slate-100 dark:hover:bg-slate-600 dark:active:bg-slate-500";
    } else {
        variantClasses =
            "bg-transparent text-slate-700 hover:bg-slate-100 active:bg-slate-200 dark:text-slate-200 dark:hover:bg-slate-800 dark:active:bg-slate-700";
    }

    let sizeClasses = "";
    if (size === "sm") {
        sizeClasses = "text-sm px-3 py-1.5";
    } else if (size === "md") {
        sizeClasses = "text-sm px-4 py-2";
    } else {
        sizeClasses = "text-base px-5 py-2.5";
    }

    const finalClasses = `${base} ${variantClasses} ${sizeClasses} ${className}`;

    return (
        <button className={finalClasses} disabled={disabled} {...props}>
            {children}
        </button>
    );
};
