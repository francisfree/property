import axios, {
  AxiosError,
  AxiosRequestConfig,
  type AxiosInstance,
} from "axios"

/**
 * Centralized API client.
 *
 * The API base resolves relative to the deployed base URL so the same build
 * works in development (Vite proxy at "/" -> Spring Boot) and in production
 * (served from the Spring Boot context-path, e.g. "/property").
 */
export const API_BASE = `${import.meta.env.BASE_URL}api/v1`

export interface ApiErrorResponse {
  status?: string
  statusCode?: number
  messages?: string[]
}

export class ApiError extends Error {
  statusCode: number
  messages: string[]

  constructor(message: string, statusCode: number, messages: string[] = []) {
    super(message)
    this.name = "ApiError"
    this.statusCode = statusCode
    this.messages = messages.length > 0 ? messages : [message]
  }
}

function buildErrorMessage(
  status: number,
  payload: ApiErrorResponse | undefined,
): string {
  if (payload?.messages && payload.messages.length > 0) {
    return payload.messages.join(". ")
  }
  if (status >= 500) {
    return "An unexpected server error occurred. Please try again."
  }
  return "The request could not be completed."
}

const client: AxiosInstance = axios.create({
  baseURL: API_BASE,
  headers: {
    "Content-Type": "application/json",
  },
})

client.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiErrorResponse>) => {
    const status = error.response?.status ?? 0
    const message = buildErrorMessage(status, error.response?.data)
    return Promise.reject(new ApiError(message, status, error.response?.data?.messages))
  },
)

export async function apiGet<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const response = await client.get<T>(url, config)
  return response.data
}

export async function apiPost<T>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
): Promise<T> {
  const response = await client.post<T>(url, data, config)
  return response.data
}

export async function apiPut<T>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
): Promise<T> {
  const response = await client.put<T>(url, data, config)
  return response.data
}

export async function apiDelete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const response = await client.delete<T>(url, config)
  return response.data
}

export default client