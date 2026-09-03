import { z } from "zod"
import { IDENTIFICATION_TYPES } from "@/lib/constants"

export const personSchema = z.object({
  firstName: z.string().trim().min(1, "First name is required").max(250, "First name must be 250 characters or fewer"),
  lastName: z.string().trim().min(1, "Last name is required").max(250, "Last name must be 250 characters or fewer"),
  otherName: z.string().trim().max(250, "Other names must be 250 characters or fewer").optional().or(z.literal("")),
  identificationType: z.enum(IDENTIFICATION_TYPES, { message: "Identification type is required" }),
  identificationNumber: z.string().trim().min(1, "Identification value is required").max(50, "Identification value must be 50 characters or fewer"),
  nationality: z.string().trim().max(250, "Nationality must be 250 characters or fewer").optional().or(z.literal("")),
  phoneNumber: z
    .string()
    .trim()
    .min(1, "Phone number is required")
    .regex(/^(\+254|0)(7[0-9]|1[0-1])[0-9]{7}$/, "Invalid phone number"),
})

export type PersonFormValues = z.infer<typeof personSchema>

export const personFormDefaults: PersonFormValues = {
  firstName: "",
  lastName: "",
  otherName: "",
  identificationType: "NATIONAL_ID",
  identificationNumber: "",
  nationality: "",
  phoneNumber: "",
}