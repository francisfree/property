import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"

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
import { FLOORS } from "@/lib/constants"
import type { Property } from "@/types/api"
import {
  houseFormDefaults,
  houseSchema,
  type HouseFormValues,
} from "../schema"

interface HouseFormProps {
  properties: Property[]
  initialValues?: HouseFormValues
  submitting: boolean
  onSubmit: (values: HouseFormValues) => void
  onCancel?: () => void
}

export function HouseForm({
  properties,
  initialValues,
  submitting,
  onSubmit,
  onCancel,
}: HouseFormProps) {
  const form = useForm<HouseFormValues>({
    resolver: zodResolver(houseSchema),
    defaultValues: initialValues ?? houseFormDefaults,
  })

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4" noValidate>
        <FormField
          control={form.control}
          name="number"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Number</FormLabel>
              <FormControl>
                <Input {...field} placeholder="House number" />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="floor"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Floor</FormLabel>
              <Select onValueChange={field.onChange} value={field.value}>
                <FormControl>
                  <SelectTrigger aria-label="Floor">
                    <SelectValue placeholder="Select One" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  {FLOORS.map((floor) => (
                    <SelectItem key={floor} value={floor}>
                      {floor}
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
        <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          {onCancel ? (
            <Button type="button" variant="outline" onClick={onCancel} disabled={submitting}>
              Cancel
            </Button>
          ) : null}
          <Button type="submit" disabled={submitting}>
            {submitting ? "Saving…" : "Save"}
          </Button>
        </div>
      </form>
    </Form>
  )
}