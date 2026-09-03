import { z } from "zod"

export const propertySchema = z.object({
  name: z.string().trim().min(1, "Name is required").max(250, "Name must be 250 characters or fewer"),
  location: z.string().trim().min(1, "Location is required").max(250, "Location must be 250 characters or fewer"),
  area: z.string().trim().min(1, "Area is required").max(250, "Area must be 250 characters or fewer"),
})

export type PropertyFormValues = z.infer<typeof propertySchema>

export const propertyFormDefaults: PropertyFormValues = {
  name: "",
  location: "",
  area: "",
}