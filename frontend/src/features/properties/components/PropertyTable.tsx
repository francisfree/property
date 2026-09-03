import { type ColumnDef } from "@tanstack/react-table"
import { Pencil } from "lucide-react"

import { DataTable } from "@/components/DataTable"
import { Button } from "@/components/ui/button"
import { formatDateTime } from "@/lib/format"
import type { Property, PagedResponse } from "@/types/api"

interface PropertyTableProps {
  data: PagedResponse<Property> | undefined
  loading: boolean
  page: number
  size: number
  onPageChange: (page: number) => void
  onEdit: (property: Property) => void
}

export function PropertyTable({
  data,
  loading,
  page,
  size,
  onPageChange,
  onEdit,
}: PropertyTableProps) {
  const columns: ColumnDef<Property, unknown>[] = [
    {
      id: "name",
      accessorKey: "name",
      header: "Name",
    },
    {
      id: "location",
      accessorKey: "location",
      header: "Location",
    },
    {
      id: "area",
      accessorKey: "area",
      header: "Area",
    },
    {
      id: "dateCreated",
      header: "Date Created",
      cell: ({ row }) => formatDateTime(row.original.dateCreated),
    },
    {
      id: "dateModified",
      header: "Date Updated",
      cell: ({ row }) => formatDateTime(row.original.dateModified),
    },
    {
      id: "actions",
      header: "",
      cell: ({ row }) => (
        <div className="flex justify-end">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => onEdit(row.original)}
            aria-label={`Edit ${row.original.name}`}
          >
            <Pencil className="h-4 w-4" />
          </Button>
        </div>
      ),
    },
  ]

  const pageCount = data ? Math.max(1, data.totalPages) : 1

  return (
    <DataTable
      columns={columns}
      data={data?.content ?? []}
      loading={loading}
      rowKey={(property) => property.id}
      className="mb-6"
      pagination={{
        pageIndex: page,
        pageSize: size,
        pageCount,
        total: data?.totalElements ?? 0,
        onPageChange,
      }}
    />
  )
}