import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query"
import { toast } from "sonner"

import {
  createPerson,
  fetchAllPersons,
  fetchPersons,
  updatePerson,
  type PersonQueryParams,
} from "./api"
import type { PersonFormValues } from "./schema"

const queryKeys = {
  all: ["persons"] as const,
  list: (params: PersonQueryParams) => [...queryKeys.all, "list", params] as const,
}

export function usePersons(params: PersonQueryParams) {
  return useQuery({
    queryKey: queryKeys.list(params),
    queryFn: () => fetchPersons(params),
    placeholderData: keepPreviousData,
  })
}

export function useAllPersons() {
  return useQuery({
    queryKey: queryKeys.all,
    queryFn: () => fetchAllPersons(),
  })
}

export function useSavePerson() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, values }: { id?: string; values: PersonFormValues }) =>
      id ? updatePerson(id, values) : createPerson(values),
    onSuccess: (_data, variables) => {
      toast.success(variables.id ? "Person updated" : "Person created")
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}