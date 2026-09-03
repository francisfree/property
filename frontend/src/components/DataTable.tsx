import {
  flexRender,
  getCoreRowModel,
  useReactTable,
  type ColumnDef,
  type SortingState,
} from "@tanstack/react-table"
import { ChevronLeft, ChevronRight } from "lucide-react"
import * as React from "react"

import { Button } from "@/components/ui/button"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Skeleton } from "@/components/ui/skeleton"
import { cn } from "@/lib/utils"

export interface DataTablePagination {
  pageIndex: number
  pageSize: number
  pageCount: number
  total: number
  onPageChange: (pageIndex: number) => void
  onPageSizeChange?: (pageSize: number) => void
}

interface DataTableProps<TData> {
  columns: ColumnDef<TData, unknown>[]
  data: TData[]
  loading?: boolean
  pagination?: DataTablePagination
  rowKey: (row: TData) => string | number
  onRowClick?: (row: TData) => void
  emptyMessage?: string
  className?: string
}

export function DataTable<TData>({
  columns,
  data,
  loading = false,
  pagination,
  rowKey,
  onRowClick,
  emptyMessage = "No records found.",
  className,
}: DataTableProps<TData>) {
  const [sorting, setSorting] = React.useState<SortingState>([])

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    state: { sorting },
    onSortingChange: setSorting,
    manualPagination: true,
  })

  const pageSizes = [10, 25, 50]

  return (
    <div className={cn("space-y-4", className)}>
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => (
                  <TableHead key={header.id}>
                    {header.isPlaceholder
                      ? null
                      : flexRender(
                          header.column.columnDef.header,
                          header.getContext(),
                        )}
                  </TableHead>
                ))}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {loading ? (
              Array.from({ length: pagination?.pageSize ?? 10 }).map((_, i) => (
                <TableRow key={`skeleton-${i}`}>
                  {columns.map((_, j) => (
                    <TableCell key={j}>
                      <Skeleton className="h-4 w-full" />
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : data.length === 0 ? (
              <TableRow>
                <TableCell colSpan={columns.length} className="h-24 text-center">
                  {emptyMessage}
                </TableCell>
              </TableRow>
            ) : (
              table.getRowModel().rows.map((row) => (
                <TableRow
                  key={rowKey(row.original)}
                  className={onRowClick ? "cursor-pointer" : undefined}
                  onClick={onRowClick ? () => onRowClick(row.original) : undefined}
                >
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {pagination ? (
        <div className="flex flex-col items-center justify-between gap-4 sm:flex-row">
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            {pagination.total.toLocaleString()} record
            {pagination.total === 1 ? "" : "s"}
          </div>
          <div className="flex items-center gap-4">
            {pagination.onPageSizeChange ? (
              <select
                className="h-8 rounded-md border border-input bg-background px-2 text-sm"
                value={pagination.pageSize}
                onChange={(e) => pagination.onPageSizeChange?.(Number(e.target.value))}
              >
                {pageSizes.map((size) => (
                  <option key={size} value={size}>
                    {size} / page
                  </option>
                ))}
              </select>
            ) : null}
            <div className="flex items-center gap-1">
              <Button
                variant="outline"
                size="icon"
                disabled={pagination.pageIndex === 0 || loading}
                onClick={() =>
                  pagination.onPageChange(pagination.pageIndex - 1)
                }
              >
                <ChevronLeft className="h-4 w-4" />
              </Button>
              <span className="px-2 text-sm">
                Page {pagination.pageCount === 0 ? 0 : pagination.pageIndex + 1} of{" "}
                {pagination.pageCount}
              </span>
              <Button
                variant="outline"
                size="icon"
                disabled={
                  pagination.pageIndex + 1 >= pagination.pageCount || loading
                }
                onClick={() =>
                  pagination.onPageChange(pagination.pageIndex + 1)
                }
              >
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  )
}