import { z } from "zod"
import { IDENTIFICATION_TYPES } from "@/lib/constants"

export const rentalCreateSchema = z.object({
  propertyPublicId: z.string().trim().min(1, "Property is required"),
  housePublicId: z.string().trim().min(1, "House is required"),
  amount: z
    .string()
    .min(1, "Amount is required")
    .refine((value) => !isNaN(Number(value)), "Amount must be a number")
    .refine((value) => Number(value) >= 0, "Amount must be 0 or more"),
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

export type RentalCreateValues = z.infer<typeof rentalCreateSchema>

export const rentalCreateDefaults: RentalCreateValues = {
  propertyPublicId: "",
  housePublicId: "",
  amount: "",
  firstName: "",
  lastName: "",
  otherName: "",
  identificationType: "NATIONAL_ID",
  identificationNumber: "",
  nationality: "",
  phoneNumber: "",
}

export const rentalChangeAmountSchema = z.object({
  amount: z
    .string()
    .min(1, "Amount is required")
    .refine((value) => !isNaN(Number(value)), "Amount must be a number")
    .refine((value) => Number(value) > 0, "New amount must be greater than 0"),
})

export type RentalChangeAmountValues = z.infer<typeof rentalChangeAmountSchema>

export const rentalFilterSchema = z.object({
  propertyId: z.string().trim().optional().or(z.literal("")),
  houseId: z.string().trim().optional().or(z.literal("")),
  accountStatus: z.string().trim().optional().or(z.literal("")),
  arrearStatus: z.string().trim().optional().or(z.literal("")),
  search: z.string().trim().optional().or(z.literal("")),
})

export type RentalFilterValues = z.infer<typeof rentalFilterSchema>