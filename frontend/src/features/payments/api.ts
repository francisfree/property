import { apiGet, apiPost } from "@/lib/api-client"
import type { PagedResponse } from "@/types/api"
import type { PaymentMonth } from "./types"

export interface PaymentQueryParams {
  page: number
  size: number
  revisionCount: number
  month: string
  blockName: string
  searchParam?: string
}

export async function fetchPayments(
  params: PaymentQueryParams,
): Promise<PagedResponse<PaymentMonth>> {
  return apiGet<PagedResponse<PaymentMonth>>("/payments", {
    params: {
      page: params.page,
      size: params.size,
      revisionCount: params.revisionCount,
      month: params.month,
      blockName: params.blockName,
      searchParam: params.searchParam || undefined,
    },
  })
}

export async function uploadPaymentFile(
  file: File,
  month: string,
  blockName: string,
): Promise<void> {
  const formData = new FormData()
  formData.append("file", file)
  formData.append("month", month)
  formData.append("blockName", blockName)

  await apiPost<void>("/payments/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  })
}

export async function fetchBlockNames(month: string): Promise<string[]> {
  return apiGet<string[]>("/payments/filters/blockNames", {
    params: { month },
  })
}

export async function fetchRevisionCounts(
  month: string,
  blockName: string,
): Promise<number[]> {
  return apiGet<number[]>("/payments/filters/revisionCount", {
    params: { month, blockName },
  })
}
