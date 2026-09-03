import { apiGet, apiPost, apiPut, apiPatch, apiDelete } from "@/lib/api-client"
import type { PagedResponse } from "@/types/api"
import type { User } from "@/features/authentication/types"
import type { CreateUserFormValues, UpdateUserFormValues, ResetPasswordFormValues } from "./schema"

export interface UserQueryParams {
  page: number
  size: number
  search?: string
}

export async function fetchUsers(params: UserQueryParams): Promise<PagedResponse<User>> {
  return apiGet<PagedResponse<User>>("/users", {
    params: {
      page: params.page,
      size: params.size,
      search: params.search?.trim() || undefined,
    },
  })
}

export async function createUser(values: CreateUserFormValues): Promise<User> {
  return apiPost<User>("/users", values)
}

export async function updateUser(
  id: string,
  values: UpdateUserFormValues,
): Promise<User> {
  return apiPut<User>(`/users/${id}`, values)
}

export async function enableUser(id: string): Promise<void> {
  return apiPatch<void>(`/users/${id}/enable`)
}

export async function disableUser(id: string): Promise<void> {
  return apiPatch<void>(`/users/${id}/disable`)
}

export async function resetPassword(
  id: string,
  values: ResetPasswordFormValues,
): Promise<void> {
  return apiPost<void>(`/users/${id}/reset-password`, values)
}

export async function deleteUser(id: string): Promise<void> {
  return apiDelete<void>(`/users/${id}`)
}
