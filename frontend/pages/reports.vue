<!-- pages/reports.vue — Reports Page -->
<template>
  <div style="max-width: 1100px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Reports</h1>
      <v-spacer />
      <!-- Month Selector -->
      <div class="d-flex align-center mr-4">
        <v-btn icon="mdi-chevron-left" variant="text" size="small" @click="prevMonth" />
        <span class="month-label">{{ monthLabel }}</span>
        <v-btn icon="mdi-chevron-right" variant="text" size="small" @click="nextMonth" />
      </div>
      <v-btn variant="outlined" prepend-icon="mdi-download" @click="exportCsv">
        Export CSV
      </v-btn>
    </div>

    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <template v-if="!loading && stats">
      <!-- Monthly Summary -->
      <v-card class="mb-4 rounded-lg" elevation="2">
        <v-card-title class="d-flex align-center">
          <v-icon color="primary" class="mr-2">mdi-file-document-outline</v-icon>
          Monthly Summary Report
        </v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="12" sm="4" class="text-center border-right">
              <div class="text-caption text-grey mb-1">Total Income</div>
              <div class="text-h4 text-success font-weight-bold">${{ fmt(stats.totalIncome) }}</div>
            </v-col>
            <v-col cols="12" sm="4" class="text-center border-right">
              <div class="text-caption text-grey mb-1">Total Expenses</div>
              <div class="text-h4 text-error font-weight-bold">${{ fmt(stats.totalExpense) }}</div>
            </v-col>
            <v-col cols="12" sm="4" class="text-center">
              <div class="text-caption text-grey mb-1">Net Balance</div>
              <div class="text-h4 font-weight-bold" :class="stats.netBalance >= 0 ? 'text-success' : 'text-error'">
                ${{ fmt(stats.netBalance) }}
              </div>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <!-- Cash Flow Statement -->
      <v-card class="mb-4 rounded-lg" elevation="2">
        <v-card-title class="d-flex align-center">
          <v-icon color="info" class="mr-2">mdi-cash-multiple</v-icon>
          Cash Flow Statement
        </v-card-title>
        <v-card-text>
          <table class="w-100">
            <thead>
              <tr class="border-bottom">
                <th class="text-left py-2 text-grey">Category</th>
                <th class="text-right py-2 text-grey">Amount</th>
                <th class="text-right py-2 text-grey">% of Total</th>
              </tr>
            </thead>
            <tbody>
              <!-- Income Section -->
              <tr class="bg-success-lighten pa-2">
                <td colspan="3" class="py-2 font-weight-bold text-success">Income</td>
              </tr>
              <tr v-for="item in stats.incomeBreakdown" :key="'in-' + item.categoryId">
                <td class="py-1 pl-4">{{ item.categoryName }}</td>
                <td class="py-1 text-right text-success">${{ fmt(item.amount) }}</td>
                <td class="py-1 text-right text-grey">{{ item.percentage.toFixed(1) }}%</td>
              </tr>
              <tr class="border-top font-weight-bold">
                <td class="py-2">Total Income</td>
                <td class="py-2 text-right text-success">${{ fmt(stats.totalIncome) }}</td>
                <td class="py-2 text-right">100%</td>
              </tr>
              
              <!-- Expense Section -->
              <tr>
                <td colspan="3" class="py-2 font-weight-bold text-error">Expenses</td>
              </tr>
              <tr v-for="item in stats.expenseBreakdown" :key="'ex-' + item.categoryId">
                <td class="py-1 pl-4">{{ item.categoryName }}</td>
                <td class="py-1 text-right text-error">-${{ fmt(item.amount) }}</td>
                <td class="py-1 text-right text-grey">{{ item.percentage.toFixed(1) }}%</td>
              </tr>
              <tr class="border-top font-weight-bold">
                <td class="py-2">Total Expenses</td>
                <td class="py-2 text-right text-error">-${{ fmt(stats.totalExpense) }}</td>
                <td class="py-2 text-right">100%</td>
              </tr>
            </tbody>
            <tfoot class="border-top-2">
              <tr class="text-h6 font-weight-bold">
                <td class="py-3">Net Balance</td>
                <td class="py-3 text-right" :class="stats.netBalance >= 0 ? 'text-success' : 'text-error'">
                  ${{ fmt(stats.netBalance) }}
                </td>
                <td class="py-3 text-right"></td>
              </tr>
            </tfoot>
          </table>
        </v-card-text>
      </v-card>

      <!-- Transaction Count Summary -->
      <v-card class="mb-4 rounded-lg" elevation="2">
        <v-card-title>
          <v-icon color="primary" class="mr-2">mdi-counter</v-icon>
          Transaction Summary
        </v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="6" sm="3" class="text-center">
              <div class="text-h5 font-weight-bold text-success">{{ stats.transactionCount }}</div>
              <div class="text-caption text-grey">Total Transactions</div>
            </v-col>
            <v-col cols="6" sm="3" class="text-center">
              <div class="text-h5 font-weight-bold text-success">{{ stats.incomeBreakdown.reduce((s, i) => s + i.count, 0) }}</div>
              <div class="text-caption text-grey">Income Entries</div>
            </v-col>
            <v-col cols="6" sm="3" class="text-center">
              <div class="text-h5 font-weight-bold text-error">{{ stats.expenseBreakdown.reduce((s, i) => s + i.count, 0) }}</div>
              <div class="text-caption text-grey">Expense Entries</div>
            </v-col>
            <v-col cols="6" sm="3" class="text-center">
              <div class="text-h5 font-weight-bold text-info">{{ transactions.filter(t => t.transactionType === 4 || t.transactionType === 5).length / 2 }}</div>
              <div class="text-caption text-grey">Transfers</div>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <!-- Top Spending Categories -->
      <v-card class="rounded-lg" elevation="2">
        <v-card-title>
          <v-icon color="error" class="mr-2">mdi-trending-up</v-icon>
          Top Spending Categories
        </v-card-title>
        <v-card-text>
          <v-list>
            <v-list-item v-for="(item, i) in topExpenses" :key="item.categoryId">
              <template v-slot:prepend>
                <v-avatar color="error" size="32" class="mr-3">
                  <span class="text-white text-caption font-weight-bold">{{ i + 1 }}</span>
                </v-avatar>
              </template>
              <v-list-item-title class="font-weight-medium">{{ item.categoryName }}</v-list-item-title>
              <v-list-item-subtitle>
                {{ item.count }} transactions
              </v-list-item-subtitle>
              <template v-slot:append>
                <span class="text-error font-weight-bold mr-4">${{ fmt(item.amount) }}</span>
                <v-progress-linear
                  :model-value="item.percentage"
                  :color="item.percentage > 50 ? 'error' : 'warning'"
                  height="8"
                  style="width: 100px"
                  rounded
                />
              </template>
            </v-list-item>
          </v-list>
        </v-card-text>
      </v-card>
    </template>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

