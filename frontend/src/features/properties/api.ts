import { apiGet, apiPost, apiPut } from "@/lib/api-client"
import type { PagedResponse, Property } from "@/types/api"
import type { PropertyFormValues } from "./schema"

export interface PropertyQueryParams {
  page: number
  size: number
  search?: string
}

export async function fetchProperties(params: PropertyQueryParams): Promise<PagedResponse<Property>> {
  return apiGet<PagedResponse<Property>>("/properties", {
    params: {
      page: params.page,
      size: params.size,
      search: params.search?.trim() || undefined,
    },
  })
}

export async function fetchAllProperties(): Promise<Property[]> {
  return apiGet<Property[]>("/properties/list")
}

export async function createProperty(values: PropertyFormValues): Promise<Property> {
  return apiPost<Property>("/properties", values)
}

export async function updateProperty(
  id: string,
  values: PropertyFormValues,
): Promise<Property> {
  return apiPut<Property>(`/properties/${id}`, values)
}