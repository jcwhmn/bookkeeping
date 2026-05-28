<!-- pages/scheduled-transactions.vue — Scheduled/Recurring Transactions -->
<template>
  <div style="max-width: 1000px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Scheduled Transactions</h1>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" rounded="lg" @click="openCreate">
        Add Scheduled
      </v-btn>
    </div>

    <!-- Stats Cards -->
    <v-row class="mb-4" v-if="stats">
      <v-col cols="6" sm="3">
        <v-card class="pa-3 rounded-lg text-center" elevation="0" color="grey-lighten-4">
          <div class="text-h5 font-weight-bold">{{ stats.activeScheduled }}</div>
          <div class="text-caption text-grey">Active</div>
        </v-card>
      </v-col>
      <v-col cols="6" sm="3">
        <v-card class="pa-3 rounded-lg text-center" elevation="0" color="blue-lighten-5">
          <div class="text-h5 font-weight-bold text-blue">{{ stats.dailyCount + stats.weeklyCount }}</div>
          <div class="text-caption text-grey">Weekly/Daily</div>
        </v-card>
      </v-col>
      <v-col cols="6" sm="3">
        <v-card class="pa-3 rounded-lg text-center" elevation="0" color="green-lighten-5">
          <div class="text-h5 font-weight-bold text-green">{{ stats.monthlyCount }}</div>
          <div class="text-caption text-grey">Monthly</div>
        </v-card>
      </v-col>
      <v-col cols="6" sm="3">
        <v-card class="pa-3 rounded-lg text-center" elevation="0" color="orange-lighten-5">
          <div class="text-h5 font-weight-bold text-orange">{{ stats.yearlyCount }}</div>
          <div class="text-caption text-grey">Yearly</div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Loading -->
    <div v-if="loading" class="text-center pa-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <!-- Empty State -->
    <v-alert v-else-if="scheduled.length === 0" type="info" variant="tonal" class="rounded-lg">
      No scheduled transactions yet.
      <v-btn variant="text" color="primary" @click="openCreate">Create your first one</v-btn>
    </v-alert>

    <!-- List -->
    <v-card v-else class="rounded-lg" elevation="1">
      <v-list>
        <v-list-item v-for="st in scheduled" :key="st.id" class="py-3">
          <template v-slot:prepend>
            <v-avatar :color="typeColor(st.transactionType)" size="40">
              <v-icon color="white">{{ typeIcon(st.transactionType) }}</v-icon>
            </v-avatar>
          </template>

          <v-list-item-title class="font-weight-medium">
            {{ st.description || 'Untitled' }}
            <v-chip v-if="!st.active" size="x-small" color="grey" variant="tonal" class="ml-2">Paused</v-chip>
          </v-list-item-title>
          
          <v-list-item-subtitle>
            <span class="text-caption">{{ frequencyLabel(st.frequency) }}</span>
            · {{ st.accountName }}
            <span v-if="st.destinationAccountName"> → {{ st.destinationAccountName }}</span>
          </v-list-item-subtitle>

          <template v-slot:append>
            <div class="text-right mr-3">
              <div class="text-body-1 font-weight-bold" :class="amountClass(st.transactionType)">
                {{ amountPrefix(st.transactionType) }}${{ st.amountStr }}
              </div>
              <div class="text-caption text-grey">
                Next: {{ st.nextRunTimeStr }}
              </div>
            </div>
            <v-menu>
              <template v-slot:activator="{ props }">
                <v-btn v-bind="props" icon="mdi-dots-vertical" variant="text" size="small" />
              </template>
              <v-list density="compact">
                <v-list-item @click="openEdit(st)">
                  <v-list-item-title>Edit</v-list-item-title>
                </v-list-item>
                <v-list-item @click="toggleActive(st)">
                  <v-list-item-title>{{ st.active ? 'Pause' : 'Resume' }}</v-list-item-title>
                </v-list-item>
                <v-list-item @click="confirmDelete(st)" class="text-error">
                  <v-list-item-title>Delete</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
          </template>
        </v-list-item>
      </v-list>
    </v-card>

    <!-- Create/Edit Dialog -->
    <v-dialog v-model="dialog" max-width="520">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          {{ editing ? 'Edit Scheduled Transaction' : 'New Scheduled Transaction' }}
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="dialog = false" />
        </v-card-title>
        <v-card-text>
          <!-- Transaction Type -->
          <div class="mb-4">
            <div class="text-caption text-grey mb-2">Type</div>
            <v-btn-toggle v-model="form.transactionType" mandatory divided color="primary" density="compact" class="w-100">
              <v-btn :value="2" size="large" class="text-none flex-grow-1">
                <v-icon start>mdi-arrow-down-bold</v-icon> Income
              </v-btn>
              <v-btn :value="3" size="large" class="text-none flex-grow-1">
                <v-icon start>mdi-arrow-up-bold</v-icon> Expense
              </v-btn>
              <v-btn :value="4" size="large" class="text-none flex-grow-1">
                <v-icon start>mdi-swap-horizontal</v-icon> Transfer
              </v-btn>
            </v-btn-toggle>
          </div>

          <!-- Amount -->
          <v-text-field v-model="amountStr" label="Amount" variant="outlined" density="comfortable" prefix="$" class="mb-3 amount-input" :rules="[required]" />

          <!-- From Account -->
          <v-select v-model="form.accountId" :items="accountOptions" label="From Account" variant="outlined" density="comfortable" class="mb-3" :rules="[required]" />

          <!-- To Account (Transfer only) -->
          <v-select v-if="form.transactionType === 4" v-model="form.destinationAccountId" :items="toAccountOptions" label="To Account" variant="outlined" density="comfortable" class="mb-3" />

          <!-- Category (not Transfer) -->
          <v-select v-if="form.transactionType !== 4" v-model="form.categoryId" :items="categoryOptions" label="Category" variant="outlined" density="comfortable" class="mb-3" />

          <!-- Description -->
          <v-text-field v-model="form.description" label="Description" variant="outlined" density="comfortable" class="mb-3" :rules="[required]" />

          <v-divider class="my-4" />
          <div class="text-subtitle-2 font-weight-bold mb-3">Schedule</div>

          <!-- Frequency -->
          <v-select v-model="form.frequency" :items="frequencyOptions" label="Frequency" variant="outlined" density="comfortable" class="mb-3" :rules="[required]" />

          <!-- Interval (for daily) -->
          <v-text-field v-if="form.frequency === 'daily'" v-model.number="form.intervalDays" label="Every N days" type="number" variant="outlined" density="comfortable" class="mb-3" />

          <!-- Day of Week (for weekly) -->
          <v-select v-if="form.frequency === 'weekly'" v-model="form.dayOfWeek" :items="weekdayOptions" label="Day of Week" variant="outlined" density="comfortable" class="mb-3" />

          <!-- Day of Month (for monthly) -->
          <v-select v-if="form.frequency === 'monthly'" v-model="form.dayOfMonth" :items="dayOfMonthOptions" label="Day of Month" variant="outlined" density="comfortable" class="mb-3" />

          <!-- Start Date -->
          <v-text-field v-model="form.startDate" label="Start Date" type="date" variant="outlined" density="comfortable" class="mb-3" :rules="[required]" />

          <!-- End Date (optional) -->
          <v-text-field v-model="form.endDate" label="End Date (optional)" type="date" variant="outlined" density="comfortable" class="mb-3" />
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="lg" @click="save" :loading="saving">
            {{ editing ? 'Save Changes' : 'Create' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="text-error">
          <v-icon color="error" class="mr-2">mdi-alert-circle</v-icon>
          Delete Scheduled Transaction?
        </v-card-title>
        <v-card-text>
          <v-alert type="warning" variant="tonal" class="rounded-lg">
            This will permanently delete the scheduled transaction "{{ deletingSt?.description }}".
          </v-alert>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">Cancel</v-btn>
          <v-btn color="error" rounded="lg" @click="doDelete" :loading="deleting">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

interface ScheduledTx {
  id: number
  transactionType: number
  accountId: number
  accountName: string
  categoryId: number | null
  categoryName: string | null
  destinationAccountId: number | null
  destinationAccountName: string | null
  amount: number
  amountStr: string
  description: string
  frequency: string
  intervalDays: number | null
  dayOfWeek: number | null
  dayOfMonth: number | null
  monthOfYear: number | null
  startDate: number
  endDate: number | null
  nextRunTime: number
  nextRunTimeStr: string
  active: boolean
  lastRunTime: number | null
  lastRunResult: string | null
  runCount: number
}

interface Stats { activeScheduled: number; dailyCount: number; weeklyCount: number; monthlyCount: number; yearlyCount: number }
interface Account { id: number; name: string }
interface Category { id: number; name: string; categoryType: string }

const scheduled = ref<ScheduledTx[]>([])
const stats = ref<Stats | null>(null)
const accounts = ref<Account[]>([])
const categories = ref<Category[]>([])
const loading = ref(true)
const dialog = ref(false)
const saving = ref(false)
const editing = ref<ScheduledTx | null>(null)
const amountStr = ref('')
const required = (v: any) => !!v || 'Required'

// Delete dialog
const deleteDialog = ref(false)
const deletingSt = ref<ScheduledTx | null>(null)
const deleting = ref(false)

// Form
const form = reactive({
  transactionType: 3 as number,
  accountId: 0 as number,
  destinationAccountId: null as number | null,
  categoryId: null as number | null,
  description: '',
  frequency: 'monthly',
  intervalDays: 1,
  dayOfWeek: null as number | null,
  dayOfMonth: 1,
  monthOfYear: null as number | null,
  startDate: new Date().toISOString().split('T')[0],
  endDate: null as string | null,
})

const accountOptions = computed(() => accounts.value.map(a => ({ title: a.name, value: a.id })))
const toAccountOptions = computed(() => accounts.value.filter(a => a.id !== form.accountId).map(a => ({ title: a.name, value: a.id })))
const categoryOptions = computed(() => categories.value.filter(c => form.transactionType === 2 ? c.categoryType === 'INCOME' : c.categoryType === 'EXPENSE').map(c => ({ title: c.name, value: c.id })))

const frequencyOptions = [
  { title: 'Daily', value: 'daily' },
  { title: 'Weekly', value: 'weekly' },
  { title: 'Monthly', value: 'monthly' },
  { title: 'Yearly', value: 'yearly' },
]

const weekdayOptions = [
  { title: 'Sunday', value: 0 },
  { title: 'Monday', value: 1 },
  { title: 'Tuesday', value: 2 },
  { title: 'Wednesday', value: 3 },
  { title: 'Thursday', value: 4 },
  { title: 'Friday', value: 5 },
  { title: 'Saturday', value: 6 },
]

const dayOfMonthOptions = Array.from({ length: 31 }, (_, i) => ({ title: `Day ${i + 1}`, value: i + 1 }))

function frequencyLabel(freq: string) {
  return frequencyOptions.find(f => f.value === freq)?.title || freq
}

function typeColor(type: number) {
  switch (type) {
    case 2: return 'success'
    case 3: return 'error'
    case 4: return 'info'
    default: return 'grey'
  }
}

function typeIcon(type: number) {
  switch (type) {
    case 2: return 'mdi-arrow-down-bold'
    case 3: return 'mdi-arrow-up-bold'
    case 4: return 'mdi-swap-horizontal'
    default: return 'mdi-cash'
  }
}

function amountClass(type: number) {
  if (type === 2 || type === 5) return 'text-success'
  if (type === 3 || type === 4) return 'text-error'
  return ''
}

function amountPrefix(type: number) {
  return (type === 2 || type === 5) ? '+' : '-'
}

function openCreate() {
  editing.value = null
  form.transactionType = 3
  form.accountId = accounts.value[0]?.id || 0
  form.destinationAccountId = null
  form.categoryId = null
  form.description = ''
  form.frequency = 'monthly'
  form.intervalDays = 1
  form.dayOfWeek = null
  form.dayOfMonth = 1
  form.monthOfYear = null
  form.startDate = new Date().toISOString().split('T')[0]
  form.endDate = null
  amountStr.value = ''
  dialog.value = true
}

function openEdit(st: ScheduledTx) {
  editing.value = st
  form.transactionType = st.transactionType
  form.accountId = st.accountId
  form.destinationAccountId = st.destinationAccountId
  form.categoryId = st.categoryId
  form.description = st.description
  form.frequency = st.frequency
  form.intervalDays = st.intervalDays || 1
  form.dayOfWeek = st.dayOfWeek
  form.dayOfMonth = st.dayOfMonth
  form.monthOfYear = st.monthOfYear
  form.startDate = new Date(st.startDate * 1000).toISOString().split('T')[0]
  form.endDate = st.endDate ? new Date(st.endDate * 1000).toISOString().split('T')[0] : null
  amountStr.value = st.amountStr
  dialog.value = true
}

function confirmDelete(st: ScheduledTx) {
  deletingSt.value = st
  deleteDialog.value = true
}

async function doDelete() {
  if (!deletingSt.value) return
  deleting.value = true
  try {
    await api.post('/scheduled_transactions/delete.json', null, { params: { id: deletingSt.value.id } })
    deleteDialog.value = false
    deletingSt.value = null
    await fetchData()
  } finally {
    deleting.value = false
  }
}

async function toggleActive(st: ScheduledTx) {
  await api.post('/scheduled_transactions/toggle_active.json', null, { params: { id: st.id } })
  await fetchData()
}

async function save() {
  saving.value = true
  try {
    const startDate = Math.floor(new Date(form.startDate).getTime() / 1000)
    const endDate = form.endDate ? Math.floor(new Date(form.endDate).getTime() / 1000) : null

    const payload = {
      transactionType: form.transactionType,
      accountId: form.accountId,
      categoryId: form.transactionType !== 4 ? form.categoryId : null,
      destinationAccountId: form.transactionType === 4 ? form.destinationAccountId : null,
      amount: Math.round(parseFloat(amountStr.value || '0') * 100),
      description: form.description,
      frequency: form.frequency,
      intervalDays: form.frequency === 'daily' ? form.intervalDays : null,
      dayOfWeek: form.frequency === 'weekly' ? form.dayOfWeek : null,
      dayOfMonth: form.frequency === 'monthly' ? form.dayOfMonth : null,
      startDate,
      endDate,
    }

    if (editing.value) {
      await api.post('/scheduled_transactions/modify.json', payload, { params: { id: editing.value.id } })
    } else {
      await api.post('/scheduled_transactions/add.json', payload)
    }
    dialog.value = false
    await fetchData()
  } finally {
    saving.value = false
  }
}

async function fetchData() {
  loading.value = true
  try {
    const [sched, statsData, acc, cats] = await Promise.all([
      api.get<ScheduledTx[]>('/scheduled_transactions/list.json'),
      api.get<Stats>('/scheduled_transactions/statistics.json').catch(() => null),
      api.get<Account[]>('/accounts'),
      api.get<Category[]>('/categories'),
    ])
    scheduled.value = sched
    stats.value = statsData
    accounts.value = acc
    categories.value = cats
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.amount-input :deep(input) { font-size: 24px !important; font-weight: 600 !important; }
</style>