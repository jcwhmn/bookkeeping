<!-- pages/onboarding.vue — New User Onboarding Wizard -->
<template>
  <v-app>
    <v-main class="bg-grey-lighten-4">
      <v-container class="fill-height d-flex align-center justify-center">
        <v-card width="600" class="pa-8 rounded-lg" elevation="3">
          <!-- Logo & Title -->
          <div class="text-center mb-8">
            <v-icon size="56" color="primary" class="mb-3">mdi-calculator-variant</v-icon>
            <h1 class="text-h4 font-weight-bold text-primary">Welcome to Bookkeeping</h1>
            <p class="text-body-1 text-grey-darken-1 mt-2">Let's set up your account in a few steps</p>
          </div>

          <!-- Progress Steps -->
          <v-stepper :items="steps" alt-labels class="mb-8 elevation-0 bg-transparent">
            <template v-slot:item.1>
              <v-card-text>
                <h3 class="text-h6 mb-4">Step 1: Create Your First Account</h3>
                <p class="text-body-2 text-grey mb-4">
                  Start by adding an account where you'll track your money.
                </p>
                
                <!-- Account Templates -->
                <v-row>
                  <v-col v-for="template in accountTemplates" :key="template.name" cols="6" md="4">
                    <v-card 
                      variant="outlined" 
                      :class="{ 'border-primary': selectedAccountTemplate === template.name }"
                      class="pa-3 text-center cursor-pointer"
                      @click="selectedAccountTemplate = template.name"
                    >
                      <v-icon :color="template.color" size="32" class="mb-2">{{ template.icon }}</v-icon>
                      <div class="text-body-2 font-weight-medium">{{ template.name }}</div>
                    </v-card>
                  </v-col>
                </v-row>

                <v-text-field
                  v-model="accountName"
                  label="Account Name"
                  variant="outlined"
                  density="comfortable"
                  class="mt-4"
                  placeholder="e.g., My Bank Account"
                />
              </v-card-text>
            </template>

            <template v-slot:item.2>
              <v-card-text>
                <h3 class="text-h6 mb-4">Step 2: Add Categories</h3>
                <p class="text-body-2 text-grey mb-4">
                  Categories help you organize your income and expenses.
                </p>

                <v-alert type="info" variant="tonal" density="compact" class="mb-4">
                  We can create common categories for you automatically.
                </v-alert>

                <v-btn-toggle v-model="categoryType" color="primary" class="mb-4">
                  <v-btn value="expense">Expense</v-btn>
                  <v-btn value="income">Income</v-btn>
                  <v-btn value="all">Both</v-btn>
                </v-btn-toggle>

                <!-- Preview Categories -->
                <div v-if="categoryType === 'expense' || categoryType === 'all'">
                  <div class="text-subtitle-2 mb-2">Expense Categories (10)</div>
                  <v-chip-group class="mb-3">
                    <v-chip v-for="cat in defaultExpenseCategories" :key="cat" size="small" variant="outlined">
                      {{ cat }}
                    </v-chip>
                  </v-chip-group>
                </div>

                <div v-if="categoryType === 'income' || categoryType === 'all'">
                  <div class="text-subtitle-2 mb-2">Income Categories (6)</div>
                  <v-chip-group>
                    <v-chip v-for="cat in defaultIncomeCategories" :key="cat" size="small" variant="outlined">
                      {{ cat }}
                    </v-chip>
                  </v-chip-group>
                </div>
              </v-card-text>
            </template>

            <template v-slot:item.3>
              <v-card-text>
                <h3 class="text-h6 mb-4">Step 3: Set Preferences</h3>
                <p class="text-body-2 text-grey mb-4">
                  Configure your preferred settings.
                </p>

                <v-select
                  v-model="defaultCurrency"
                  label="Default Currency"
                  :items="currencies"
                  variant="outlined"
                  density="comfortable"
                  class="mb-3"
                />

                <v-select
                  v-model="language"
                  label="Language"
                  :items="languages"
                  item-title="name"
                  item-value="code"
                  variant="outlined"
                  density="comfortable"
                  class="mb-3"
                />

                <v-select
                  v-model="firstDayOfWeek"
                  label="First Day of Week"
                  :items="weekDays"
                  item-title="name"
                  item-value="value"
                  variant="outlined"
                  density="comfortable"
                />
              </v-card-text>
            </template>
          </v-stepper>

          <!-- Actions -->
          <div class="d-flex justify-space-between">
            <v-btn 
              v-if="currentStep > 1" 
              variant="text" 
              @click="currentStep--"
              :disabled="loading"
            >
              Back
            </v-btn>
            <v-spacer />
            
            <v-btn 
              v-if="currentStep < 3"
              color="primary"
              @click="currentStep++"
              :disabled="loading"
            >
              Continue
            </v-btn>
            
            <v-btn 
              v-if="currentStep === 3"
              color="success"
              :loading="loading"
              @click="completeOnboarding"
            >
              Complete Setup
            </v-btn>
          </div>

          <!-- Skip Option -->
          <div class="text-center mt-6">
            <v-btn variant="text" size="small" color="grey" @click="skipOnboarding">
              Skip setup, go to dashboard
            </v-btn>
          </div>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'empty' })

