import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { formatMoney } from "@/lib/format"
import type { Rental } from "@/types/api"

interface CloseAccountDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  rental?: Rental
  onProceed: () => void
}

export function CloseAccountDialog({
  open,
  onOpenChange,
  rental,
  onProceed,
}: CloseAccountDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-sm">
        <DialogHeader>
          <DialogTitle>Close Account</DialogTitle>
          <DialogDescription>Review the rental details before closing the account.</DialogDescription>
        </DialogHeader>
        {rental ? (
          <div className="grid grid-cols-2 gap-2 text-sm">
            <span className="text-muted-foreground">Arrears Status</span>
            <span>{rental.arrearStatus}</span>
            <span className="text-muted-foreground">Account Status</span>
            <span>{rental.accountStatus}</span>
            <span className="text-muted-foreground">First Name</span>
            <span>{rental.person.firstName}</span>
            <span className="text-muted-foreground">Last Name</span>
            <span>{rental.person.lastName}</span>
            <span className="text-muted-foreground">Other Name</span>
            <span>{rental.person.otherName ?? "—"}</span>
            <span className="text-muted-foreground">Phone Number</span>
            <span>{rental.person.phoneNumber}</span>
            <span className="text-muted-foreground">Property</span>
            <span>{rental.house.propertyName}</span>
            <span className="text-muted-foreground">House Floor</span>
            <span>{rental.house.floor}</span>
            <span className="text-muted-foreground">House Number</span>
            <span>{rental.house.number}</span>
            <span className="text-muted-foreground">Amount</span>
            <span className="tabular-nums">{formatMoney(rental.amount)}</span>
          </div>
        ) : null}
        <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button type="button" variant="destructive" onClick={onProceed}>
            Close Account
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}