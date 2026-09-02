import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query"
import { toast } from "sonner"

import {
  changeRentalAmount,
  closeRentalAccount,
  createRental,
  fetchRentals,
  type RentalQueryParams,
} from "./api"
import type { RentalCreateValues } from "./schema"

const queryKeys = {
  all: ["rentals"] as const,
  list: (params: RentalQueryParams) => [...queryKeys.all, "list", params] as const,
}

export function useRentals(params: RentalQueryParams) {
  return useQuery({
    queryKey: queryKeys.list(params),
    queryFn: () => fetchRentals(params),
    placeholderData: keepPreviousData,
  })
}

export function useCreateRental() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (values: RentalCreateValues) => createRental(values),
    onSuccess: () => {
      toast.success("Rental created")
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export function useChangeRentalAmount() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, amount }: { id: string; amount: number }) =>
      changeRentalAmount(id, amount),
    onSuccess: () => {
      toast.success("Amount changed")
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export function useCloseRentalAccount() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: string) => closeRentalAccount(id),
    onSuccess: () => {
      toast.success("Account closed")
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}