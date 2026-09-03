import { type ColumnDef } from "@tanstack/react-table"
import { MoreHorizontal, Power, KeyRound } from "lucide-react"
import { useState } from "react"

import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { DataTable } from "@/components/DataTable"
import type { User } from "@/features/authentication/types"
import type { PagedResponse } from "@/types/api"
import { useResetPassword, useToggleUserStatus } from "../hooks"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { resetPasswordSchema, type ResetPasswordFormValues } from "../schema"

interface UserTableProps {
  data: PagedResponse<User> | undefined
  loading: boolean
  page: number
  size: number
  onPageChange: (page: number) => void
  onEdit: (user: User) => void
}

function ResetPasswordButton({ userId }: { userId: string }) {
  const [open, setOpen] = useState(false)
  const resetPasswordMutation = useResetPassword()

  const form = useForm<ResetPasswordFormValues>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: { newPassword: "" },
  })

  const onSubmit = (values: ResetPasswordFormValues) => {
    resetPasswordMutation.mutate(
      { id: userId, values },
      {
        onSuccess: () => {
          setOpen(false)
          form.reset()
        },
        onError: () => {
          form.setError("newPassword", { message: "Failed to reset password" })
        },
      },
    )
  }

  return (
    <>
      <DropdownMenuItem
        onSelect={(e) => {
          e.preventDefault()
          e.stopPropagation()
          setOpen(true)
        }}
      >
        <KeyRound className="mr-2 h-4 w-4" />
        Reset Password
      </DropdownMenuItem>
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Reset Password</DialogTitle>
            <DialogDescription>
              Set a new password for this user.
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="newPassword">New Password</Label>
              <Input
                id="newPassword"
                type="password"
                placeholder="Enter new password"
                {...form.register("newPassword")}
              />
              {form.formState.errors.newPassword && (
                <p className="text-sm text-destructive">
                  {form.formState.errors.newPassword.message}
                </p>
              )}
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => setOpen(false)}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={resetPasswordMutation.isPending}>
                {resetPasswordMutation.isPending ? "Resetting..." : "Reset Password"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </>
  )
}

export function UserTable({
  data,
  loading,
  page,
  size,
  onPageChange,
  onEdit,
}: UserTableProps) {
  const toggleStatus = useToggleUserStatus()

  const columns: ColumnDef<User>[] = [
    {
      accessorKey: "username",
      header: "Username",
    },
    {
      accessorKey: "firstName",
      header: "First Name",
      cell: ({ row }) => row.original.firstName || "—",
    },
    {
      accessorKey: "lastName",
      header: "Last Name",
      cell: ({ row }) => row.original.lastName || "—",
    },
    {
      accessorKey: "email",
      header: "Email",
    },
    {
      accessorKey: "roles",
      header: "Roles",
      cell: ({ row }) => (
        <div className="flex flex-wrap gap-1">
          {row.original.roles.map((role) => (
            <Badge key={role} variant="secondary">
              {role}
            </Badge>
          ))}
        </div>
      ),
    },
    {
      accessorKey: "enabled",
      header: "Status",
      cell: ({ row }) =>
        row.original.enabled ? (
          <Badge className="bg-green-600">Active</Badge>
        ) : (
          <Badge variant="destructive">Disabled</Badge>
        ),
    },
    {
      id: "actions",
      header: "",
      cell: ({ row }) => {
        const user = row.original
        return (
          <div onClick={(e) => e.stopPropagation()}>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon">
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onSelect={() => onEdit(user)}>
                  Edit
                </DropdownMenuItem>
                <DropdownMenuItem
                  onSelect={() =>
                    toggleStatus.mutate({ id: user.id, enabled: !user.enabled })
                  }
                >
                  <Power className="mr-2 h-4 w-4" />
                  {user.enabled ? "Disable" : "Enable"}
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <ResetPasswordButton userId={user.id} />
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        )
      },
    },
  ]

  const pageCount = data?.totalPages ?? 0

  return (
    <DataTable
      columns={columns}
      data={data?.content ?? []}
      loading={loading}
      rowKey={(row) => row.id}
      pagination={{
        pageIndex: page,
        pageSize: size,
        pageCount,
        total: data?.totalElements ?? 0,
        onPageChange,
      }}
      emptyMessage="No users found."
    />
  )
}
