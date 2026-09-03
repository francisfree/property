import { useEffect, useState } from "react"
import { Upload, X } from "lucide-react"

import { PageHeader } from "@/components/PageHeader"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { useBlockNames, usePayments, useRevisionCounts, useUploadPayment } from "../hooks"
import { PaymentTable } from "../components/PaymentTable"
import { UploadPaymentDialog } from "../components/UploadPaymentDialog"

const PAGE_SIZE = 25

export function PaymentsPage() {
  const [page, setPage] = useState(0)
  const [month, setMonth] = useState("")
  const [blockName, setBlockName] = useState("")
  const [revisionCount, setRevisionCount] = useState("")
  const [search, setSearch] = useState("")
  const [uploadOpen, setUploadOpen] = useState(false)
  const [pendingUpload, setPendingUpload] = useState<{ month: string; blockName: string } | null>(null)

  const blockNamesQuery = useBlockNames(month)
  const revisionCountsQuery = useRevisionCounts(month, blockName)

  const blockNames = blockNamesQuery.data ?? []
  const revisionCounts = revisionCountsQuery.data ?? []

  const hasFilters = month !== "" || blockName !== "" || revisionCount !== "" || search !== ""

  const { data, isFetching, isError } = usePayments({
    page,
    size: PAGE_SIZE,
    month: month,
    blockName: blockName,
    revisionCount: revisionCount !== "" ? Number(revisionCount) : 0,
    searchParam: search || undefined,
  })

  const uploadMutation = useUploadPayment()

  useEffect(() => {
    if (pendingUpload) {
      const counts = revisionCountsQuery.data
      if (counts && counts.length > 0) {
        const latest = Math.max(...counts)
        setRevisionCount(String(latest))
        setPendingUpload(null)
      }
    }
  }, [pendingUpload, revisionCountsQuery.data])

  const handleUploadSubmit = (file: File, uploadMonth: string, uploadBlockName: string) => {
    uploadMutation.mutate(
      { file, month: uploadMonth, blockName: uploadBlockName },
      {
        onSuccess: () => {
          setMonth(uploadMonth)
          setBlockName(uploadBlockName)
          setPage(0)
          setSearch("")
          setUploadOpen(false)
          setPendingUpload({ month: uploadMonth, blockName: uploadBlockName })
        },
      },
    )
  }

  const handleClear = () => {
    setMonth("")
    setBlockName("")
    setRevisionCount("")
    setSearch("")
    setPage(0)
  }

  return (
    <div>
      <PageHeader
        title="Payments"
        description="Upload and view payment records by block and month."
        actions={
          <Button onClick={() => setUploadOpen(true)}>
            <Upload className="mr-2 h-4 w-4" />
            Upload
          </Button>
        }
      />

      <div className="mb-4 flex flex-wrap items-end gap-4">
        <div className="space-y-1">
          <Label htmlFor="filter-month">Month</Label>
          <Input
            id="filter-month"
            type="month"
            className="w-[180px]"
            value={month}
            onChange={(e) => {
              setMonth(e.target.value)
              setBlockName("")
              setRevisionCount("")
              setPage(0)
            }}
          />
        </div>

        <div className="space-y-1">
          <Label>Block Name</Label>
          <Select
            value={blockName}
            onValueChange={(value) => {
              setBlockName(value)
              setRevisionCount("")
              setPage(0)
            }}
            disabled={!month || blockNames.length === 0}
          >
            <SelectTrigger className="w-[160px]">
              <SelectValue placeholder="Select block" />
            </SelectTrigger>
            <SelectContent>
              {blockNames.map((name) => (
                <SelectItem key={name} value={name}>
                  {name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <div className="space-y-1">
          <Label>Revision</Label>
          <Select
            value={revisionCount}
            onValueChange={(value) => {
              setRevisionCount(value)
              setPage(0)
            }}
            disabled={!blockName || revisionCounts.length === 0}
          >
            <SelectTrigger className="w-[140px]">
              <SelectValue placeholder="Select" />
            </SelectTrigger>
            <SelectContent>
              {revisionCounts.map((rc) => (
                <SelectItem key={rc} value={String(rc)}>
                  Rev {rc}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <div className="space-y-1">
          <Label htmlFor="filter-search">Search</Label>
          <Input
            id="filter-search"
            placeholder="House, name, phone..."
            className="w-[200px]"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value)
              setPage(0)
            }}
          />
        </div>

        {hasFilters && (
          <Button variant="outline" onClick={handleClear}>
            <X className="mr-1 h-4 w-4" />
            Clear
          </Button>
        )}
      </div>

      {isError ? (
        <p className="text-sm text-destructive">
          Failed to load payments. Please try again.
        </p>
      ) : (
        <PaymentTable
          data={data?.content}
          loading={isFetching}
          pagination={
            data
              ? {
                  pageIndex: page,
                  pageSize: PAGE_SIZE,
                  pageCount: Math.max(1, data.totalPages),
                  total: data.totalElements,
                  onPageChange: setPage,
                }
              : undefined
          }
        />
      )}

      <UploadPaymentDialog
        open={uploadOpen}
        onOpenChange={setUploadOpen}
        onSubmit={handleUploadSubmit}
        submitting={uploadMutation.isPending}
      />
    </div>
  )
}