const router = useRouter()
const api = useApi()
const auth = useAuthStore()

// Stepper state
const currentStep = ref(1)
const loading = ref(false)
const steps = [
  { title: 'Account', value: 1 },
  { title: 'Categories', value: 2 },
  { title: 'Preferences', value: 3 },
]

// Step 1: Account
const accountTemplates = [
  { name: 'Cash', icon: 'mdi-cash', color: '#4CAF50' },
  { name: 'Bank', icon: 'mdi-bank', color: '#2196F3' },
  { name: 'Credit Card', icon: 'mdi-credit-card', color: '#F44336' },
  { name: 'Savings', icon: 'mdi-piggy-bank', color: '#00BCD4' },
  { name: 'Investment', icon: 'mdi-chart-line', color: '#9C27B0' },
  { name: 'Wallet', icon: 'mdi-wallet', color: '#607D8B' },
]
const selectedAccountTemplate = ref('Bank')
const accountName = ref('My Bank Account')

// Step 2: Categories
const categoryType = ref('all')
const defaultExpenseCategories = [
  'Food & Dining', 'Transportation', 'Shopping', 'Bills & Utilities',
  'Entertainment', 'Healthcare', 'Education', 'Travel',
  'Personal Care', 'Other Expenses'
]
const defaultIncomeCategories = [
  'Salary', 'Freelance', 'Investment Returns', 'Business Income',
  'Gifts & Donations', 'Other Income'
]

// Step 3: Preferences
const currencies = ['CNY', 'USD', 'EUR', 'GBP', 'JPY', 'HKD', 'SGD']
const languages = [
  { code: 'en-US', name: 'English' },
  { code: 'zh-CN', name: '中文' },
]
const weekDays = [
  { value: 0, name: 'Sunday' },
  { value: 1, name: 'Monday' },
]

const defaultCurrency = ref('CNY')
const language = ref('en-US')
const firstDayOfWeek = ref(0)

// Create account
async function createAccount() {
  const template = accountTemplates.find(t => t.name === selectedAccountTemplate.value)
  const accountTypes: Record<string, number> = {
    'Cash': 1, 'Bank': 2, 'Credit Card': 3, 'Savings': 8,
    'Investment': 7, 'Wallet': 4
  }
  
  try {
    await api.post('/accounts', {
      name: accountName.value || selectedAccountTemplate.value,
      accountType: accountTypes[selectedAccountTemplate.value] || 2,
      currency: defaultCurrency.value,
      icon: template?.icon,
      color: template?.color,
      includeInTotal: true,
      hidden: false,
    })
    return true
  } catch (e) {
    console.error('Failed to create account:', e)
    return false
  }
}

// Create default categories
async function createDefaultCategories() {
  try {
    await api.post('/onboarding/create_defaults.json', {
      type: categoryType.value
    })
    return true
  } catch (e) {
    console.error('Failed to create categories:', e)
    return false
  }
}

// Update user preferences
async function updatePreferences() {
  try {
    await api.put('/users/me', {
      defaultCurrency: defaultCurrency.value,
      language: language.value,
      firstDayOfWeek: firstDayOfWeek.value,
    })
    return true
  } catch (e) {
    console.error('Failed to update preferences:', e)
    return false
  }
}

// Complete onboarding
async function completeOnboarding() {
  loading.value = true
  try {
    // Step 1: Create account
    await createAccount()
    
    // Step 2: Create categories
    await createDefaultCategories()
    
    // Step 3: Update preferences
    await updatePreferences()
    
    // Mark onboarding complete
    await api.post('/onboarding/complete.json', {})
    
    // Redirect to dashboard
    await router.push('/')
  } catch (e) {
    console.error('Onboarding failed:', e)
  } finally {
    loading.value = false
  }
}

// Skip onboarding
async function skipOnboarding() {
  loading.value = true
  try {
    await api.post('/onboarding/complete.json', {})
    await router.push('/')
  } catch (e) {
    console.error('Skip failed:', e)
    await router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
</style>