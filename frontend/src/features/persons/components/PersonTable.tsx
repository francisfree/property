import { type ColumnDef } from "@tanstack/react-table"
import { Pencil } from "lucide-react"

import { DataTable } from "@/components/DataTable"
import { Button } from "@/components/ui/button"
import { formatDateTime, formatUuid } from "@/lib/format"
import type { PagedResponse, Person } from "@/types/api"

interface PersonTableProps {
  data: PagedResponse<Person> | undefined
  loading: boolean
  page: number
  size: number
  onPageChange: (page: number) => void
  onEdit: (person: Person) => void
}

export function PersonTable({
  data,
  loading,
  page,
  size,
  onPageChange,
  onEdit,
}: PersonTableProps) {
  const columns: ColumnDef<Person, unknown>[] = [
    {
      id: "id",
      header: "Id",
      cell: ({ row }) => formatUuid(row.original.id),
    },
    {
      id: "dateCreated",
      header: "Date",
      cell: ({ row }) => formatDateTime(row.original.dateCreated),
    },
    {
      id: "firstName",
      accessorKey: "firstName",
      header: "First Name",
    },
    {
      id: "lastName",
      accessorKey: "lastName",
      header: "Last Name",
    },
    {
      id: "otherName",
      accessorKey: "otherName",
      header: "Other Name",
      cell: ({ getValue }) => (getValue() ? String(getValue()) : ""),
    },
    {
      id: "phoneNumber",
      accessorKey: "phoneNumber",
      header: "Phone Number",
    },
    {
      id: "identificationType",
      accessorKey: "identificationType",
      header: "Identification Type",
    },
    {
      id: "identificationNumber",
      accessorKey: "identificationNumber",
      header: "Identification Number",
    },
    {
      id: "nationality",
      accessorKey: "nationality",
      header: "Nationality",
      cell: ({ getValue }) => (getValue() ? String(getValue()) : ""),
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
            aria-label={`Edit ${row.original.firstName} ${row.original.lastName}`}
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
      rowKey={(person) => person.id}
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