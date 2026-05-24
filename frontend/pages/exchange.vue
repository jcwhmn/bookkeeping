<!-- pages/exchange.vue — Exchange Rates Management -->
<template>
  <div style="max-width: 1000px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Exchange Rates</h1>
      <v-spacer />
      <v-btn variant="outlined" prepend-icon="mdi-refresh" @click="refreshRates" :loading="refreshing">
        Refresh Rates
      </v-btn>
    </div>

    <!-- Base Currency Selector -->
    <v-card class="mb-4 rounded-lg" elevation="1">
      <v-card-text>
        <div class="d-flex align-center">
          <span class="text-body-2 text-grey mr-4">Base Currency:</span>
          <v-select
            v-model="baseCurrency"
            :items="currencyOptions"
            variant="outlined"
            density="compact"
            style="max-width: 200px"
            @update:model-value="convertAll"
          />
          <v-text-field
            v-model="baseAmount"
            label="Amount"
            type="number"
            variant="outlined"
            density="compact"
            class="ml-4"
            style="max-width: 150px"
            @update:model-value="convertAll"
          />
          <span class="ml-2 text-grey">= {{ formatAmount(convertedAmount) }} {{ baseCurrency }}</span>
        </div>
      </v-card-text>
    </v-card>

    <!-- Exchange Rates Table -->
    <v-card class="rounded-lg" elevation="1">
      <v-data-table
        :headers="headers"
        :items="rates"
        :items-per-page="20"
        class="elevation-0"
      >
        <template v-slot:item.currency="{ item }">
          <div class="d-flex align-center py-2">
            <v-avatar size="32" color="primary" class="mr-3">
              <span class="text-white text-caption font-weight-bold">{{ item.currency.slice(0,2) }}</span>
            </v-avatar>
            <div>
              <div class="font-weight-medium">{{ item.currency }}</div>
              <div class="text-caption text-grey">{{ item.name }}</div>
            </div>
          </div>
        </template>

        <template v-slot:item.rate="{ item }">
          <span class="text-body-1 font-weight-medium">{{ formatRate(item.rate) }}</span>
        </template>

        <template v-slot:item.converted="{ item }">
          <span class="text-body-2 text-grey" v-if="baseCurrency !== item.currency">
            ≈ {{ formatAmount(convert(item.rate)) }} {{ item.currency }}
          </span>
          <span v-else class="text-grey">—</span>
        </template>

        <template v-slot:item.isCustom="{ item }">
          <v-chip v-if="item.isCustom" size="small" color="warning" variant="tonal">Custom</v-chip>
          <v-chip v-else size="small" variant="outlined" color="grey">Default</v-chip>
        </template>

        <template v-slot:item.actions="{ item }">
          <v-btn icon="mdi-pencil" variant="text" size="small" color="primary" @click="editRate(item)" />
          <v-btn v-if="item.isCustom" icon="mdi-delete" variant="text" size="small" color="error" @click="deleteRate(item)" />
        </template>
      </v-data-table>
    </v-card>

    <!-- Quick Reference -->
    <v-card class="mt-4 rounded-lg" elevation="1">
      <v-card-title class="text-subtitle-1">Quick Reference</v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="6" sm="4" v-for="rate in commonRates" :key="rate.currency">
            <div class="d-flex align-center justify-space-between pa-2 rounded-lg" style="background: #f5f5f5">
              <span class="font-weight-medium">{{ rate.currency }}</span>
              <span class="text-grey">{{ formatRate(rate.rate) }}</span>
            </div>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Edit Custom Rate Dialog -->
    <v-dialog v-model="editDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          {{ editingRate?.isCustom ? 'Edit Custom Rate' : 'Add Custom Rate' }}
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="editDialog = false" />
        </v-card-title>
        <v-card-text>
          <v-select v-model="form.currency" :items="availableCurrencies" label="Currency" variant="outlined" density="comfortable" class="mb-3" />
          <v-text-field v-model="form.rate" label="Exchange Rate" type="number" variant="outlined" density="comfortable" hint="1 USD = ?" persistent-hint />
          <div class="text-caption text-grey mt-2">
            1 {{ baseCurrency }} = {{ form.rate || '?' }} {{ form.currency || 'USD' }}
          </div>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="editDialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="lg" @click="saveRate" :loading="saving">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

interface Rate { currency: string; name: string; rate: number; isCustom: boolean }

const rates = ref<Rate[]>([])
const baseCurrency = ref('USD')
const baseAmount = ref(100)
const refreshing = ref(false)
const editDialog = ref(false)
const saving = ref(false)
const editingRate = ref<Rate | null>(null)

