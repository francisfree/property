import { apiGet, apiPost, apiPut } from "@/lib/api-client"
import type { PagedResponse, Person } from "@/types/api"
import type { PersonFormValues } from "./schema"

export interface PersonQueryParams {
  page: number
  size: number
  search?: string
}

export async function fetchPersons(params: PersonQueryParams): Promise<PagedResponse<Person>> {
  return apiGet<PagedResponse<Person>>("/persons", {
    params: {
      page: params.page,
      size: params.size,
      search: params.search?.trim() || undefined,
    },
  })
}

export async function fetchAllPersons(search?: string): Promise<Person[]> {
  return apiGet<Person[]>("/persons/list", {
    params: { search: search?.trim() || undefined },
  })
}

export async function createPerson(values: PersonFormValues): Promise<Person> {
  return apiPost<Person>("/persons", values)
}

export async function updatePerson(id: string, values: PersonFormValues): Promise<Person> {
  return apiPut<Person>(`/persons/${id}`, values)
}