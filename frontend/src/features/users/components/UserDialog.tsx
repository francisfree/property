import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import type { User } from "@/features/authentication/types"
import type { CreateUserFormValues, UpdateUserFormValues } from "../schema"
import { UserForm } from "./UserForm"

interface UserDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  user?: User
  submitting: boolean
  onSubmit: (values: CreateUserFormValues | UpdateUserFormValues) => void
}

export function UserDialog({
  open,
  onOpenChange,
  user,
  submitting,
  onSubmit,
}: UserDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{user ? "Edit User" : "New User"}</DialogTitle>
          <DialogDescription>
            {user
              ? "Update user details and roles."
              : "Create a new user account."}
          </DialogDescription>
        </DialogHeader>
        <UserForm
          user={user}
          submitting={submitting}
          onSubmit={onSubmit}
          onCancel={() => onOpenChange(false)}
        />
      </DialogContent>
    </Dialog>
  )
}
