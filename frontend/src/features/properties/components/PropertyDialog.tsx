import { useEffect } from "react"

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import type { Property } from "@/types/api"
import { PropertyForm } from "./Form"
import type { PropertyFormValues } from "../schema"

interface PropertyDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  property?: Property
  submitting: boolean
  onSubmit: (values: PropertyFormValues) => void
}

export function PropertyDialog({
  open,
  onOpenChange,
  property,
  submitting,
  onSubmit,
}: PropertyDialogProps) {
  useEffect(() => {
    if (!open) return
  }, [open])

  const isEdit = Boolean(property)

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{isEdit ? "Edit Property" : "New Property"}</DialogTitle>
          <DialogDescription>
            {isEdit
              ? "Update the property details below."
              : "Fill in the details to create a new property."}
          </DialogDescription>
        </DialogHeader>
        <PropertyForm
          key={property?.id ?? "new"}
          initialValues={
            property
              ? {
                  name: property.name,
                  location: property.location,
                  area: property.area,
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