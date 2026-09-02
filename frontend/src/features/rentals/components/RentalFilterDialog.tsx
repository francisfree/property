import { zodResolver } from "@hookform/resolvers/zod"
import { useQuery } from "@tanstack/react-query"
import { useEffect } from "react"
import { useForm, useWatch } from "react-hook-form"

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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { fetchAllHouses } from "@/features/houses/api"
import { ACCOUNT_STATUSES, ARREAR_STATUSES } from "@/lib/constants"
import type { Property } from "@/types/api"
import {
  rentalFilterSchema,
  type RentalFilterValues,
} from "../schema"

interface RentalFilterDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  properties: Property[]
  initialValues?: RentalFilterValues
  onApply: (values: RentalFilterValues) => void
}

export function RentalFilterDialog({
  open,
  onOpenChange,
  properties,
  initialValues,
  onApply,
}: RentalFilterDialogProps) {
  const form = useForm<RentalFilterValues>({
    resolver: zodResolver(rentalFilterSchema),
    defaultValues: initialValues ?? {
      propertyId: "",
      houseId: "",
      accountStatus: "",
      arrearStatus: "",
      search: "",
    },
  })

  const propertyId = useWatch({ control: form.control, name: "propertyId" })

  const housesQuery = useQuery({
    queryKey: ["houses", "list", { propertyId: propertyId || undefined }],
    queryFn: () => fetchAllHouses(propertyId || undefined),
    enabled: Boolean(propertyId),
  })

  useEffect(() => {
    form.setValue("houseId", "")
  }, [propertyId, form])

  const houses = housesQuery.data ?? []
  const housesLoading = housesQuery.isLoading

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Filter Rentals</DialogTitle>
          <DialogDescription>
            Narrow down the rental list using any combination of filters.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onApply)}
            className="space-y-4"
            noValidate
          >
            <FormField
              control={form.control}
              name="propertyId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Property</FormLabel>
                  <Select onValueChange={field.onChange} value={field.value}>
                    <FormControl>
                      <SelectTrigger aria-label="Property">
                        <SelectValue placeholder="All Properties" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectItem value="__all__">All Properties</SelectItem>
                      {properties.map((property) => (
                        <SelectItem key={property.id} value={property.id}>
                          {property.name} – {property.area}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="houseId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>House</FormLabel>
                  <Select onValueChange={field.onChange} value={field.value}>
                    <FormControl>
                      <SelectTrigger
                        aria-label="House"
                        disabled={!propertyId || housesLoading}
                      >
                        <SelectValue
                          placeholder={
                            !propertyId
                              ? "Select a property first"
                              : housesLoading
                                ? "Loading houses…"
                                : "All Houses"
                          }
                        />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectItem value="__all__">All Houses</SelectItem>
                      {houses.map((house) => (
                        <SelectItem key={house.id} value={house.id}>
                          {house.number} – {house.floor} ({house.propertyName})
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                control={form.control}
                name="accountStatus"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Account Status</FormLabel>
                    <Select onValueChange={field.onChange} value={field.value}>
                      <FormControl>
                        <SelectTrigger aria-label="Account status">
                          <SelectValue placeholder="Any" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="__all__">Any</SelectItem>
                        {ACCOUNT_STATUSES.map((status) => (
                          <SelectItem key={status} value={status}>
                            {status}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="arrearStatus"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Arrear Status</FormLabel>
                    <Select onValueChange={field.onChange} value={field.value}>
                      <FormControl>
                        <SelectTrigger aria-label="Arrear status">
                          <SelectValue placeholder="Any" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="__all__">Any</SelectItem>
                        {ARREAR_STATUSES.map((status) => (
                          <SelectItem key={status} value={status}>
                            {status}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
            <FormField
              control={form.control}
              name="search"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Search</FormLabel>
                  <FormControl>
                    <Input {...field} placeholder="Search name, phone, house…" />
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
              >
                Cancel
              </Button>
              <Button type="submit">Filter</Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}