import { useEffect } from "react"

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import type { Person } from "@/types/api"
import { PersonForm } from "./PersonForm"
import type { PersonFormValues } from "../schema"

interface PersonDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  person?: Person
  submitting: boolean
  onSubmit: (values: PersonFormValues) => void
}

export function PersonDialog({
  open,
  onOpenChange,
  person,
  submitting,
  onSubmit,
}: PersonDialogProps) {
  useEffect(() => {
    if (!open) return
  }, [open])

  const isEdit = Boolean(person)

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>{isEdit ? "Edit Person" : "New Person"}</DialogTitle>
          <DialogDescription>
            {isEdit
              ? "Update the person details below."
              : "Fill in the details to create a new person."}
          </DialogDescription>
        </DialogHeader>
        <PersonForm
          key={person?.id ?? "new"}
          initialValues={
            person
              ? {
                  firstName: person.firstName,
                  lastName: person.lastName,
                  otherName: person.otherName ?? "",
                  identificationType: person.identificationType,
                  identificationNumber: person.identificationNumber,
                  nationality: person.nationality ?? "",
                  phoneNumber: person.phoneNumber,
                }
              : undefined
          }
          submitting={submitting}
          onSubmit={onSubmit}
          onCancel={() => onOpenChange(false)}
        />
      </DialogContent>
    </Dialog>
  )
}