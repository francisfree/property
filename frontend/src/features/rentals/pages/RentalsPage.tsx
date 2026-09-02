import { useQuery } from "@tanstack/react-query"
import { Filter, Plus, X } from "lucide-react"
import { useState } from "react"

import { ConfirmDialog } from "@/components/ConfirmDialog"
import { PageHeader } from "@/components/PageHeader"
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { fetchAllProperties } from "@/features/properties/api"
import type { Rental } from "@/types/api"
import { ChangeAmountDialog } from "../components/ChangeAmountDialog"
import { CloseAccountDialog } from "../components/CloseAccountDialog"
import { RentalFilterDialog } from "../components/RentalFilterDialog"
import { RentalForm } from "../components/RentalForm"
import { RentalTable } from "../components/RentalTable"
import {
  useChangeRentalAmount,
  useCloseRentalAccount,
  useCreateRental,
  useRentals,
} from "../hooks"
import type { RentalCreateValues, RentalFilterValues } from "../schema"

const PAGE_SIZE = 25

const EMPTY_FILTERS: RentalFilterValues = {
  propertyId: "",
  houseId: "",
  accountStatus: "",
  arrearStatus: "",
  search: "",
}

function normalizeFilterValue(value: string | undefined): string | undefined {
  if (!value || value === "__all__") return undefined
  return value
}

export function RentalsPage() {
  const [page, setPage] = useState(0)
  const [filters, setFilters] = useState<RentalFilterValues>(EMPTY_FILTERS)
  const [filterOpen, setFilterOpen] = useState(false)
  const [createOpen, setCreateOpen] = useState(false)
  const [changeOpen, setChangeOpen] = useState(false)
  const [closeOpen, setCloseOpen] = useState(false)
  const [closeConfirmOpen, setCloseConfirmOpen] = useState(false)
  const [selectedRental, setSelectedRental] = useState<Rental | undefined>(undefined)

  const propertiesQuery = useQuery({
    queryKey: ["properties", "all"],
    queryFn: fetchAllProperties,
  })

  const { data, isFetching, isError } = useRentals({
    page,
    size: PAGE_SIZE,
    propertyId: normalizeFilterValue(filters.propertyId),
    houseId: normalizeFilterValue(filters.houseId),
    accountStatus: normalizeFilterValue(filters.accountStatus),
    arrearStatus: normalizeFilterValue(filters.arrearStatus),
    search: filters.search || undefined,
  })

  const createMutation = useCreateRental()
  const changeMutation = useChangeRentalAmount()
  const closeMutation = useCloseRentalAccount()

  const properties = propertiesQuery.data ?? []

  const handleApplyFilters = (values: RentalFilterValues) => {
    setFilters(values)
    setPage(0)
    setFilterOpen(false)
  }

  const handleClear = () => {
    setFilters(EMPTY_FILTERS)
    setPage(0)
  }

  const handleCreateSubmit = (values: RentalCreateValues) => {
    createMutation.mutate(values, {
      onSuccess: () => setCreateOpen(false),
    })
  }

  const handleChangeSubmit = (amount: number) => {
    if (!selectedRental) return
    changeMutation.mutate(
      { id: selectedRental.id, amount },
      {
        onSuccess: () => {
          setChangeOpen(false)
          setSelectedRental(undefined)
        },
      },
    )
  }

  const handleCloseConfirm = () => {
    if (!selectedRental) return
    closeMutation.mutate(selectedRental.id, {
      onSuccess: () => {
        setCloseConfirmOpen(false)
        setCloseOpen(false)
        setSelectedRental(undefined)
      },
    })
  }

  const hasActiveFilters = Object.values(filters).some((v) => v && v !== "__all__")

  return (
    <div>
      <PageHeader
        title="Rentals"
        description="Manage rental accounts, amounts and arrears."
        actions={
          <>
            <Button variant={hasActiveFilters ? "secondary" : "outline"} onClick={() => setFilterOpen(true)}>
              <Filter className="h-4 w-4" />
              Filter
            </Button>
            <Button variant="outline" onClick={handleClear}>
              <X className="h-4 w-4" />
              Clear
            </Button>
            <Button onClick={() => setCreateOpen(true)}>
              <Plus className="h-4 w-4" />
              New Rental
            </Button>
          </>
        }
      />

      {isError ? (
        <p className="text-sm text-destructive">
          Failed to load rentals. Please try again.
        </p>
      ) : (
        <RentalTable
          data={data}
          loading={isFetching}
          page={page}
          size={PAGE_SIZE}
          onPageChange={setPage}
          onChangeAmount={(rental) => {
            setSelectedRental(rental)
            setChangeOpen(true)
          }}
          onCloseAccount={(rental) => {
            setSelectedRental(rental)
            setCloseOpen(true)
          }}
        />
      )}

      <RentalFilterDialog
        open={filterOpen}
        onOpenChange={setFilterOpen}
        properties={properties}
        initialValues={filters}
        onApply={handleApplyFilters}
      />

      <Dialog open={createOpen} onOpenChange={setCreateOpen}>
        <DialogContent className="sm:max-w-lg">
          <DialogHeader>
            <DialogTitle>New Rental</DialogTitle>
            <DialogDescription>
              Create a new rental and its tenant in one step.
            </DialogDescription>
          </DialogHeader>
          <RentalForm
            key={createOpen ? "open" : "closed"}
            properties={properties}
            submitting={createMutation.isPending}
            onSubmit={handleCreateSubmit}
            onCancel={() => setCreateOpen(false)}
          />
        </DialogContent>
      </Dialog>

      <ChangeAmountDialog
        open={changeOpen}
        onOpenChange={setChangeOpen}
        rental={selectedRental}
        submitting={changeMutation.isPending}
        onSubmit={handleChangeSubmit}
      />

      <CloseAccountDialog
        open={closeOpen}
        onOpenChange={setCloseOpen}
        rental={selectedRental}
        onProceed={() => setCloseConfirmOpen(true)}
      />

      <ConfirmDialog
        open={closeConfirmOpen}
        onOpenChange={setCloseConfirmOpen}
        title="Close Account"
        description={`Are you sure you want to close the account for ${selectedRental?.person.firstName ?? ""} ${selectedRental?.person.lastName ?? ""} (${selectedRental?.house.number ?? ""})? This cannot be undone.`}
        confirmLabel="Yes, Close Account"
        destructive
        loading={closeMutation.isPending}
        onConfirm={handleCloseConfirm}
      />
    </div>
  )
}