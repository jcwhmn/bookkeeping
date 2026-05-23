// stores/auth.ts
import { defineStore } from 'pinia'

interface User {
  id: number
  username: string
  email: string
  nickname: string
  defaultCurrency: string
  language: string
}

interface LoginResponse {
  token: string
  user: User
}

export const useAuthStore = defineStore('auth', () => {
  const token = useCookie<string>('token', { maxAge: 86400 })
  const user = ref<User | null>(null)
  const isAuthenticated = computed(() => !!token.value)

  async function login(username: string, password: string) {
    const api = useApi()
    const response = await api.post<LoginResponse>('/auth/login', { username, password })
    token.value = response.token
    user.value = response.user
    return response
  }

  async function register(username: string, email: string, password: string) {
    const api = useApi()
    const response = await api.post<User>('/auth/register', { username, email, password })
    return response
  }

  async function fetchCurrentUser() {
    if (!token.value) return
    try {
      const api = useApi()
      user.value = await api.get<User>('/auth/me')
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = null
    user.value = null
    navigateTo('/login')
  }

  return { token, user, isAuthenticated, login, register, fetchCurrentUser, logout }
})
