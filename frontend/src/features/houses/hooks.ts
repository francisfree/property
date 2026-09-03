import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query"
import { toast } from "sonner"

import {
  createHouse,
  fetchHouses,
  runHouseAction,
  updateHouse,
  type HouseActionType,
  type HouseQueryParams,
} from "./api"
import type { HouseFormValues } from "./schema"

const queryKeys = {
  all: ["houses"] as const,
  list: (params: HouseQueryParams) => [...queryKeys.all, "list", params] as const,
}

export function useHouses(params: HouseQueryParams) {
  return useQuery({
    queryKey: queryKeys.list(params),
    queryFn: () => fetchHouses(params),
    placeholderData: keepPreviousData,
  })
}

export function useSaveHouse() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, values }: { id?: string; values: HouseFormValues }) =>
      id ? updateHouse(id, values) : createHouse(values),
    onSuccess: (_data, variables) => {
      toast.success(variables.id ? "House updated" : "House created")
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export function useHouseAction() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      id,
      actionType,
      amount,
    }: {
      id: string
      actionType: HouseActionType
      amount?: number | null
    }) => runHouseAction(id, actionType, amount),
    onSuccess: (_data, variables) => {
      const labels: Record<HouseActionType, string> = {
        Occupied: "House marked as occupied",
        Vacant: "House marked as vacant",
        ChangeAmount: "House amount changed",
      }
      toast.success(labels[variables.actionType])
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}