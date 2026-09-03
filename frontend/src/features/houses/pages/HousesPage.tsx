import { useQuery } from "@tanstack/react-query"
import { Plus } from "lucide-react"
import { useState } from "react"

import { ConfirmDialog } from "@/components/ConfirmDialog"
import { PageHeader } from "@/components/PageHeader"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { fetchAllProperties } from "@/features/properties/api"
import type { House } from "@/types/api"
import { HouseAmountDialog } from "../components/HouseAmountDialog"
import { HouseDialog } from "../components/HouseDialog"
import { HouseTable } from "../components/HouseTable"
import { useHouseAction, useHouses, useSaveHouse } from "../hooks"
import type { HouseFormValues } from "../schema"

const PAGE_SIZE = 25

type AmountAction = "changeAmount" | "markOccupied" | null

export function HousesPage() {
  const [searchInput, setSearchInput] = useState("")
  const [search, setSearch] = useState("")
  const [propertyId, setPropertyId] = useState<string>("")
  const [page, setPage] = useState(0)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<House | undefined>(undefined)
  const [amountAction, setAmountAction] = useState<AmountAction>(null)
  const [actionHouse, setActionHouse] = useState<House | undefined>(undefined)
  const [vacantConfirmOpen, setVacantConfirmOpen] = useState(false)

  const { data, isFetching, isError } = useHouses({
    page,
    size: PAGE_SIZE,
    search,
    propertyId: propertyId || undefined,
  })

  const propertiesQuery = useQuery({
    queryKey: ["properties", "all"],
    queryFn: fetchAllProperties,
  })

  const saveMutation = useSaveHouse()
  const actionMutation = useHouseAction()

  const handleFilter = () => {
    setPage(0)
    setSearch(searchInput)
  }

  const handleClear = () => {
    setSearchInput("")
    setSearch("")
    setPropertyId("")
    setPage(0)
  }

  const handleSubmit = (values: HouseFormValues) => {
    saveMutation.mutate(
      { id: editing?.id, values },
      {
        onSuccess: () => {
          setDialogOpen(false)
          setEditing(undefined)
        },
      },
    )
  }

  const openAmountAction = (house: House, action: Exclude<AmountAction, null>) => {
    setActionHouse(house)
    setAmountAction(action)
  }

  const handleAmountSubmit = (amount: number) => {
    if (!actionHouse) return
    actionMutation.mutate(
      {
        id: actionHouse.id,
        actionType: amountAction === "changeAmount" ? "ChangeAmount" : "Occupied",
        amount,
      },
      {
        onSuccess: () => {
          setAmountAction(null)
          setActionHouse(undefined)
        },
      },
    )
  }

  const handleMarkVacant = () => {
    if (!actionHouse) return
    actionMutation.mutate(
      {
        id: actionHouse.id,
        actionType: "Vacant",
        amount: null,
      },
      {
        onSuccess: () => {
          setVacantConfirmOpen(false)
          setActionHouse(undefined)
        },
      },
    )
  }

  const properties = propertiesQuery.data ?? []
  const propertiesLoading = propertiesQuery.isLoading

  return (
    <div>
      <PageHeader
        title="Houses"
        description="Manage houses across properties."
        actions={
          <Button onClick={() => setDialogOpen(true)}>
            <Plus className="h-4 w-4" />
            New House
          </Button>
        }
      />

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Select
          value={propertyId || "__none__"}
          onValueChange={(value) => {
            setPropertyId(value === "__none__" ? "" : value)
          }}
        >
          <SelectTrigger className="w-56" aria-label="Property filter">
            <SelectValue placeholder="Select Property" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="__none__">All Properties</SelectItem>
            {properties.map((property) => (
              <SelectItem key={property.id} value={property.id}>
                {property.name} – {property.area}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        <label htmlFor="search" className="text-sm">
          Search
        </label>
        <Input
          id="search"
          className="w-64"
          placeholder="Search house number or floor…"
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") handleFilter()
          }}
        />
        <Button variant="secondary" onClick={handleFilter}>
          Filter
        </Button>
        <Button variant="outline" onClick={handleClear}>
          Clear
        </Button>
      </div>

      {isError ? (
        <p className="text-sm text-destructive">
          Failed to load houses. Please try again.
        </p>
      ) : (
        <HouseTable
          data={data}
          loading={isFetching || propertiesLoading}
          page={page}
          size={PAGE_SIZE}
          onPageChange={setPage}
          onEdit={(house) => {
            setEditing(house)
            setDialogOpen(true)
          }}
          onChangeAmount={(house) => openAmountAction(house, "changeAmount")}
          onMarkOccupied={(house) => openAmountAction(house, "markOccupied")}
          onMarkVacant={(house) => {
            setActionHouse(house)
            setVacantConfirmOpen(true)
          }}
        />
      )}

      <HouseDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        properties={properties}
        house={editing}
        submitting={saveMutation.isPending}
        onSubmit={handleSubmit}
      />

      <HouseAmountDialog
        open={amountAction !== null}
        onOpenChange={(next) => setAmountAction(next ? amountAction : null)}
        title={amountAction === "markOccupied" ? "Mark Occupied" : "Change Amount"}
        description={
          amountAction === "markOccupied"
            ? "Set the monthly rent and mark this house as occupied."
            : "Set a new monthly rent for this house."
        }
        house={actionHouse}
        confirmLabel={amountAction === "markOccupied" ? "Mark Occupied" : "Change Amount"}
        submitting={actionMutation.isPending}
        onSubmit={handleAmountSubmit}
      />

      <ConfirmDialog
        open={vacantConfirmOpen}
        onOpenChange={setVacantConfirmOpen}
        title="Mark House Vacant"
        description={
          actionHouse
            ? `Mark house ${actionHouse.number} (${actionHouse.floor}) as vacant? This clears the current monthly rent.`
            : undefined
        }
        confirmLabel="Mark Vacant"
        destructive
        loading={actionMutation.isPending}
        onConfirm={handleMarkVacant}
      />
    </div>
  )
}