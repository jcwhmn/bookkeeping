<!-- pages/app-settings.vue — Application Settings Page -->
<template>
  <div style="max-width: 900px; margin: 0 auto">
    <!-- Version Info Bar -->
    <v-alert v-if="appInfo" type="info" variant="tonal" class="mb-4 rounded-lg" density="compact">
      <div class="d-flex align-center justify-space-between">
        <div class="d-flex align-center">
          <v-icon start size="18">mdi-information</v-icon>
          <span class="text-body-2">{{ appInfo.name }}</span>
        </div>
        <div class="d-flex align-center">
          <v-chip size="small" variant="outlined" class="mr-2">
            <v-icon start size="14">mdi-tag</v-icon>
            v{{ appInfo.version }}
          </v-chip>
          <span class="text-caption text-grey">Built: {{ appInfo.buildTime }}</span>
        </div>
      </div>
    </v-alert>

    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Application Settings</h1>
      <v-spacer />
      <v-btn variant="outlined" prepend-icon="mdi-content-save" @click="saveSettings" :loading="saving">
        Save Changes
      </v-btn>
    </div>

    <!-- Settings Tabs -->
    <v-tabs v-model="activeTab" color="primary" class="mb-4" density="compact">
      <v-tab value="basic">
        <v-icon start size="18">mdi-tune</v-icon> Basic
      </v-tab>
      <v-tab value="lock">
        <v-icon start size="18">mdi-lock-outline</v-icon> App Lock
      </v-tab>
      <v-tab value="statistics">
        <v-icon start size="18">mdi-chart-bar</v-icon> Statistics
      </v-tab>
      <v-tab value="cache">
        <v-icon start size="18">mdi-database</v-icon> Cache
      </v-tab>
    </v-tabs>

    <!-- ============ BASIC SETTINGS ============ -->
    <v-card v-if="activeTab === 'basic'" class="rounded-lg" elevation="1">
      <v-card-text class="pa-6">
        <div class="text-subtitle-1 font-weight-bold mb-4">Display Options</div>
        
        <v-switch v-model="settings.showAccountBalances" label="Show account balances in list" color="primary" class="mb-2" />
        <v-switch v-model="settings.autoUpdateRates" label="Auto-update exchange rates" color="primary" class="mb-2" />
        <v-switch v-model="settings.showAddButton" label="Show quick add button in nav" color="primary" class="mb-2" />

        <v-divider class="my-4" />

        <div class="text-subtitle-1 font-weight-bold mb-4">Currency Display</div>
        <v-row>
          <v-col cols="12" sm="6">
            <v-select v-model="settings.currencyPosition" :items="currencyPositionOptions" label="Currency Symbol Position" variant="outlined" density="comfortable" class="mb-3" />
          </v-col>
          <v-col cols="12" sm="6">
            <v-select v-model="settings.decimalSeparator" :items="decimalOptions" label="Decimal Separator" variant="outlined" density="comfortable" />
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- ============ APP LOCK SETTINGS ============ -->
    <v-card v-if="activeTab === 'lock'" class="rounded-lg" elevation="1">
      <v-card-text class="pa-6">
        <div class="text-subtitle-1 font-weight-bold mb-4">Application Lock</div>
        
        <v-switch v-model="settings.pinLockEnabled" label="Enable PIN Lock" color="primary" class="mb-4" />

        <v-text-field 
          v-if="settings.pinLockEnabled" 
          v-model="settings.pinCode" 
          label="PIN Code (6 digits)" 
          type="password" 
          variant="outlined" 
          density="comfortable" 
          maxlength="6"
          class="mb-3"
          hint="Enter a 6-digit PIN to lock the app"
          persistent-hint
        />

        <v-divider class="my-4" />

        <div class="text-subtitle-1 font-weight-bold mb-4">Biometric Unlock</div>
        <v-switch v-model="settings.biometricEnabled" label="Enable fingerprint/face unlock" color="primary" class="mb-2" />
        <div class="text-body-2 text-grey">
          Use your device's biometric authentication (fingerprint or face) to unlock the app.
        </div>

        <v-divider class="my-4" />

        <v-alert type="info" variant="tonal" class="rounded-lg">
          When enabled, you'll need to enter your PIN or use biometrics to access the app after it closes.
        </v-alert>
      </v-card-text>
    </v-card>

    <!-- ============ STATISTICS SETTINGS ============ -->
    <v-card v-if="activeTab === 'statistics'" class="rounded-lg" elevation="1">
      <v-card-text class="pa-6">
        <div class="text-subtitle-1 font-weight-bold mb-4">Default Chart Settings</div>
        
        <v-row class="mb-4">
          <v-col cols="12" sm="6">
            <v-select v-model="settings.defaultChartType" :items="chartTypeOptions" label="Default Chart Type" variant="outlined" density="comfortable" />
          </v-col>
          <v-col cols="12" sm="6">
            <v-select v-model="settings.defaultDataRange" :items="rangeOptions" label="Default Data Range" variant="outlined" density="comfortable" />
          </v-col>
        </v-row>

        <v-divider class="my-4" />

        <div class="text-subtitle-1 font-weight-bold mb-4">Default Filters</div>
        <v-select v-model="settings.defaultAccountFilter" :items="accountOptions" label="Default Account Filter" variant="outlined" density="comfortable" class="mb-3" clearable />
        <v-select v-model="settings.defaultCategoryFilter" :items="categoryOptions" label="Default Category Filter" variant="outlined" density="comfortable" class="mb-3" clearable />
      </v-card-text>
    </v-card>

    <!-- ============ CACHE SETTINGS ============ -->
    <v-card v-if="activeTab === 'cache'" class="rounded-lg" elevation="1">
      <v-card-text class="pa-6">
        <div class="text-subtitle-1 font-weight-bold mb-4">Browser Cache Management</div>
        
        <v-alert type="info" variant="tonal" class="mb-4 rounded-lg">
          Clear cached data to free up space or resolve display issues.
        </v-alert>

        <v-row>
          <v-col cols="12" sm="4">
            <v-card class="pa-4 rounded-lg text-center" elevation="0" color="grey-lighten-4">
              <div class="text-h6 font-weight-bold">{{ cacheStats.mapTiles }} MB</div>
              <div class="text-caption text-grey">Map Tiles Cache</div>
              <v-btn size="small" variant="outlined" class="mt-2" @click="clearCache('mapTiles')">Clear</v-btn>
            </v-card>
          </v-col>
          <v-col cols="12" sm="4">
            <v-card class="pa-4 rounded-lg text-center" elevation="0" color="grey-lighten-4">
              <div class="text-h6 font-weight-bold">{{ cacheStats.exchangeRates }} MB</div>
              <div class="text-caption text-grey">Exchange Rates Cache</div>
              <v-btn size="small" variant="outlined" class="mt-2" @click="clearCache('exchangeRates')">Clear</v-btn>
            </v-card>
          </v-col>
          <v-col cols="12" sm="4">
            <v-card class="pa-4 rounded-lg text-center" elevation="0" color="grey-lighten-4">
              <div class="text-h6 font-weight-bold">{{ cacheStats.charts }} MB</div>
              <div class="text-caption text-grey">Charts Cache</div>
              <v-btn size="small" variant="outlined" class="mt-2" @click="clearCache('charts')">Clear</v-btn>
            </v-card>
          </v-col>
        </v-row>

        <v-divider class="my-4" />

        <div class="d-flex align-center">
          <v-btn variant="outlined" color="error" @click="clearAllCache">
            <v-icon start>mdi-delete-sweep</v-icon> Clear All Cache
          </v-btn>
          <span class="text-body-2 text-grey ml-3">Total: {{ totalCache }} MB</span>
        </div>
      </v-card-text>
    </v-card>

    <!-- Success Snackbar -->
    <v-snackbar v-model="snackbar" color="success" timeout="3000">
      Settings saved successfully
    </v-snackbar>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

