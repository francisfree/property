import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query"
import { toast } from "sonner"

import {
  createProperty,
  fetchProperties,
  updateProperty,
  type PropertyQueryParams,
} from "./api"
import type { PropertyFormValues } from "./schema"

const queryKeys = {
  all: ["properties"] as const,
  list: (params: PropertyQueryParams) => [...queryKeys.all, "list", params] as const,
}

export function useProperties(params: PropertyQueryParams) {
  return useQuery({
    queryKey: queryKeys.list(params),
    queryFn: () => fetchProperties(params),
    placeholderData: keepPreviousData,
  })
}

export function useSaveProperty() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, values }: { id?: string; values: PropertyFormValues }) =>
      id ? updateProperty(id, values) : createProperty(values),
    onSuccess: (_data, variables) => {
      toast.success(variables.id ? "Property updated" : "Property created")
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}