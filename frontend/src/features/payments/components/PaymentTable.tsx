import { useState } from "react"
import { ChevronDown, ChevronLeft, ChevronRight } from "lucide-react"

import { type DataTablePagination } from "@/components/DataTable"
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
import { formatMoney } from "@/lib/format"
import type { PaymentMonth } from "../types"

const COLUMN_COUNT = 12

interface PaymentTableProps {
  data: PaymentMonth[] | undefined
  loading: boolean
  pagination?: DataTablePagination
}

export function PaymentTable({ data, loading, pagination }: PaymentTableProps) {
  const [expanded, setExpanded] = useState<Set<number>>(new Set())

  const toggleRow = (id: number) => {
    setExpanded((prev) => {
      const next = new Set(prev)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      return next
    })
  }

  const pageCount = pagination
    ? Math.max(1, Math.ceil(pagination.total / pagination.pageSize))
    : 1

  if (loading && (!data || data.length === 0)) {
    return (
      <div className="space-y-4">
        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-10" />
                <TableHead>No</TableHead>
                <TableHead>Name</TableHead>
                <TableHead>Phone</TableHead>
                <TableHead>Rent (Current)</TableHead>
                <TableHead>Rent (Previous)</TableHead>
                <TableHead>Arrears B/F</TableHead>
                <TableHead>Total Payment</TableHead>
                <TableHead>Prev Water Unit</TableHead>
                <TableHead>Curr Water Unit</TableHead>
                <TableHead>Price/Unit</TableHead>
                <TableHead>Water Bill</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {Array.from({ length: pagination?.pageSize ?? 10 }).map((_, i) => (
                <TableRow key={`skeleton-${i}`}>
                  {Array.from({ length: COLUMN_COUNT }).map((_, j) => (
                    <TableCell key={j}>
                      <Skeleton className="h-4 w-full" />
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      </div>
    )
  }

  const payments = data ?? []

  return (
    <div className="space-y-4">
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-10" />
              <TableHead>No</TableHead>
              <TableHead>Name</TableHead>
              <TableHead>Phone</TableHead>
              <TableHead>Rent (Current)</TableHead>
              <TableHead>Rent (Previous)</TableHead>
              <TableHead>Arrears B/F</TableHead>
              <TableHead>Total Payment</TableHead>
              <TableHead>Prev Water Unit</TableHead>
              <TableHead>Curr Water Unit</TableHead>
              <TableHead>Price/Unit</TableHead>
              <TableHead>Consumed Units</TableHead>
              <TableHead>Water Bill</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {payments.length === 0 ? (
              <TableRow>
                <TableCell colSpan={COLUMN_COUNT} className="h-24 text-center">
                  No records found.
                </TableCell>
              </TableRow>
            ) : (
              payments.map((payment) => (
                <PaymentRow
                  key={payment.id}
                  payment={payment}
                  isExpanded={expanded.has(payment.id)}
                  onToggle={() => toggleRow(payment.id)}
                />
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {pagination && (
        <div className="flex flex-col items-center justify-between gap-4 sm:flex-row">
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            {pagination.total.toLocaleString()} record
            {pagination.total === 1 ? "" : "s"}
          </div>
          <div className="flex items-center gap-1">
            <Button
              variant="outline"
              size="icon"
              disabled={pagination.pageIndex === 0 || loading}
              onClick={() => pagination.onPageChange(pagination.pageIndex - 1)}
            >
              <ChevronLeft className="h-4 w-4" />
            </Button>
            <span className="px-2 text-sm">
              Page {pageCount === 0 ? 0 : pagination.pageIndex + 1} of {pageCount}
            </span>
            <Button
              variant="outline"
              size="icon"
              disabled={pagination.pageIndex + 1 >= pageCount || loading}
              onClick={() => pagination.onPageChange(pagination.pageIndex + 1)}
            >
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}

function PaymentRow({
  payment,
  isExpanded,
  onToggle,
}: {
  payment: PaymentMonth
  isExpanded: boolean
  onToggle: () => void
}) {
  return (
    <>
      <TableRow>
        <TableCell>
          <Button variant="ghost" size="icon" className="h-6 w-6" onClick={onToggle}>
            {isExpanded ? (
              <ChevronDown className="h-3 w-3" />
            ) : (
              <ChevronRight className="h-3 w-3" />
            )}
          </Button>
        </TableCell>
        <TableCell>{payment.houseNumber}</TableCell>
        <TableCell>{payment.occupantName}</TableCell>
        <TableCell>{payment.occupantPhoneNumber}</TableCell>
        <TableCell>
          <span className="tabular-nums">{formatMoney(payment.rentCurrentMonth)}</span>
        </TableCell>
        <TableCell>
          <span className="tabular-nums">{formatMoney(payment.rentPreviousMonth)}</span>
        </TableCell>
        <TableCell>
          <span className="tabular-nums">
            {formatMoney(payment.arrearsBroughtForward)}
          </span>
        </TableCell>
        <TableCell>
          <span className="tabular-nums">{formatMoney(payment.totalPayment)}</span>
        </TableCell>
        <TableCell>{payment.previousWaterUnit}</TableCell>
        <TableCell>{payment.currentWaterUnit}</TableCell>
        <TableCell>
          <span className="tabular-nums">{formatMoney(payment.pricePerUnit)}</span>
        </TableCell>
        <TableCell>{payment.unitsConsumed}</TableCell>
        <TableCell>
          <span className="tabular-nums">{formatMoney(payment.waterBill)}</span>
        </TableCell>
      </TableRow>
      {isExpanded && payment.weeklyEntries.length > 0 && (
        <TableRow>
          <TableCell colSpan={COLUMN_COUNT} className="bg-muted/30 p-0">
            <div className="px-8 py-2">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Week</TableHead>
                    <TableHead>Cash</TableHead>
                    <TableHead>Till</TableHead>
                    <TableHead>Mpesa</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {payment.weeklyEntries.map((entry) => (
                    <TableRow key={entry.id}>
                      <TableCell className="font-medium">{entry.weekName}</TableCell>
                      <TableCell>
                        <span className="tabular-nums">{formatMoney(entry.cash)}</span>
                      </TableCell>
                      <TableCell>
                        <span className="tabular-nums">{formatMoney(entry.till)}</span>
                      </TableCell>
                      <TableCell>
                        <span className="tabular-nums">{formatMoney(entry.mpesa)}</span>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </TableCell>
        </TableRow>
      )}
    </>
  )
}
