// composables/useApi.ts
// Centralized API client for the bookkeeping backend (client-side only)

interface ApiResponse<T> {
  success: boolean
  result: T
  errorCode?: number
  errorMessage?: string
}

const API_BASE = 'http://localhost:8080/api/v1'

function getAuthHeaders(): Record<string, string> {
  if (typeof window === 'undefined') return {}
  const token = useCookie<string>('token').value
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...getAuthHeaders(),
  }

  const options: RequestInit = { method, headers }
  if (body) options.body = JSON.stringify(body)

  const res = await fetch(`${API_BASE}${path}`, options)
  const data = await res.json() as ApiResponse<T>

  if (!data.success) {
    throw createError({
      statusCode: data.errorCode || res.status,
      statusMessage: data.errorMessage || 'Unknown error',
    })
  }

  return data.result
}

export const useApi = () => ({
  get: <T>(path: string) => request<T>('GET', path),
  post: <T>(path: string, body?: unknown) => request<T>('POST', path, body),
  put: <T>(path: string, body?: unknown) => request<T>('PUT', path, body),
  delete: <T>(path: string) => request<T>('DELETE', path),
})
