import { useEffect } from "react"

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import type { House, Property } from "@/types/api"
import { HouseForm } from "./HouseForm"
import type { HouseFormValues } from "../schema"

interface HouseDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  properties: Property[]
  house?: House
  submitting: boolean
  onSubmit: (values: HouseFormValues) => void
}

export function HouseDialog({
  open,
  onOpenChange,
  properties,
  house,
  submitting,
  onSubmit,
}: HouseDialogProps) {
  useEffect(() => {
    if (!open) return
  }, [open])

  const isEdit = Boolean(house)

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{isEdit ? "Edit House" : "New House"}</DialogTitle>
          <DialogDescription>
            {isEdit
              ? "Update the house details below."
              : "Fill in the details to create a new house."}
          </DialogDescription>
        </DialogHeader>
        <HouseForm
          key={house?.id ?? "new"}
          properties={properties}
          initialValues={
            house
              ? {
                  number: house.number,
                  floor: house.floor,
                  propertyPublicId: house.propertyId,
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