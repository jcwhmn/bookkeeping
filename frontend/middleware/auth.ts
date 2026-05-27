// middleware/auth.ts
interface OnboardingStatus {
  completed: boolean
  hasAccounts: boolean
  hasCategories: boolean
}

export default defineNuxtRouteMiddleware(async () => {
  const token = useCookie<string>('token').value
  if (!token) {
    return navigateTo('/login')
  }

  // Check if onboarding is complete
  const onboardingPages = ['/onboarding', '/login', '/register']
  const currentPath = useRoute().path
  
  if (onboardingPages.includes(currentPath)) {
    return // Skip check on these pages
  }

  try {
    const api = useApi()
    const status = await api.get<OnboardingStatus>('/onboarding/status.json')
    
    // Redirect to onboarding if not completed
    if (!status.completed && !status.hasAccounts) {
      return navigateTo('/onboarding')
    }
  } catch {
    // API error, allow access
  }
})
