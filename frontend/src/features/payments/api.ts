import { apiGet, apiPost } from "@/lib/api-client"
import type { PagedResponse } from "@/types/api"
import type { RentalPayment } from "./types"

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
): Promise<PagedResponse<RentalPayment>> {
  return apiGet<PagedResponse<RentalPayment>>("/payments", {
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

export async function fetchPaymentReceipt(publicId: string): Promise<Blob> {
  return apiGet<Blob>(`/payments/receipts/${publicId}`, {
    responseType: "blob",
  })
}

export async function fetchPaymentReceipts(publicIds: string[]): Promise<Blob> {
  return apiPost<Blob>(
    "/payments/receipts",
    { publicIds },
    { responseType: "blob" },
  )
}

export async function fetchAllPaymentIds(
  params: Omit<PaymentQueryParams, "page" | "size">,
): Promise<string[]> {
  const first = await fetchPayments({ ...params, page: 0, size: 25 })
  const totalPages = Math.max(0, first.totalPages)
  const rest =
    totalPages <= 1
      ? []
      : await Promise.all(
          Array.from({ length: totalPages - 1 }, (_, i) =>
            fetchPayments({ ...params, page: i + 1, size: 25 }),
          ),
        )
  return [
    ...first.content.map((p) => p.publicId),
    ...rest.flatMap((page) => page.content.map((p) => p.publicId)),
  ]
}
