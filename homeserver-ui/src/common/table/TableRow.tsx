import React from "react";

export type TableRowProps = {
    children: React.ReactNode;
    selected?: boolean;
    header?: boolean;
    onClick?: () => void;
};

export const TableRow: React.FC<TableRowProps> = ({
    children,
    selected = false,
    header = false,
    onClick,
}) => {

    const columns = React.Children.toArray(children).filter(child =>
        React.isValidElement(child),
    ).length;

    // https://tailwindcss.com/docs/detecting-classes-in-source-files#dynamic-class-names
    let base = `grid grid-cols-${String(columns)} items-center px-4 py-2 border-b border-slate-200 dark:border-slate-700`;
    if (header) {
        base += " bg-slate-50 dark:bg-slate-900/40";
    } else {
        base += " text-sm transition-colors";
    }

    const hover =
        "hover:bg-slate-100 active:bg-slate-200 dark:hover:bg-slate-800 dark:active:bg-slate-700 cursor-pointer";

    const selectedClasses = selected
        ? "bg-indigo-50 dark:bg-indigo-900/30 border-indigo-200 dark:border-indigo-700"
        : "";

    const finalClasses = `${base} ${hover} ${selectedClasses}`;

    return (
        <div className={finalClasses} onClick={onClick}>
            {children}
        </div>
    );
};

export default TableRow;
