export interface User {
  id: string
  username: string
  email: string
  firstName: string | null
  lastName: string | null
  enabled: boolean
  roles: string[]
  dateCreated: string
  dateModified: string
}

export interface AuthResponse {
  token: string
  user: User
}

export interface LoginRequest {
  username: string
  password: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}
