import { type ColumnDef } from "@tanstack/react-table"
import { MoreHorizontal } from "lucide-react"

import { DataTable } from "@/components/DataTable"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { formatDateTime, formatMoney, formatUuid } from "@/lib/format"
import type { PagedResponse, Rental } from "@/types/api"

interface RentalTableProps {
  data: PagedResponse<Rental> | undefined
  loading: boolean
  page: number
  size: number
  onPageChange: (page: number) => void
  onChangeAmount: (rental: Rental) => void
  onCloseAccount: (rental: Rental) => void
}

export function RentalTable({
  data,
  loading,
  page,
  size,
  onPageChange,
  onChangeAmount,
  onCloseAccount,
}: RentalTableProps) {
  const columns: ColumnDef<Rental, unknown>[] = [
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
      id: "arrearStatus",
      accessorKey: "arrearStatus",
      header: "Arrears Status",
      cell: ({ row }) => (
        <Badge variant={row.original.arrearStatus === "HasArrears" ? "warning" : "secondary"}>
          {row.original.arrearStatus}
        </Badge>
      ),
    },
    {
      id: "accountStatus",
      accessorKey: "accountStatus",
      header: "Account Status",
      cell: ({ row }) => (
        <Badge variant={row.original.accountStatus === "Active" ? "success" : "outline"}>
          {row.original.accountStatus}
        </Badge>
      ),
    },
    {
      id: "amount",
      accessorKey: "amount",
      header: "Amount (KSh)",
      cell: ({ row }) => <span className="tabular-nums">{formatMoney(row.original.amount)}</span>,
    },
    {
      id: "property",
      accessorFn: (rental) => rental.house.propertyName,
      header: "Property",
    },
    {
      id: "floor",
      accessorFn: (rental) => rental.house.floor,
      header: "Floor",
    },
    {
      id: "houseNumber",
      accessorFn: (rental) => rental.house.number,
      header: "House Number",
    },
    {
      id: "firstName",
      accessorFn: (rental) => rental.person.firstName,
      header: "First Name",
    },
    {
      id: "lastName",
      accessorFn: (rental) => rental.person.lastName,
      header: "Last Name",
    },
    {
      id: "otherName",
      accessorFn: (rental) => rental.person.otherName ?? "",
      header: "Other Name",
      cell: ({ getValue }) => (getValue() ? String(getValue()) : ""),
    },
    {
      id: "phoneNumber",
      accessorFn: (rental) => rental.person.phoneNumber,
      header: "Phone Number",
    },
    {
      id: "actions",
      header: "",
      cell: ({ row }) => {
        const rental = row.original
        const isActive = rental.accountStatus === "Active"
        return (
          <div className="flex justify-end">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon" aria-label="More actions">
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                {isActive ? (
                  <>
                    <DropdownMenuItem onClick={() => onChangeAmount(rental)}>
                      Change Amount
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      onClick={() => onCloseAccount(rental)}
                      className="text-destructive focus:text-destructive"
                    >
                      Close Account
                    </DropdownMenuItem>
                  </>
                ) : (
                  <DropdownMenuItem disabled>Account inactive</DropdownMenuItem>
                )}
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
      rowKey={(rental) => rental.id}
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