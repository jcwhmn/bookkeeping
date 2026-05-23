<!-- pages/accounts.vue — Designed Accounts page -->
<template>
  <div style="max-width: 1200px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Accounts</h1>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" rounded="lg" @click="openCreate()">Add Account</v-btn>
    </div>

    <!-- Filter Tabs -->
    <v-tabs v-model="activeTab" color="primary" class="mb-4" density="compact">
      <v-tab value="all">All</v-tab>
      <v-tab value="CASH">Cash</v-tab>
      <v-tab value="CHECKING">Bank</v-tab>
      <v-tab value="CREDIT">Credit</v-tab>
      <v-tab value="INVESTMENT">Investment</v-tab>
    </v-tabs>

    <!-- Account Cards -->
    <v-row v-if="!loading">
      <v-col cols="12" sm="6" lg="4" v-for="account in filteredAccounts" :key="account.id">
        <v-card class="rounded-lg" elevation="2" :class="`hover-card account-card account-${account.accountType.toLowerCase()}`">
          <div class="pa-4">
            <!-- Top row: icon + name + balance -->
            <div class="d-flex align-start mb-3">
              <v-avatar :color="typeColor(account.accountType)" size="44" class="mr-3">
                <v-icon color="white" size="22">{{ typeIcon(account.accountType) }}</v-icon>
              </v-avatar>
              <div class="flex-grow-1">
                <div class="text-body-1 font-weight-bold">{{ account.name }}</div>
                <div class="text-caption text-grey-darken-1">{{ account.accountType }} · {{ account.currency }}</div>
              </div>
              <div class="text-h6 font-weight-bold" :class="account.balance >= 0 ? 'text-success' : 'text-error'">
                ${{ fmt(account.balance) }}
              </div>
            </div>

            <!-- Balance bar -->
            <v-progress-linear
              :model-value="balancePercent(account)"
              :color="account.balance >= 0 ? 'success' : 'error'"
              height="4" rounded class="mb-3"
            />

            <!-- Bottom actions -->
            <div class="d-flex justify-end">
              <v-btn icon="mdi-pencil-outline" variant="text" size="small" density="compact" @click="openEdit(account)" />
              <v-btn icon="mdi-archive-outline" variant="text" size="small" density="compact" color="error" @click="confirmDelete(account)" />
            </div>
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" v-if="filteredAccounts.length === 0">
        <v-alert type="info" variant="tonal" class="rounded-lg">
          No accounts in this category. <v-btn variant="text" size="small" @click="openCreate()">Create one</v-btn>
        </v-alert>
      </v-col>
    </v-row>

    <v-progress-linear v-else indeterminate color="primary" class="mb-4" />

    <!-- Total Bar -->
    <v-card v-if="!loading && accounts.length" class="mt-4 pa-4 rounded-lg" elevation="2" color="grey-lighten-3">
      <div class="d-flex align-center justify-space-between">
        <span class="text-subtitle-1 font-weight-bold">Total Balance</span>
        <span class="text-h5 font-weight-bold text-primary">${{ fmt(totalBalance) }}</span>
      </div>
    </v-card>

    <!-- Create/Edit Dialog -->
    <v-dialog v-model="dialog" max-width="480">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          {{ editing ? 'Edit Account' : 'New Account' }}
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="dialog = false" />
        </v-card-title>
        <v-card-text>
          <v-text-field v-model="form.name" label="Name" variant="outlined" density="comfortable" :rules="[required]" class="mb-3" />
          <div class="mb-3">
            <div class="text-caption text-grey mb-2">Type</div>
            <v-btn-toggle v-model="form.accountType" mandatory divided color="primary" density="compact">
              <v-btn v-for="t in types" :key="t" :value="t" size="small" class="text-none px-3">{{ t }}</v-btn>
            </v-btn-toggle>
          </div>
          <v-text-field v-model="form.currency" label="Currency" variant="outlined" density="comfortable" maxlength="3" :rules="[required]" class="mb-3" />
          <v-text-field v-if="!editing" v-model="form.initialBalance" label="Initial Balance ($)" type="number" variant="outlined" density="comfortable" class="mb-3" />
          <v-text-field v-model="form.description" label="Description" variant="outlined" density="comfortable" />
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="lg" @click="save" :loading="saving">{{ editing ? 'Update' : 'Create' }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirm -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title>Archive Account?</v-card-title>
        <v-card-text>Move "{{ deletingAccount?.name }}" to archived?</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">Cancel</v-btn>
          <v-btn color="error" rounded="lg" @click="remove" :loading="deleting">Archive</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

interface Account { id: number; name: string; accountType: string; currency: string; balance: number; description?: string }

const accounts = shallowRef<Account[]>([])
const loading = ref(true)
const activeTab = ref('all')
const dialog = ref(false)
const deleteDialog = ref(false)
const saving = ref(false)
const deleting = ref(false)
const editing = ref(false)
const deletingAccount = ref<Account | null>(null)
const required = (v: string) => !!v || 'Required'
const types = ['CASH', 'CHECKING', 'SAVINGS', 'CREDIT', 'INVESTMENT']

const form = reactive({ name: '', accountType: 'CASH', currency: 'USD', initialBalance: 0, description: '' })

const filteredAccounts = computed(() =>
  activeTab.value === 'all' ? accounts.value : accounts.value.filter(a => a.accountType === activeTab.value)
)
const totalBalance = computed(() => accounts.value.reduce((s, a) => s + a.balance, 0))

function typeIcon(t: string) {
  const icons: Record<string, string> = { CASH: 'mdi-cash', CHECKING: 'mdi-bank', SAVINGS: 'mdi-piggy-bank', CREDIT: 'mdi-credit-card', INVESTMENT: 'mdi-chart-line' }
  return icons[t] || 'mdi-wallet'
}
function typeColor(t: string) {
  const colors: Record<string, string> = { CASH: 'success', CHECKING: 'primary', SAVINGS: 'info', CREDIT: 'error', INVESTMENT: 'warning' }
  return colors[t] || 'grey'
}
function fmt(c: number) { return (c / 100).toLocaleString('en-US', { minimumFractionDigits: 2 }) }
function balancePercent(a: Account) {
  const max = Math.max(...accounts.value.map(x => Math.abs(x.balance)), 1)
  return (Math.abs(a.balance) / max) * 100
}

function openCreate() { editing.value = false; resetForm(); dialog.value = true }
function openEdit(a: Account) {
  editing.value = true
  form.name = a.name; form.accountType = a.accountType; form.currency = a.currency; form.description = a.description || ''
  dialog.value = true
}
function confirmDelete(a: Account) { deletingAccount.value = a; deleteDialog.value = true }
function resetForm() { form.name = ''; form.accountType = 'CASH'; form.currency = 'USD'; form.initialBalance = 0; form.description = '' }

async function fetchAccounts() {
  loading.value = true
  try { accounts.value = await api.get<Account[]>('/accounts') }
  finally { loading.value = false }
}

async function save() {
  saving.value = true
  try {
    if (editing.value) {
      await api.put(`/accounts/${(form as any).id}`, { name: form.name, description: form.description || null })
    } else {
      await api.post('/accounts', { name: form.name, accountType: form.accountType, currency: form.currency, initialBalance: Math.round(form.initialBalance * 100), description: form.description || null })
    }
    dialog.value = false
    await fetchAccounts()
  } finally { saving.value = false }
}

async function remove() {
  if (!deletingAccount.value) return
  deleting.value = true
  try {
    await api.delete(`/accounts/${deletingAccount.value.id}`)
    deleteDialog.value = false
    deletingAccount.value = null
    await fetchAccounts()
  } finally { deleting.value = false }
}

onMounted(fetchAccounts)
</script>

<style scoped>
.hover-card { transition: transform 0.15s, box-shadow 0.15s; }
.hover-card:hover { transform: translateY(-2px); box-shadow: 0 4px 16px rgba(0,0,0,0.12) !important; }
</style>
