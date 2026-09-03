import { Plus } from "lucide-react"
import { useState } from "react"

import { PageHeader } from "@/components/PageHeader"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { useUsers, useCreateUser, useUpdateUser } from "../hooks"
import type { CreateUserFormValues, UpdateUserFormValues } from "../schema"
import type { User } from "@/features/authentication/types"
import { UserDialog } from "../components/UserDialog"
import { UserTable } from "../components/UserTable"

const PAGE_SIZE = 25

export function UsersPage() {
  const [search, setSearch] = useState("")
  const [searchInput, setSearchInput] = useState("")
  const [page, setPage] = useState(0)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<User | undefined>(undefined)

  const { data, isFetching, isError } = useUsers({
    page,
    size: PAGE_SIZE,
    search,
  })

  const createMutation = useCreateUser()
  const updateMutation = useUpdateUser()

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

  const handleEdit = (user: User) => {
    setEditing(user)
    setDialogOpen(true)
  }

  const handleSubmit = (
    values: CreateUserFormValues | UpdateUserFormValues,
  ) => {
    if (editing) {
      updateMutation.mutate(
        { id: editing.id, values: values as UpdateUserFormValues },
        {
          onSuccess: () => {
            setDialogOpen(false)
            setEditing(undefined)
          },
        },
      )
    } else {
      createMutation.mutate(values as CreateUserFormValues, {
        onSuccess: () => {
          setDialogOpen(false)
          setEditing(undefined)
        },
      })
    }
  }

  return (
    <div>
      <PageHeader
        title="Users"
        description="Manage user accounts and roles."
        actions={
          <Button onClick={handleOpenNew}>
            <Plus className="h-4 w-4" />
            New User
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
          placeholder="Search users…"
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
          Failed to load users. Please try again.
        </p>
      ) : (
        <UserTable
          data={data}
          loading={isFetching}
          page={page}
          size={PAGE_SIZE}
          onPageChange={setPage}
          onEdit={handleEdit}
        />
      )}

      <UserDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        user={editing}
        submitting={editing ? updateMutation.isPending : createMutation.isPending}
        onSubmit={handleSubmit}
      />
    </div>
  )
}
