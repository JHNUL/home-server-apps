export type TableCellProps = {
    children: React.ReactNode;
    align?: "left" | "center" | "right";
    header?: boolean;
    width?: string;
};

export const TableCell: React.FC<TableCellProps> = ({
    children,
    align = "left",
    header = false,
    width,
}) => {
    const alignment =
        align === "center" ? "text-center" : align === "right" ? "text-right" : "text-left";

    const base = "px-2 py-1 truncate text-sm text-slate-800 dark:text-slate-200";
    const headerClasses = header ? "font-semibold text-slate-900 dark:text-slate-100" : "";

    const style: React.CSSProperties = width ? { width } : {};

    return (
        <div className={`${base} ${alignment} ${headerClasses}`} style={style}>
            {children}
        </div>
    );
};
