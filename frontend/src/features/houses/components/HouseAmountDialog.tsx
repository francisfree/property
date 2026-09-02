import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import * as z from "zod"

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
import type { House } from "@/types/api"

const amountSchema = z.object({
  amount: z
    .string()
    .min(1, "Amount is required")
    .refine((value) => !isNaN(Number(value)), "Amount must be a number")
    .refine((value) => Number(value) >= 0, "Amount must be 0 or more"),
})

type AmountFormValues = z.infer<typeof amountSchema>

interface HouseAmountDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  title: string
  description: string
  house?: House
  confirmLabel?: string
  submitting: boolean
  onSubmit: (amount: number) => void
}

export function HouseAmountDialog({
  open,
  onOpenChange,
  title,
  description,
  house,
  confirmLabel = "Save",
  submitting,
  onSubmit,
}: HouseAmountDialogProps) {
  const form = useForm<AmountFormValues>({
    resolver: zodResolver(amountSchema),
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
          <DialogTitle>{title}</DialogTitle>
          <DialogDescription>{description}</DialogDescription>
        </DialogHeader>
        {house ? (
          <div className="grid grid-cols-2 gap-2 text-sm">
            <span className="text-muted-foreground">Property</span>
            <span>{house.propertyName}</span>
            <span className="text-muted-foreground">House</span>
            <span>
              {house.number} ({house.floor})
            </span>
            <span className="text-muted-foreground">Current Rent</span>
            <span>{formatMoney(house.currentMonthlyRent)}</span>
          </div>
        ) : null}
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit((values) =>
              onSubmit(Number(values.amount)),
            )}
            className="space-y-4"
            noValidate
          >
            <FormField
              control={form.control}
              name="amount"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>{confirmLabel === "Change Amount" ? "New Amount" : "Amount (KSh)"}</FormLabel>
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
                {submitting ? "Saving…" : confirmLabel}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}