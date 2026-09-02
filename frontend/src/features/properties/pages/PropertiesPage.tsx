import { Plus } from "lucide-react"
import { useState } from "react"

import { PageHeader } from "@/components/PageHeader"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { useProperties, useSaveProperty } from "../hooks"
import type { PropertyFormValues } from "../schema"
import type { Property } from "@/types/api"
import { PropertyDialog } from "../components/PropertyDialog"
import { PropertyTable } from "../components/PropertyTable"

const PAGE_SIZE = 25

export function PropertiesPage() {
  const [search, setSearch] = useState("")
  const [searchInput, setSearchInput] = useState("")
  const [page, setPage] = useState(0)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Property | undefined>(undefined)

  const { data, isFetching, isError } = useProperties({
    page,
    size: PAGE_SIZE,
    search,
  })

  const saveMutation = useSaveProperty()

  const handleFilter = () => {
    setPage(0)
    setSearch(searchInput)
  }

  const handleClear = () => {
    setSearchInput("")
    setSearch("")
    setPage(0)
  }

  const handleOpenNew = () => {
    setEditing(undefined)
    setDialogOpen(true)
  }

  const handleEdit = (property: Property) => {
    setEditing(property)
    setDialogOpen(true)
  }

  const handleSubmit = (values: PropertyFormValues) => {
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

  return (
    <div>
      <PageHeader
        title="Properties"
        description="Manage rental properties."
        actions={
          <Button onClick={handleOpenNew}>
            <Plus className="h-4 w-4" />
            New Property
          </Button>
        }
      />

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <label htmlFor="search" className="text-sm">
          Search
        </label>
        <Input
          id="search"
          className="w-64"
          placeholder="Search properties…"
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
          Failed to load properties. Please try again.
        </p>
      ) : (
        <PropertyTable
          data={data}
          loading={isFetching}
          page={page}
          size={PAGE_SIZE}
          onPageChange={setPage}
          onEdit={handleEdit}
        />
      )}

      <PropertyDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        property={editing}
        submitting={saveMutation.isPending}
        onSubmit={handleSubmit}
      />
    </div>
  )
}