interface CategoryBreakdown {
  categoryId: number
  categoryName: string
  amount: number
  count: number
  percentage: number
}

interface Statistics {
  totalIncome: number
  totalExpense: number
  netBalance: number
  transactionCount: number
  incomeBreakdown: CategoryBreakdown[]
  expenseBreakdown: CategoryBreakdown[]
}

interface Tx {
  id: number
  transactionType: number
  accountId: number
  amount: number
  description: string
  transactionTime: number
}

const stats = ref<Statistics | null>(null)
const transactions = ref<Tx[]>([])
const loading = ref(true)

const now = new Date()
const currentYear = now.getFullYear()
const currentMonth = now.getMonth() + 1
const selectedYear = ref(currentYear)
const selectedMonth = ref(currentMonth)

const monthLabel = computed(() => {
  const date = new Date(selectedYear.value, selectedMonth.value - 1)
  return date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
})

const topExpenses = computed(() => {
  if (!stats.value) return []
  return stats.value.expenseBreakdown.slice(0, 5)
})

function prevMonth() {
  if (selectedMonth.value === 1) {
    selectedMonth.value = 12
    selectedYear.value--
  } else {
    selectedMonth.value--
  }
  fetchData()
}

function nextMonth() {
  if (selectedMonth.value === 12) {
    selectedMonth.value = 1
    selectedYear.value++
  } else {
    selectedMonth.value++
  }
  fetchData()
}

function fmt(cents: number) {
  return (cents / 100).toLocaleString('en-US', { minimumFractionDigits: 2 })
}

function exportCsv() {
  if (!stats.value) return
  
  const rows = [
    ['Category', 'Type', 'Amount', 'Count', 'Percentage'],
    ...stats.value.incomeBreakdown.map(i => [i.categoryName, 'Income', `$${fmt(i.amount)}`, i.count.toString(), `${i.percentage.toFixed(1)}%`]),
    ...stats.value.expenseBreakdown.map(e => [e.categoryName, 'Expense', `$${fmt(e.amount)}`, e.count.toString(), `${e.percentage.toFixed(1)}%`]),
    ['TOTAL', '', `$${fmt(stats.value.netBalance)}`, stats.value.transactionCount.toString(), ''],
  ]
  
  const csv = rows.map(r => r.join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `report-${selectedYear.value}-${selectedMonth.value}.csv`
  a.click()
  URL.revokeObjectURL(url)
}

async function fetchData() {
  loading.value = true
  try {
    const [st, txs] = await Promise.all([
      api.get<Statistics>(`/transactions/statistics?year=${selectedYear.value}&month=${selectedMonth.value}`),
      api.get<Tx[]>(`/transactions?year=${selectedYear.value}&month=${selectedMonth.value}`),
    ])
    stats.value = st
    transactions.value = txs
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.border-right { border-right: 1px solid #eee; }
.border-bottom { border-bottom: 1px solid #eee; }
.border-top { border-top: 1px solid #eee; }
.border-top-2 { border-top: 2px solid #333; }
.border { border-bottom: 1px solid #eee; }
.bg-success-lighten { background: rgba(76, 175, 80, 0.1); }
</style>