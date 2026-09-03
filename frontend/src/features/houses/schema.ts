import { z } from "zod"
import { FLOORS } from "@/lib/constants"

export const houseSchema = z.object({
  number: z.string().trim().min(1, "Number is required").max(250, "Number must be 250 characters or fewer"),
  floor: z.enum(FLOORS, { message: "Floor is required" }),
  propertyPublicId: z.string().trim().min(1, "Property is required"),
})

export type HouseFormValues = z.infer<typeof houseSchema>

export const houseFormDefaults: HouseFormValues = {
  number: "",
  floor: FLOORS[0],
  propertyPublicId: "",
}