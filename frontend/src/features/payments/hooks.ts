import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query"
import { toast } from "sonner"

import {
  fetchPayments,
  fetchBlockNames,
  fetchRevisionCounts,
  uploadPaymentFile,
  type PaymentQueryParams,
} from "./api"

const queryKeys = {
  all: ["payments"] as const,
  list: (params: PaymentQueryParams) => [...queryKeys.all, "list", params] as const,
  blockNames: (month: string) => [...queryKeys.all, "blockNames", month] as const,
  revisionCounts: (month: string, blockName: string) =>
    [...queryKeys.all, "revisionCounts", month, blockName] as const,
}

export function usePayments(params: PaymentQueryParams) {
  return useQuery({
    queryKey: queryKeys.list(params),
    queryFn: () => fetchPayments(params),
    placeholderData: keepPreviousData,
  })
}

export function useUploadPayment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      file,
      month,
      blockName,
    }: {
      file: File
      month: string
      blockName: string
    }) => uploadPaymentFile(file, month, blockName),
    onSuccess: () => {
      toast.success("Payment file uploaded")
      void queryClient.invalidateQueries({ queryKey: queryKeys.all })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export function useBlockNames(month: string) {
  return useQuery({
    queryKey: queryKeys.blockNames(month),
    queryFn: () => fetchBlockNames(month),
    enabled: !!month,
  })
}

export function useRevisionCounts(month: string, blockName: string) {
  return useQuery({
    queryKey: queryKeys.revisionCounts(month, blockName),
    queryFn: () => fetchRevisionCounts(month, blockName),
    enabled: !!month && !!blockName,
  })
}
