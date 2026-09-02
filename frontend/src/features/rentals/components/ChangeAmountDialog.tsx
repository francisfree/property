import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"

import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { formatMoney } from "@/lib/format"
import type { Rental } from "@/types/api"
import {
  rentalChangeAmountSchema,
  type RentalChangeAmountValues,
} from "../schema"

interface ChangeAmountDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  rental?: Rental
  submitting: boolean
  onSubmit: (amount: number) => void
}

export function ChangeAmountDialog({
  open,
  onOpenChange,
  rental,
  submitting,
  onSubmit,
}: ChangeAmountDialogProps) {
  const form = useForm<RentalChangeAmountValues>({
    resolver: zodResolver(rentalChangeAmountSchema),
    defaultValues: { amount: "" },
  })

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        if (!submitting) {
          form.reset({ amount: "" })
          onOpenChange(next)
        }
      }}
    >
      <DialogContent className="sm:max-w-sm">
        <DialogHeader>
          <DialogTitle>Change Amount</DialogTitle>
          <DialogDescription>Update the monthly amount for this rental.</DialogDescription>
        </DialogHeader>
        {rental ? (
          <div className="grid grid-cols-2 gap-2 text-sm">
            <span className="text-muted-foreground">Property</span>
            <span>{rental.house.propertyName}</span>
            <span className="text-muted-foreground">House Floor</span>
            <span>{rental.house.floor}</span>
            <span className="text-muted-foreground">House Number</span>
            <span>{rental.house.number}</span>
            <span className="text-muted-foreground">Current Amount</span>
            <span className="tabular-nums">{formatMoney(rental.amount)}</span>
          </div>
        ) : null}
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit((values) => onSubmit(Number(values.amount)))}
            className="space-y-4"
            noValidate
          >
            <FormField
              control={form.control}
              name="amount"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>New Amount</FormLabel>
                  <FormControl>
                    <Input {...field} inputMode="decimal" placeholder="0.00" />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
                disabled={submitting}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={submitting}>
                {submitting ? "Saving…" : "Change Amount"}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}