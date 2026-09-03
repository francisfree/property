import { zodResolver } from "@hookform/resolvers/zod"
import { useQuery } from "@tanstack/react-query"
import { useEffect } from "react"
import { useForm, useWatch } from "react-hook-form"

import { Button } from "@/components/ui/button"
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
import { IDENTIFICATION_TYPES } from "@/lib/constants"
import type { Property } from "@/types/api"
import {
  rentalCreateDefaults,
  rentalCreateSchema,
  type RentalCreateValues,
} from "../schema"

interface RentalFormProps {
  properties: Property[]
  submitting: boolean
  onSubmit: (values: RentalCreateValues) => void
  onCancel?: () => void
}

export function RentalForm({
  properties,
  submitting,
  onSubmit,
  onCancel,
}: RentalFormProps) {
  const form = useForm<RentalCreateValues>({
    resolver: zodResolver(rentalCreateSchema),
    defaultValues: rentalCreateDefaults,
  })

  const propertyId = useWatch({ control: form.control, name: "propertyPublicId" })

  const housesQuery = useQuery({
    queryKey: ["houses", "list", { propertyId: propertyId || undefined }],
    queryFn: () => fetchAllHouses(propertyId || undefined),
    enabled: Boolean(propertyId),
  })

  useEffect(() => {
    form.setValue("housePublicId", "")
  }, [propertyId, form])

  const houses = housesQuery.data ?? []
  const housesLoading = housesQuery.isLoading

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4" noValidate>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField
            control={form.control}
            name="propertyPublicId"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Property</FormLabel>
                <Select onValueChange={field.onChange} value={field.value}>
                  <FormControl>
                    <SelectTrigger aria-label="Property">
                      <SelectValue placeholder="Select Property" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
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
            name="housePublicId"
            render={({ field }) => (
              <FormItem>
                <FormLabel>House</FormLabel>
                <Select onValueChange={field.onChange} value={field.value}>
                  <FormControl>
                    <SelectTrigger aria-label="House" disabled={!propertyId || housesLoading}>
                      <SelectValue
                        placeholder={
                          !propertyId
                            ? "Select a property first"
                            : housesLoading
                              ? "Loading houses…"
                              : "Select House"
                        }
                      />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
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
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField
            control={form.control}
            name="amount"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Amount</FormLabel>
                <FormControl>
                  <Input {...field} inputMode="decimal" placeholder="e.g. 12000" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <div />
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField
            control={form.control}
            name="firstName"
            render={({ field }) => (
              <FormItem>
                <FormLabel>First Name</FormLabel>
                <FormControl>
                  <Input {...field} placeholder="First name" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="lastName"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Surname</FormLabel>
                <FormControl>
                  <Input {...field} placeholder="Surname" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>
        <FormField
          control={form.control}
          name="otherName"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Other Names</FormLabel>
              <FormControl>
                <Input {...field} placeholder="Other names (optional)" />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField
            control={form.control}
            name="identificationType"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Identification Type</FormLabel>
                <Select onValueChange={field.onChange} value={field.value}>
                  <FormControl>
                    <SelectTrigger aria-label="Identification type">
                      <SelectValue placeholder="Select type" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    {IDENTIFICATION_TYPES.map((type) => (
                      <SelectItem key={type} value={type}>
                        {type}
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
            name="identificationNumber"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Identification Value</FormLabel>
                <FormControl>
                  <Input {...field} placeholder="Identification number" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField
            control={form.control}
            name="phoneNumber"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Phone Number</FormLabel>
                <FormControl>
                  <Input {...field} placeholder="e.g. 0712345678 or +254712345678" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="nationality"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Nationality</FormLabel>
                <FormControl>
                  <Input {...field} placeholder="Nationality (optional)" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>
        <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          {onCancel ? (
            <Button type="button" variant="outline" onClick={onCancel} disabled={submitting}>
              Cancel
            </Button>
          ) : null}
          <Button type="submit" disabled={submitting} className="min-w-24">
            {submitting ? "Saving…" : "Save"}
          </Button>
        </div>
      </form>
    </Form>
  )
}