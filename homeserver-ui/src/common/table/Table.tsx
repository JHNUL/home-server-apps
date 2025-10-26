import { TableCell } from "./TableCell";
import TableRow from "./TableRow";

export type TableProps = {
    headers?: string[];
    children: React.ReactNode;
};

export const Table: React.FC<TableProps> = ({ headers, children }) => {
    return (
        <div className="overflow-hidden rounded-md border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 shadow-sm">
            {headers && headers.length > 0 && (
                <TableRow header>
                    {headers.map((header, i) => (<TableCell key={i} header>{header}</TableCell>))}
                </TableRow>
            )}
            <div className="divide-y divide-slate-200 dark:divide-slate-700">{children}</div>
        </div>
    );
};

export default Table;
