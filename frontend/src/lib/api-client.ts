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

// Token getter function - set by AuthProvider
let tokenGetter: (() => string | null) | null = null

export function setTokenGetter(getter: () => string | null) {
  tokenGetter = getter
}

// Request interceptor to attach access token
client.interceptors.request.use(
  (config) => {
    const token = tokenGetter?.()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// Track if we're currently refreshing to prevent loops
let isRefreshing = false
let failedQueue: Array<{
  resolve: (value: unknown) => void
  reject: (reason?: unknown) => void
}> = []

function processQueue(error: unknown) {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(undefined)
    }
  })
  failedQueue = []
}

client.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ApiErrorResponse>) => {
    const status = error.response?.status ?? 0
    const originalRequest = error.config

    // Handle 401 - try refresh token
    if (status === 401 && originalRequest && !("_retry" in originalRequest)) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then(() => client(originalRequest))
      }

      ;(originalRequest as AxiosRequestConfig & { _retry: boolean })._retry =
        true
      isRefreshing = true

      try {
        await axios.post(`${API_BASE}/auth/refresh`, null, {
          withCredentials: true,
        })
        processQueue(null)
        return client(originalRequest)
      } catch (refreshError) {
        processQueue(refreshError)
        // Refresh failed - clear auth state by redirecting to the login route.
        // The URL is built from BASE_URL so it respects the context-path basename
        // (e.g. "/property/login") rather than navigating to the app root.
        window.location.href = `${import.meta.env.BASE_URL}login`
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

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

export async function apiPatch<T>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
): Promise<T> {
  const response = await client.patch<T>(url, data, config)
  return response.data
}

export async function apiDelete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const response = await client.delete<T>(url, config)
  return response.data
}

export default client