interface Account { id: number; name: string }
interface Category { id: number; name: string }
interface AppInfo { name: string; version: string; buildTime: string }

const activeTab = ref('basic')
const saving = ref(false)
const snackbar = ref(false)
const accounts = ref<Account[]>([])
const categories = ref<Category[]>([])
const appInfo = ref<AppInfo | null>(null)

const settings = reactive({
  showAccountBalances: true,
  autoUpdateRates: false,
  showAddButton: true,
  currencyPosition: 'before',
  decimalSeparator: '.',
  pinLockEnabled: false,
  pinCode: '',
  biometricEnabled: false,
  defaultChartType: 'bar',
  defaultDataRange: 'this_month',
  defaultAccountFilter: null as number | null,
  defaultCategoryFilter: null as number | null,
})

const cacheStats = reactive({ mapTiles: 12, exchangeRates: 2, charts: 8 })
const totalCache = computed(() => cacheStats.mapTiles + cacheStats.exchangeRates + cacheStats.charts)

const currencyPositionOptions = [
  { title: 'Before amount ($100)', value: 'before' },
  { title: 'After amount (100$)', value: 'after' },
]

const decimalOptions = [
  { title: 'Period (1,234.56)', value: '.' },
  { title: 'Comma (1.234,56)', value: ',' },
]

const chartTypeOptions = [
  { title: 'Bar Chart', value: 'bar' },
  { title: 'Line Chart', value: 'line' },
  { title: 'Pie Chart', value: 'pie' },
  { title: 'Area Chart', value: 'area' },
]

const rangeOptions = [
  { title: 'This Month', value: 'this_month' },
  { title: 'Last Month', value: 'last_month' },
  { title: 'This Year', value: 'this_year' },
  { title: 'Last 3 Months', value: 'last_3_months' },
  { title: 'Last 6 Months', value: 'last_6_months' },
  { title: 'Last 12 Months', value: 'last_12_months' },
]

const accountOptions = computed(() => [{ title: 'All Accounts', value: null }, ...accounts.value.map(a => ({ title: a.name, value: a.id }))])
const categoryOptions = computed(() => [{ title: 'All Categories', value: null }, ...categories.value.map(c => ({ title: c.name, value: c.id }))])

async function saveSettings() {
  saving.value = true
  try {
    // Would call API to save settings
    await new Promise(r => setTimeout(r, 500)) // Simulate API call
    snackbar.value = true
  } catch (e) { console.error('Save failed:', e) }
  finally { saving.value = false }
}

function clearCache(type: string) {
  if (type === 'mapTiles') cacheStats.mapTiles = 0
  else if (type === 'exchangeRates') cacheStats.exchangeRates = 0
  else if (type === 'charts') cacheStats.charts = 0
}

function clearAllCache() {
  cacheStats.mapTiles = 0
  cacheStats.exchangeRates = 0
  cacheStats.charts = 0
}

onMounted(async () => {
  // Fetch app info and user data in parallel
  const [info, acc, cats] = await Promise.all([
    api.get<AppInfo>('/info').catch(() => null),
    api.get<Account[]>('/accounts'),
    api.get<Category[]>('/categories'),
  ])
  if (info) appInfo.value = info
  accounts.value = acc
  categories.value = cats
})
</script>