import { apiGet, apiPost, apiPut } from "@/lib/api-client"
import type { PagedResponse, Rental } from "@/types/api"
import type { RentalCreateValues } from "./schema"

export interface RentalQueryParams {
  page: number
  size: number
  propertyId?: string
  houseId?: string
  identificationNumber?: string
  phoneNumber?: string
  accountStatus?: string
  arrearStatus?: string
  search?: string
}

export interface RentalCreateRequest {
  firstName: string
  lastName: string
  otherName?: string
  identificationType: string
  identificationNumber: string
  nationality?: string
  phoneNumber: string
  housePublicId: string
  amount: number
}

export async function fetchRentals(params: RentalQueryParams): Promise<PagedResponse<Rental>> {
  return apiGet<PagedResponse<Rental>>("/rentals", {
    params: {
      page: params.page,
      size: params.size,
      propertyId: params.propertyId || undefined,
      houseId: params.houseId || undefined,
      identificationNumber: params.identificationNumber || undefined,
      phoneNumber: params.phoneNumber || undefined,
      accountStatus: params.accountStatus || undefined,
      arrearStatus: params.arrearStatus || undefined,
      search: params.search || undefined,
    },
  })
}

export async function createRental(values: RentalCreateValues): Promise<Rental> {
  const request: RentalCreateRequest = {
    firstName: values.firstName,
    lastName: values.lastName,
    otherName: values.otherName || undefined,
    identificationType: values.identificationType,
    identificationNumber: values.identificationNumber,
    nationality: values.nationality || undefined,
    phoneNumber: values.phoneNumber,
    housePublicId: values.housePublicId,
    amount: Number(values.amount),
  }
  return apiPost<Rental>("/rentals", request)
}

export async function changeRentalAmount(id: string, amount: number): Promise<Rental> {
  return apiPut<Rental>(`/rentals/${id}/change-amount`, { amount })
}

export async function closeRentalAccount(id: string): Promise<Rental> {
  return apiPut<Rental>(`/rentals/${id}/close-account`)
}