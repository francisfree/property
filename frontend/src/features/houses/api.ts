import { apiGet, apiPost, apiPut } from "@/lib/api-client"
import type { House, PagedResponse } from "@/types/api"
import type { HouseFormValues } from "./schema"

export interface HouseQueryParams {
  page: number
  size: number
  search?: string
  propertyId?: string
}

export type HouseActionType = "Occupied" | "Vacant" | "ChangeAmount"

export async function fetchHouses(params: HouseQueryParams): Promise<PagedResponse<House>> {
  return apiGet<PagedResponse<House>>("/houses", {
    params: {
      page: params.page,
      size: params.size,
      search: params.search?.trim() || undefined,
      propertyId: params.propertyId || undefined,
    },
  })
}

export async function fetchAllHouses(propertyId?: string): Promise<House[]> {
  return apiGet<House[]>("/houses/list", {
    params: { propertyId: propertyId || undefined },
  })
}

export async function createHouse(values: HouseFormValues): Promise<House> {
  return apiPost<House>("/houses", values)
}

export async function updateHouse(id: string, values: HouseFormValues): Promise<House> {
  return apiPut<House>(`/houses/${id}`, values)
}

export async function runHouseAction(
  id: string,
  actionType: HouseActionType,
  amount?: number | null,
): Promise<House> {
  return apiPut<House>(`/houses/${id}/actions`, {
    actionType,
    amount: amount ?? null,
  })
}