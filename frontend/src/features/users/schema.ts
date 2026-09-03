import { z } from "zod"

export const userSchema = z
  .object({
    username: z
      .string()
      .trim()
      .min(3, "Username must be at least 3 characters")
      .max(100, "Username must be 100 characters or fewer"),
    email: z.string().trim().email("Email must be valid"),
    password: z
      .string()
      .min(6, "Password must be at least 6 characters")
      .optional(),
    firstName: z.string().trim().optional(),
    lastName: z.string().trim().optional(),
    roleNames: z.array(z.string()).min(1, "At least one role is required"),
  })
  .refine((data) => {
    if (data.password !== undefined && data.password.length < 6) {
      return false
    }
    return true
  })

export const createUserSchema = z.object({
  username: z
    .string()
    .trim()
    .min(3, "Username must be at least 3 characters")
    .max(100, "Username must be 100 characters or fewer"),
  email: z.string().trim().email("Email must be valid"),
  password: z.string().min(6, "Password must be at least 6 characters"),
  firstName: z.string().trim().optional(),
  lastName: z.string().trim().optional(),
  roleNames: z.array(z.string()).min(1, "At least one role is required"),
})

export const updateUserSchema = z.object({
  email: z.string().trim().email("Email must be valid"),
  firstName: z.string().trim().optional(),
  lastName: z.string().trim().optional(),
  enabled: z.boolean(),
  roleNames: z.array(z.string()).min(1, "At least one role is required"),
})

export type CreateUserFormValues = z.infer<typeof createUserSchema>
export type UpdateUserFormValues = z.infer<typeof updateUserSchema>

export const createUserFormDefaults: CreateUserFormValues = {
  username: "",
  email: "",
  password: "",
  firstName: "",
  lastName: "",
  roleNames: ["USER"],
}

export const resetPasswordSchema = z.object({
  newPassword: z.string().min(6, "Password must be at least 6 characters"),
})

export type ResetPasswordFormValues = z.infer<typeof resetPasswordSchema>
