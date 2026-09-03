import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query"
import { toast } from "sonner"

import {
  createUser,
  disableUser,
  enableUser,
  fetchUsers,
  resetPassword,
  updateUser,
  type UserQueryParams,
} from "./api"
import type { CreateUserFormValues, UpdateUserFormValues, ResetPasswordFormValues } from "./schema"

const queryKeys = {
  all: ["users"] as const,
  list: (params: UserQueryParams) => [...queryKeys.all, "list", params] as const,
}

export function useUsers(params: UserQueryParams) {
  return useQuery({
    queryKey: queryKeys.list(params),
    queryFn: () => fetchUsers(params),
    placeholderData: keepPreviousData,
  })
}

export function useCreateUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (values: CreateUserFormValues) => createUser(values),
    onSuccess: () => {
      toast.success("User created")
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export function useUpdateUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, values }: { id: string; values: UpdateUserFormValues }) =>
      updateUser(id, values),
    onSuccess: () => {
      toast.success("User updated")
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export function useToggleUserStatus() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, enabled }: { id: string; enabled: boolean }) =>
      enabled ? enableUser(id) : disableUser(id),
    onSuccess: (_data, variables) => {
      toast.success(variables.enabled ? "User enabled" : "User disabled")
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export function useResetPassword() {
  return useMutation({
    mutationFn: ({ id, values }: { id: string; values: ResetPasswordFormValues }) =>
      resetPassword(id, values),
    onSuccess: () => {
      toast.success("Password reset")
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}