const form = reactive({ currency: '', rate: '' as number | string })

const headers = [
  { title: 'Currency', key: 'currency', width: '200px' },
  { title: 'Rate', key: 'rate', width: '120px' },
  { title: 'Converted', key: 'converted', width: '150px' },
  { title: 'Type', key: 'isCustom', width: '100px' },
  { title: '', key: 'actions', width: '100px', align: 'end' as const },
]

const currencyOptions = [
  { title: 'USD - US Dollar', value: 'USD' },
  { title: 'EUR - Euro', value: 'EUR' },
  { title: 'GBP - British Pound', value: 'GBP' },
  { title: 'CNY - Chinese Yuan', value: 'CNY' },
  { title: 'JPY - Japanese Yen', value: 'JPY' },
  { title: 'KRW - Korean Won', value: 'KRW' },
  { title: 'HKD - Hong Kong Dollar', value: 'HKD' },
  { title: 'SGD - Singapore Dollar', value: 'SGD' },
  { title: 'AUD - Australian Dollar', value: 'AUD' },
  { title: 'CAD - Canadian Dollar', value: 'CAD' },
  { title: 'CHF - Swiss Franc', value: 'CHF' },
  { title: 'TWD - Taiwan Dollar', value: 'TWD' },
  { title: 'THB - Thai Baht', value: 'THB' },
  { title: 'INR - Indian Rupee', value: 'INR' },
  { title: 'PHP - Philippine Peso', value: 'PHP' },
]

const availableCurrencies = computed(() =>
  currencyOptions.filter(c => !rates.value.some(r => r.currency === c.value))
)

const commonRates = computed(() => rates.value.slice(0, 12))

const convertedAmount = computed(() => {
  const baseRate = rates.value.find(r => r.currency === baseCurrency.value)?.rate || 1
  return baseAmount.value / baseRate
})

function formatRate(rate: number) {
  if (rate >= 100) return rate.toFixed(2)
  if (rate >= 1) return rate.toFixed(4)
  return rate.toFixed(6)
}

function formatAmount(amount: number) {
  return amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function convert(rate: number) {
  return convertedAmount.value * rate
}

function convertAll() {
  // Trigger recompute
}

function editRate(rate: Rate) {
  editingRate.value = rate
  form.currency = rate.currency
  form.rate = rate.rate
  editDialog.value = true
}

function deleteRate(rate: Rate) {
  api.delete(`/exchange_rates/${rate.currency}`).then(() => fetchRates())
}

async function saveRate() {
  saving.value = true
  try {
    if (editingRate.value?.isCustom) {
      await api.put(`/exchange_rates/${form.currency}`, { rate: parseFloat(form.rate as string) })
    } else {
      await api.post('/exchange_rates', { currency: form.currency, rate: parseFloat(form.rate as string) })
    }
    editDialog.value = false
    await fetchRates()
  } catch (e) { console.error('Save rate failed:', e) }
  finally { saving.value = false }
}

async function refreshRates() {
  refreshing.value = true
  try {
    await api.post('/exchange_rates/refresh.json')
    await fetchRates()
  } catch (e) { console.error('Refresh failed:', e) }
  finally { refreshing.value = false }
}

async function fetchRates() {
  try {
    rates.value = await api.get<Rate[]>('/exchange_rates')
  } catch {
    // Default rates
    rates.value = [
      { currency: 'USD', name: 'US Dollar', rate: 1, isCustom: false },
      { currency: 'EUR', name: 'Euro', rate: 0.92, isCustom: false },
      { currency: 'GBP', name: 'British Pound', rate: 0.79, isCustom: false },
      { currency: 'CNY', name: 'Chinese Yuan', rate: 7.24, isCustom: false },
      { currency: 'JPY', name: 'Japanese Yen', rate: 149.5, isCustom: false },
      { currency: 'KRW', name: 'Korean Won', rate: 1320, isCustom: false },
      { currency: 'HKD', name: 'Hong Kong Dollar', rate: 7.82, isCustom: false },
      { currency: 'SGD', name: 'Singapore Dollar', rate: 1.35, isCustom: false },
      { currency: 'AUD', name: 'Australian Dollar', rate: 1.53, isCustom: false },
      { currency: 'CAD', name: 'Canadian Dollar', rate: 1.36, isCustom: false },
      { currency: 'CHF', name: 'Swiss Franc', rate: 0.88, isCustom: false },
      { currency: 'TWD', name: 'Taiwan Dollar', rate: 31.5, isCustom: false },
    ]
  }
}

onMounted(fetchRates)
</script>