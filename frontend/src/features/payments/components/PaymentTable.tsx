import { useState } from "react"
import {
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  Download,
  Loader2,
  Printer,
  X,
} from "lucide-react"
import { toast } from "sonner"

import { type DataTablePagination } from "@/components/DataTable"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
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
import {
  fetchAllPaymentIds,
  fetchPaymentReceipt,
  fetchPaymentReceipts,
  type PaymentQueryParams,
} from "../api"
import { downloadBlob, printBlob } from "../receipt"
import type { PaymentMonth } from "../types"

const COLUMN_COUNT = 13
const COLSPAN = COLUMN_COUNT + 1

type BusyAction = "download" | "print" | "selectAll" | null

interface PaymentTableProps {
  data: PaymentMonth[] | undefined
  loading: boolean
  pagination?: DataTablePagination
  queryParams: PaymentQueryParams
}

export function PaymentTable({
  data,
  loading,
  pagination,
  queryParams,
}: PaymentTableProps) {
  const [expanded, setExpanded] = useState<Set<number>>(new Set())
  const [selected, setSelected] = useState<Set<string>>(new Set())
  const [busy, setBusy] = useState<BusyAction>(null)

  const toggleRow = (id: number) => {
    setExpanded((prev) => {
      const next = new Set(prev)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      return next
    })
  }

  const toggleRowSelection = (publicId: string) => {
    if (busy) return
    setSelected((prev) => {
      const next = new Set(prev)
      if (next.has(publicId)) next.delete(publicId)
      else next.add(publicId)
      return next
    })
  }

  const clearSelection = () => setSelected(new Set())

  const handleSelectAll = (next: boolean | "indeterminate") => {
    if (busy) return
    const currentPageIds = (data ?? []).map((p) => p.publicId)

    if (next !== true) {
      if (currentPageIds.length === 0) {
        clearSelection()
        return
      }
      setSelected((prev) => {
        const nextSet = new Set(prev)
        currentPageIds.forEach((id) => nextSet.delete(id))
        return nextSet
      })
      return
    }

    setBusy("selectAll")
    fetchAllPaymentIds(queryParams)
      .then((ids) => setSelected(new Set(ids)))
      .catch((error: Error) => toast.error(error.message))
      .finally(() => setBusy(null))
  }

  const handleReceipts = async (action: "download" | "print") => {
    if (busy) return
    setBusy(action)
    try {
      const selectedIds = Array.from(selected)

      if (action === "download") {
        const blob = await fetchPaymentReceipts(selectedIds)
        downloadBlob(blob, "receipts.pdf")
        toast.success(
          `Downloaded ${selectedIds.length} receipt${selectedIds.length === 1 ? "" : "s"}`,
        )
      } else {
        const printWin = window.open("", "_blank")
        try {
          const blob =
            selectedIds.length === 1
              ? await fetchPaymentReceipt(selectedIds[0])
              : await fetchPaymentReceipts(selectedIds)
          printBlob(blob, printWin)
        } catch (printError) {
          printWin?.close()
          throw printError
        }
      }
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Failed to get receipts")
    } finally {
      setBusy(null)
    }
  }

  const pageCount = pagination
    ? Math.max(1, Math.ceil(pagination.total / pagination.pageSize))
    : 1

  const payments = data ?? []
  const currentPageIds = payments.map((p) => p.publicId)
  const allCurrentSelected =
    currentPageIds.length > 0 && currentPageIds.every((id) => selected.has(id))
  const someCurrentSelected = currentPageIds.some((id) => selected.has(id))

  if (loading && (!data || data.length === 0)) {
    return (
      <div className="space-y-4">
        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-10" />
                <TableHead>No</TableHead>
                <TableHead>Receipt No</TableHead>
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
                  {Array.from({ length: COLSPAN }).map((_, j) => (
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

  return (
    <div className="space-y-4">
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-10">
                <div className="flex items-center justify-center">
                  {busy === "selectAll" ? (
                    <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />
                  ) : (
                    <Checkbox
                      checked={allCurrentSelected}
                      indeterminate={!allCurrentSelected && someCurrentSelected}
                      onCheckedChange={handleSelectAll}
                      disabled={payments.length === 0 || busy !== null}
                      aria-label="Select all payments"
                    />
                  )}
                </div>
              </TableHead>
              <TableHead>No</TableHead>
              <TableHead>Receipt No</TableHead>
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
                <TableCell colSpan={COLSPAN} className="h-24 text-center">
                  No records found.
                </TableCell>
              </TableRow>
            ) : (
              payments.map((payment) => (
                <PaymentRow
                  key={payment.id}
                  payment={payment}
                  isExpanded={expanded.has(payment.id)}
                  isSelected={selected.has(payment.publicId)}
                  busy={busy !== null}
                  onToggle={() => toggleRow(payment.id)}
                  onToggleSelect={() => toggleRowSelection(payment.publicId)}
                />
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {selected.size > 0 && (
        <div className="flex flex-wrap items-center justify-between gap-2 rounded-md border bg-muted/50 px-3 py-2">
          <span className="text-sm font-medium">
            {selected.size} selected
          </span>
          <div className="flex items-center gap-2">
            <Button
              size="sm"
              disabled={busy !== null}
              onClick={() => void handleReceipts("download")}
            >
              {busy === "download" && (
                <Loader2 className="h-4 w-4 animate-spin" />
              )}
              {busy !== "download" && <Download className="h-4 w-4" />}
              Download
            </Button>
            <Button
              size="sm"
              variant="secondary"
              disabled={busy !== null}
              onClick={() => void handleReceipts("print")}
            >
              {busy === "print" && <Loader2 className="h-4 w-4 animate-spin" />}
              {busy !== "print" && <Printer className="h-4 w-4" />}
              Print
            </Button>
            <Button
              size="sm"
              variant="ghost"
              disabled={busy !== null}
              onClick={clearSelection}
            >
              <X className="h-4 w-4" />
              Clear
            </Button>
          </div>
        </div>
      )}

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
              disabled={pagination.pageIndex === 0 || loading || busy !== null}
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
              disabled={pagination.pageIndex + 1 >= pageCount || loading || busy !== null}
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
  isSelected,
  busy,
  onToggle,
  onToggleSelect,
}: {
  payment: PaymentMonth
  isExpanded: boolean
  isSelected: boolean
  busy: boolean
  onToggle: () => void
  onToggleSelect: () => void
}) {
  return (
    <>
      <TableRow>
        <TableCell>
          <div className="flex items-center gap-1">
            <Checkbox
              checked={isSelected}
              onCheckedChange={onToggleSelect}
              disabled={busy}
              aria-label={`Select ${payment.houseNumber}`}
            />
            <Button variant="ghost" size="icon" className="h-6 w-6" onClick={onToggle}>
              {isExpanded ? (
                <ChevronDown className="h-3 w-3" />
              ) : (
                <ChevronRight className="h-3 w-3" />
              )}
            </Button>
          </div>
        </TableCell>
        <TableCell>{payment.houseNumber}</TableCell>
        <TableCell>{payment.receiptNumber ?? "—"}</TableCell>
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
          <TableCell colSpan={COLSPAN} className="bg-muted/30 p-0">
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