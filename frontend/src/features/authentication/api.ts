import { apiPost, apiGet } from "@/lib/api-client"
import type { AuthResponse, LoginRequest, ChangePasswordRequest } from "./types"
import type { User } from "./types"

export async function login(data: LoginRequest): Promise<AuthResponse> {
  return apiPost<AuthResponse>("/auth/login", data)
}

export async function refreshToken(): Promise<AuthResponse> {
  return apiPost<AuthResponse>("/auth/refresh")
}

export async function getCurrentUser(): Promise<User> {
  return apiGet<User>("/auth/me")
}

export async function changePassword(data: ChangePasswordRequest): Promise<void> {
  return apiPost<void>("/auth/change-password", data)
}

export async function logout(): Promise<void> {
  return apiPost<void>("/auth/logout")
}
