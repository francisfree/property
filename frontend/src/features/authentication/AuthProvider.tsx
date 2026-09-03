import {
  createContext,
  useCallback,
  useEffect,
  useRef,
  useState,
  type ReactNode,
} from "react"
import type { User } from "./types"
import * as authApi from "./api"
import { setTokenGetter } from "@/lib/api-client"

export interface AuthContextType {
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  getAccessToken: () => string | null
  login: (username: string, password: string) => Promise<void>
  logout: () => Promise<void>
  refreshUser: () => Promise<void>
}

export const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const tokenRef = useRef<string | null>(null)

  const getAccessToken = useCallback(() => tokenRef.current, [])

  // Set the token getter for the API client
  useEffect(() => {
    setTokenGetter(() => tokenRef.current)
  }, [])

  const refreshUser = useCallback(async () => {
    try {
      const response = await authApi.refreshToken()
      tokenRef.current = response.token
      setUser(response.user)
    } catch {
      tokenRef.current = null
      setUser(null)
    }
  }, [])

  useEffect(() => {
    const initAuth = async () => {
      try {
        const response = await authApi.refreshToken()
        tokenRef.current = response.token
        setUser(response.user)
      } catch {
        tokenRef.current = null
        setUser(null)
      } finally {
        setIsLoading(false)
      }
    }

    initAuth()
  }, [])

  const login = useCallback(async (username: string, password: string) => {
    const response = await authApi.login({ username, password })
    tokenRef.current = response.token
    setUser(response.user)
  }, [])

  const logout = useCallback(async () => {
    try {
      await authApi.logout()
    } finally {
      tokenRef.current = null
      setUser(null)
    }
  }, [])

  const value: AuthContextType = {
    user,
    isAuthenticated: !!user,
    isLoading,
    getAccessToken,
    login,
    logout,
    refreshUser,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
