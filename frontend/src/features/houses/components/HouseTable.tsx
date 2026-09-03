import { type ColumnDef } from "@tanstack/react-table"
import { MoreHorizontal, Pencil } from "lucide-react"

import { DataTable } from "@/components/DataTable"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { formatDateTime, formatMoney, formatUuid } from "@/lib/format"
import type { House, PagedResponse } from "@/types/api"

interface HouseTableProps {
  data: PagedResponse<House> | undefined
  loading: boolean
  page: number
  size: number
  onPageChange: (page: number) => void
  onEdit: (house: House) => void
  onChangeAmount: (house: House) => void
  onMarkOccupied: (house: House) => void
  onMarkVacant: (house: House) => void
}

export function HouseTable({
  data,
  loading,
  page,
  size,
  onPageChange,
  onEdit,
  onChangeAmount,
  onMarkOccupied,
  onMarkVacant,
}: HouseTableProps) {
  const columns: ColumnDef<House, unknown>[] = [
    {
      id: "id",
      header: "Id",
      cell: ({ row }) => formatUuid(row.original.id),
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
      id: "status",
      header: "Status",
      cell: ({ row }) => (
        <Badge variant={row.original.status === "Occupied" ? "success" : "secondary"}>
          {row.original.status}
        </Badge>
      ),
    },
    {
      id: "number",
      accessorKey: "number",
      header: "Number",
    },
    {
      id: "floor",
      accessorKey: "floor",
      header: "Floor",
    },
    {
      id: "area",
      accessorKey: "propertyName",
      header: "Area",
    },
    {
      id: "currentMonthlyRent",
      header: "Monthly Rent",
      cell: ({ row }) => formatMoney(row.original.currentMonthlyRent),
    },
    {
      id: "actions",
      header: "",
      cell: ({ row }) => {
        const house = row.original
        return (
          <div className="flex items-center justify-end gap-1">
            <Button
              variant="ghost"
              size="icon"
              onClick={() => onEdit(house)}
              aria-label={`Edit ${house.number}`}
            >
              <Pencil className="h-4 w-4" />
            </Button>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon" aria-label="More actions">
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onClick={() => onChangeAmount(house)}>
                  Change Amount
                </DropdownMenuItem>
                {house.status === "Vacant" ? (
                  <DropdownMenuItem onClick={() => onMarkOccupied(house)}>
                    Mark Occupied
                  </DropdownMenuItem>
                ) : (
                  <DropdownMenuItem onClick={() => onMarkVacant(house)}>
                    Mark Vacant
                  </DropdownMenuItem>
                )}
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => onEdit(house)}>Edit</DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        )
      },
    },
  ]

  const pageCount = data ? Math.max(1, data.totalPages) : 1

  return (
    <DataTable
      columns={columns}
      data={data?.content ?? []}
      loading={loading}
      rowKey={(house) => house.id}
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