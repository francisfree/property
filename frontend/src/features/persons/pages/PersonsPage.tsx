import { Plus } from "lucide-react"
import { useState } from "react"

import { PageHeader } from "@/components/PageHeader"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import type { Person } from "@/types/api"
import { PersonDialog } from "../components/PersonDialog"
import { PersonTable } from "../components/PersonTable"
import { usePersons, useSavePerson } from "../hooks"
import type { PersonFormValues } from "../schema"

const PAGE_SIZE = 25

export function PersonsPage() {
  const [searchInput, setSearchInput] = useState("")
  const [search, setSearch] = useState("")
  const [page, setPage] = useState(0)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Person | undefined>(undefined)

  const { data, isFetching, isError } = usePersons({
    page,
    size: PAGE_SIZE,
    search,
  })

  const saveMutation = useSavePerson()

  const handleFilter = () => {
    setPage(0)
    setSearch(searchInput)
  }

  const handleClear = () => {
    setSearchInput("")
    setSearch("")
    setPage(0)
  }

  const handleSubmit = (values: PersonFormValues) => {
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
        title="Persons"
        description="Manage persons and tenants."
        actions={
          <Button onClick={() => setDialogOpen(true)}>
            <Plus className="h-4 w-4" />
            New Person
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
          placeholder="Search persons…"
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
          Failed to load persons. Please try again.
        </p>
      ) : (
        <PersonTable
          data={data}
          loading={isFetching}
          page={page}
          size={PAGE_SIZE}
          onPageChange={setPage}
          onEdit={(person) => {
            setEditing(person)
            setDialogOpen(true)
          }}
        />
      )}

      <PersonDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        person={editing}
        submitting={saveMutation.isPending}
        onSubmit={handleSubmit}
      />
    </div>
  )
}