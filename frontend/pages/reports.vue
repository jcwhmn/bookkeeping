<!-- pages/reports.vue — Enhanced Reports with Multiple Views -->
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

    <!-- Report Type Tabs -->
    <v-tabs v-model="reportType" color="primary" class="mb-4" density="compact">
      <v-tab value="summary">
        <v-icon start size="18">mdi-file-document-outline</v-icon> Summary
      </v-tab>
      <v-tab value="cashflow">
        <v-icon start size="18">mdi-cash-multiple</v-icon> Cash Flow
      </v-tab>
      <v-tab value="reconciliation">
        <v-icon start size="18">mdi-bank-transfer</v-icon> Reconciliation
      </v-tab>
    </v-tabs>

    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <!-- ============ SUMMARY REPORT ============ -->
    <template v-if="!loading && stats && reportType === 'summary'">
      <!-- Monthly Summary Cards -->
      <v-row class="mb-4">
        <v-col cols="12" sm="4">
          <v-card class="pa-4 rounded-lg text-center" elevation="2">
            <div class="text-caption text-grey mb-1">Total Income</div>
            <div class="text-h4 text-success font-weight-bold">${{ fmt(stats.totalIncome) }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" sm="4">
          <v-card class="pa-4 rounded-lg text-center" elevation="2">
            <div class="text-caption text-grey mb-1">Total Expenses</div>
            <div class="text-h4 text-error font-weight-bold">${{ fmt(stats.totalExpense) }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" sm="4">
          <v-card class="pa-4 rounded-lg text-center" elevation="2">
            <div class="text-caption text-grey mb-1">Net Balance</div>
            <div class="text-h4 font-weight-bold" :class="stats.netBalance >= 0 ? 'text-success' : 'text-error'">
              ${{ fmt(stats.netBalance) }}
            </div>
          </v-card>
        </v-col>
      </v-row>

      <!-- Transaction Count Summary -->
      <v-card class="mb-4 rounded-lg" elevation="1">
        <v-card-title>
          <v-icon color="primary" class="mr-2">mdi-counter</v-icon>
          Transaction Summary
        </v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="6" sm="3" class="text-center">
              <div class="text-h5 font-weight-bold">{{ stats.transactionCount }}</div>
              <div class="text-caption text-grey">Total</div>
            </v-col>
            <v-col cols="6" sm="3" class="text-center">
              <div class="text-h5 font-weight-bold text-success">{{ stats.incomeBreakdown.reduce((s, i) => s + i.count, 0) }}</div>
              <div class="text-caption text-grey">Income</div>
            </v-col>
            <v-col cols="6" sm="3" class="text-center">
              <div class="text-h5 font-weight-bold text-error">{{ stats.expenseBreakdown.reduce((s, i) => s + i.count, 0) }}</div>
              <div class="text-caption text-grey">Expense</div>
            </v-col>
            <v-col cols="6" sm="3" class="text-center">
              <div class="text-h5 font-weight-bold text-info">{{ transferCount }}</div>
              <div class="text-caption text-grey">Transfers</div>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <!-- Top Spending Categories -->
      <v-card class="rounded-lg" elevation="1">
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
              <v-list-item-subtitle>{{ item.count }} transactions</v-list-item-subtitle>
              <template v-slot:append>
                <span class="text-error font-weight-bold mr-4">${{ fmt(item.amount) }}</span>
                <v-progress-linear :model-value="item.percentage" :color="item.percentage > 50 ? 'error' : 'warning'" height="8" style="width: 100px" rounded />
              </template>
            </v-list-item>
          </v-list>
        </v-card-text>
      </v-card>
    </template>

    <!-- ============ CASH FLOW REPORT ============ -->
    <template v-if="!loading && stats && reportType === 'cashflow'">
      <v-card class="rounded-lg" elevation="1">
        <v-card-title class="d-flex align-center">
          <v-icon color="info" class="mr-2">mdi-cash-multiple</v-icon>
          Cash Flow Statement — {{ monthLabel }}
          <v-spacer />
          <v-btn variant="text" size="small" @click="exportCashFlow">Export</v-btn>
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
              <tr class="bg-success-lighten">
                <td colspan="3" class="py-2 font-weight-bold text-success">Income</td>
              </tr>
              <tr v-for="item in stats.incomeBreakdown" :key="'in-' + item.categoryId" class="border-bottom">
                <td class="py-1 pl-4">{{ item.categoryName }}</td>
                <td class="py-1 text-right text-success">${{ fmt(item.amount) }}</td>
                <td class="py-1 text-right text-grey">{{ item.percentage.toFixed(1) }}%</td>
              </tr>
              <tr class="font-weight-bold border-top">
                <td class="py-2">Total Income</td>
                <td class="py-2 text-right text-success">${{ fmt(stats.totalIncome) }}</td>
                <td class="py-2 text-right">100%</td>
              </tr>
              
              <!-- Expense Section -->
              <tr class="bg-error-lighten">
                <td colspan="3" class="py-2 font-weight-bold text-error">Expenses</td>
              </tr>
              <tr v-for="item in stats.expenseBreakdown" :key="'ex-' + item.categoryId" class="border-bottom">
                <td class="py-1 pl-4">{{ item.categoryName }}</td>
                <td class="py-1 text-right text-error">-${{ fmt(item.amount) }}</td>
                <td class="py-1 text-right text-grey">{{ item.percentage.toFixed(1) }}%</td>
              </tr>
              <tr class="font-weight-bold border-top">
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
    </template>

    <!-- ============ RECONCILIATION REPORT ============ -->
    <template v-if="!loading && reportType === 'reconciliation'">
      <!-- Account Selector -->
      <v-card class="mb-4 rounded-lg" elevation="1">
        <v-card-title>Reconciliation Statement</v-card-title>
        <v-card-text>
          <v-select v-model="selectedAccountId" :items="accountOptions" label="Select Account" variant="outlined" density="comfortable" class="mb-4" />
          
          <v-row v-if="selectedAccountId">
            <v-col cols="12" sm="4">
              <div class="text-center pa-3 rounded-lg" style="background: rgba(76, 175, 80, 0.1)">
                <div class="text-caption text-grey">Starting Balance</div>
                <div class="text-h5 font-weight-bold text-success">${{ fmt(reconStatement.startBalance) }}</div>
              </div>
            </v-col>
            <v-col cols="12" sm="4">
              <div class="text-center pa-3 rounded-lg" style="background: rgba(33, 150, 243, 0.1)">
                <div class="text-caption text-grey">+ Incoming</div>
                <div class="text-h5 font-weight-bold text-info">${{ fmt(reconStatement.totalIncome) }}</div>
              </div>
            </v-col>
            <v-col cols="12" sm="4">
              <div class="text-center pa-3 rounded-lg" style="background: rgba(244, 67, 54, 0.1)">
                <div class="text-caption text-grey">- Outgoing</div>
                <div class="text-h5 font-weight-bold text-error">${{ fmt(reconStatement.totalExpense) }}</div>
              </div>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <!-- Transaction List for Reconciliation -->
      <v-card v-if="selectedAccountId" class="rounded-lg" elevation="1">
        <v-card-title class="d-flex align-center">
          <span>Transactions — {{ monthLabel }}</span>
          <v-spacer />
          <span class="text-caption text-grey">Ending Balance: </span>
          <span class="text-h6 font-weight-bold ml-2" :class="reconStatement.endBalance >= 0 ? 'text-success' : 'text-error'">
            ${{ fmt(reconStatement.endBalance) }}
          </span>
        </v-card-title>
        <v-list lines="two">
          <v-list-item v-for="tx in reconTransactions" :key="tx.id" class="border-b">
            <template v-slot:prepend>
              <v-avatar :color="typeColor(tx.transactionType)" size="36" class="mr-3">
                <v-icon color="white" size="18">{{ typeIcon(tx.transactionType) }}</v-icon>
              </v-avatar>
            </template>
            <v-list-item-title class="font-weight-medium">{{ tx.description || 'No description' }}</v-list-item-title>
            <v-list-item-subtitle>
              {{ formatDate(tx.transactionTime) }}
              <span v-if="tx.transactionType === 4 || tx.transactionType === 5"> → {{ getDestAccountName(tx.destinationAccountId) }}</span>
            </v-list-item-subtitle>
            <template v-slot:append>
              <span class="text-body-1 font-weight-bold" :class="amountClass(tx.transactionType)">
                {{ amountPrefix(tx.transactionType) }}${{ fmt(tx.amount) }}
              </span>
            </template>
          </v-list-item>
        </v-list>
        <v-card-text v-if="reconTransactions.length === 0" class="text-center text-grey py-6">
          No transactions for this account in this period
        </v-card-text>
      </v-card>

      <!-- No Account Selected -->
      <v-card v-if="!selectedAccountId" class="rounded-lg" elevation="1">
        <v-card-text class="text-center text-grey py-6">
          <v-icon size="48" color="grey">mdi-finance</v-icon>
          <div class="mt-3">Select an account to view the reconciliation statement</div>
        </v-card-text>
      </v-card>
    </template>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

interface CategoryBreakdown {
  categoryId: number; categoryName: string; amount: number; count: number; percentage: number
}
interface Statistics {
  totalIncome: number; totalExpense: number; netBalance: number; transactionCount: number
  incomeBreakdown: CategoryBreakdown[]; expenseBreakdown: CategoryBreakdown[]
}
interface Account { id: number; name: string; accountType: string; balance: number }
interface Tx {
  id: number; transactionType: number; accountId: number; destinationAccountId: number | null
  amount: number; description: string; transactionTime: number
}

const stats = ref<Statistics | null>(null)
const transactions = ref<Tx[]>([])
const accounts = ref<Account[]>([])
const loading = ref(true)
const reportType = ref('summary')
const selectedAccountId = ref<number | null>(null)

const now = new Date()
const currentYear = now.getFullYear()
const currentMonth = now.getMonth() + 1
const selectedYear = ref(currentYear)
const selectedMonth = ref(currentMonth)

const monthLabel = computed(() => {
  const date = new Date(selectedYear.value, selectedMonth.value - 1)
  return date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
})

const accountOptions = computed(() => accounts.value.map(a => ({ title: a.name, value: a.id })))
const topExpenses = computed(() => stats.value ? stats.value.expenseBreakdown.slice(0, 5) : [])
const transferCount = computed(() => Math.floor(transactions.value.filter(t => t.transactionType === 4 || t.transactionType === 5).length / 2))

// Reconciliation
const reconTransactions = computed(() =>
  transactions.value.filter(t => t.accountId === selectedAccountId.value)
)
const reconStatement = computed(() => {
  const txs = reconTransactions.value
  const acc = accounts.value.find(a => a.id === selectedAccountId.value)
  const startBalance = acc?.balance || 0
  const totalIncome = txs.filter(t => t.transactionType === 2 || t.transactionType === 5).reduce((s, t) => s + t.amount, 0)
  const totalExpense = txs.filter(t => t.transactionType === 3 || t.transactionType === 4).reduce((s, t) => s + t.amount, 0)
  return {
    startBalance,
    totalIncome,
    totalExpense,
    endBalance: startBalance + totalIncome - totalExpense
  }
})

function prevMonth() {
  if (selectedMonth.value === 1) { selectedMonth.value = 12; selectedYear.value-- }
  else selectedMonth.value--
  fetchData()
}
function nextMonth() {
  if (selectedMonth.value === 12) { selectedMonth.value = 1; selectedYear.value++ }
  else selectedMonth.value++
  fetchData()
}
function fmt(cents: number) { return (cents / 100).toLocaleString('en-US', { minimumFractionDigits: 2 }) }
function formatDate(ts: number) { return new Date(ts * 1000).toLocaleDateString('en-US') }
function getDestAccountName(id: number | null) { return id ? accounts.value.find(a => a.id === id)?.name || '-' : '-' }

function typeColor(type: number) {
  switch (type) { case 2: return 'success'; case 3: return 'error'; case 4: return 'info'; case 5: return 'info'; default: return 'grey' }
}
function typeIcon(type: number) {
  switch (type) { case 2: return 'mdi-arrow-down-bold'; case 3: return 'mdi-arrow-up-bold'; case 4: return 'mdi-swap-horizontal'; case 5: return 'mdi-swap-horizontal'; default: return 'mdi-cash' }
}
function amountClass(type: number) { return type === 2 || type === 5 ? 'text-success' : 'text-error' }
function amountPrefix(type: number) { return type === 2 || type === 5 ? '+' : '-' }

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
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = `report-${selectedYear.value}-${selectedMonth.value}.csv`
  a.click()
}

function exportCashFlow() {
  if (!stats.value) return
  const rows = [
    ['Cash Flow Statement', monthLabel.value],
    ['Income', '', `$${fmt(stats.value.totalIncome)}`],
    ...stats.value.incomeBreakdown.map(i => ['', i.categoryName, `$${fmt(i.amount)}`]),
    ['Expenses', '', `$${fmt(stats.value.totalExpense)}`],
    ...stats.value.expenseBreakdown.map(e => ['', e.categoryName, `$${fmt(e.amount)}`]),
    ['Net Balance', '', `$${fmt(stats.value.netBalance)}`],
  ]
  const csv = rows.map(r => r.join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = `cashflow-${selectedYear.value}-${selectedMonth.value}.csv`
  a.click()
}

async function fetchData() {
  loading.value = true
  try {
    const [st, txs, acc] = await Promise.all([
      api.get<Statistics>(`/transactions/statistics?year=${selectedYear.value}&month=${selectedMonth.value}`),
      api.get<Tx[]>(`/transactions?year=${selectedYear.value}&month=${selectedMonth.value}&limit=500`),
      api.get<Account[]>('/accounts'),
    ])
    stats.value = st
    transactions.value = txs
    accounts.value = acc
    if (acc.length > 0 && !selectedAccountId.value) selectedAccountId.value = acc[0].id
  } finally { loading.value = false }
}

onMounted(fetchData)
</script>

<style scoped>
.border-bottom { border-bottom: 1px solid #eee; }
.border-top { border-top: 1px solid #eee; }
.border-top-2 { border-top: 2px solid #333; }
.bg-success-lighten { background: rgba(76, 175, 80, 0.1); }
.bg-error-lighten { background: rgba(244, 67, 54, 0.1); }
</style>