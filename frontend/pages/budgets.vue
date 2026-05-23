<!-- pages/budgets.vue — Budget Management -->
<template>
  <div style="max-width: 900px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Budgets</h1>
      <v-spacer />
      <!-- Month Selector -->
      <div class="d-flex align-center mr-4">
        <v-btn icon="mdi-chevron-left" variant="text" size="small" @click="prevMonth" />
        <span class="month-label">{{ monthLabel }}</span>
        <v-btn icon="mdi-chevron-right" variant="text" size="small" @click="nextMonth" />
      </div>
      <v-btn color="primary" prepend-icon="mdi-plus" rounded="lg" @click="openCreate()">Add Budget</v-btn>
    </div>

    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <!-- Budget Cards -->
    <v-row v-if="!loading">
      <v-col v-for="budget in budgets" :key="budget.id" cols="12" sm="6" md="4">
        <v-card class="rounded-lg pa-4" elevation="2">
          <div class="d-flex align-center mb-2">
            <v-icon :color="budgetColor(budget.percentUsed)" class="mr-2">mdi-tag</v-icon>
            <span class="text-subtitle-1 font-weight-medium">{{ budget.categoryName }}</span>
          </div>
          
          <div class="mb-2">
            <span class="text-h5 font-weight-bold">${{ fmt(budget.amount) }}</span>
            <span class="text-caption text-grey ml-2">budget</span>
          </div>
          
          <v-progress-linear
            :model-value="Math.min(budget.percentUsed, 100)"
            :color="budgetColor(budget.percentUsed)"
            height="12"
            rounded
            class="mb-2"
          />
          
          <div class="d-flex justify-space-between text-body-2">
            <span :class="budgetPercentClass(budget.percentUsed)">
              ${{ fmt(budget.spent) }} spent ({{ budget.percentUsed.toFixed(0) }}%)
            </span>
            <span class="text-grey">${{ fmt(budget.amount - budget.spent) }} left</span>
          </div>
          
          <v-chip v-if="budget.percentUsed >= 100" color="error" size="small" class="mt-2">
            Over budget!
          </v-chip>
          
          <div class="d-flex justify-end mt-2">
            <v-btn icon="mdi-pencil" variant="text" size="x-small" @click="openEdit(budget)" />
            <v-btn icon="mdi-delete" variant="text" size="x-small" color="error" @click="confirmDelete(budget)" />
          </div>
        </v-card>
      </v-col>
      
      <v-col v-if="budgets.length === 0" cols="12">
        <v-alert type="info" variant="tonal" class="rounded-lg">
          No budgets for {{ monthLabel }}. 
          <v-btn variant="text" size="small" @click="openCreate()">Create one</v-btn>
        </v-alert>
      </v-col>
    </v-row>

    <!-- Create/Edit Dialog -->
    <v-dialog v-model="dialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          {{ editingBudget ? 'Edit Budget' : 'Add Budget' }}
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="dialog = false" />
        </v-card-title>
        <v-card-text>
          <v-select 
            v-model="form.categoryId" 
            :items="categoryOptions" 
            label="Category" 
            variant="outlined" 
            density="comfortable" 
            class="mb-3"
            :disabled="!!editingBudget"
          />
          <v-text-field 
            v-model="amountStr" 
            label="Budget Amount" 
            variant="outlined" 
            density="comfortable" 
            prefix="$"
          />
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="lg" @click="save" :loading="saving">
            {{ editingBudget ? 'Save' : 'Create' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="text-error">
          <v-icon color="error" class="mr-2">mdi-alert-circle</v-icon>
          Delete Budget?
        </v-card-title>
        <v-card-text>
          <v-alert type="warning" variant="tonal" class="rounded-lg">
            Delete budget for <strong>{{ deletingBudget?.categoryName }}</strong>?
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

interface Category { id: number; name: string }
interface Budget {
  id: number; categoryId: number; categoryName: string; amount: number;
  year: number; month: number; spent: number; percentUsed: number
}

const budgets = shallowRef<Budget[]>([])
const categories = shallowRef<Category[]>([])
const loading = ref(true)
const dialog = ref(false)
const saving = ref(false)
const editingBudget = ref<Budget | null>(null)
const amountStr = ref('')

const deleteDialog = ref(false)
const deletingBudget = ref<Budget | null>(null)
const deleting = ref(false)

const form = reactive({ categoryId: null as number | null })

const now = new Date()
const currentYear = now.getFullYear()
const currentMonth = now.getMonth() + 1
const selectedYear = ref(currentYear)
const selectedMonth = ref(currentMonth)

const monthLabel = computed(() => {
  const date = new Date(selectedYear.value, selectedMonth.value - 1)
  return date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
})

const categoryOptions = computed(() => 
  categories.value.map(c => ({ title: c.name, value: c.id }))
)

function prevMonth() {
  if (selectedMonth.value === 1) {
    selectedMonth.value = 12
    selectedYear.value--
  } else {
    selectedMonth.value--
  }
  fetchBudgets()
}

function nextMonth() {
  if (selectedMonth.value === 12) {
    selectedMonth.value = 1
    selectedYear.value++
  } else {
    selectedMonth.value++
  }
  fetchBudgets()
}

function fmt(cents: number) {
  return (cents / 100).toLocaleString('en-US', { minimumFractionDigits: 2 })
}

function budgetColor(percent: number) {
  if (percent >= 100) return 'error'
  if (percent >= 80) return 'warning'
  return 'success'
}

function budgetPercentClass(percent: number) {
  if (percent >= 100) return 'text-error font-weight-medium'
  if (percent >= 80) return 'text-warning font-weight-medium'
  return 'text-success'
}

function openCreate() {
  editingBudget.value = null
  form.categoryId = categories.value[0]?.id || null
  amountStr.value = ''
  dialog.value = true
}

function openEdit(budget: Budget) {
  editingBudget.value = budget
  form.categoryId = budget.categoryId
  amountStr.value = (budget.amount / 100).toString()
  dialog.value = true
}

function confirmDelete(budget: Budget) {
  deletingBudget.value = budget
  deleteDialog.value = true
}

async function doDelete() {
  if (!deletingBudget.value) return
  deleting.value = true
  try {
    await api.delete(`/budgets/${deletingBudget.value.id}`)
    deleteDialog.value = false
    deletingBudget.value = null
    await fetchBudgets()
  } finally { deleting.value = false }
}

async function fetchBudgets() {
  loading.value = true
  try {
    const [bud, cats] = await Promise.all([
      api.get<Budget[]>(`/budgets?year=${selectedYear.value}&month=${selectedMonth.value}`),
      api.get<Category[]>('/categories'),
    ])
    budgets.value = bud
    categories.value = cats
  } finally { loading.value = false }
}

async function save() {
  if (!form.categoryId || !amountStr.value) return
  saving.value = true
  try {
    const amount = Math.round(parseFloat(amountStr.value) * 100)
    if (editingBudget.value) {
      await api.put(`/budgets/${editingBudget.value.id}`, { amount })
    } else {
      await api.post('/budgets', {
        categoryId: form.categoryId,
        amount,
        year: selectedYear.value,
        month: selectedMonth.value,
      })
    }
    dialog.value = false
    await fetchBudgets()
  } finally { saving.value = false }
}

onMounted(fetchBudgets)
</script>

<style scoped>
.month-label { 
  min-width: 140px; 
  text-align: center; 
  font-weight: 500;
}
</style>