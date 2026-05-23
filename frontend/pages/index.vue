<!-- pages/index.vue — Enhanced Dashboard with Charts -->
<template>
  <div style="max-width: 1200px; margin: 0 auto">
    <h1 class="text-h4 font-weight-bold mb-6">Dashboard</h1>

    <!-- Summary Cards -->
    <v-row>
      <v-col cols="12" sm="6" md="3" v-for="card in summaryCards" :key="card.title">
        <v-card class="pa-4 rounded-lg" elevation="2">
          <div class="d-flex align-center mb-2">
            <v-icon :color="card.color" size="20" class="mr-2">{{ card.icon }}</v-icon>
            <span class="text-caption text-grey-darken-1">{{ card.title }}</span>
          </div>
          <div class="text-h5 font-weight-bold" :class="card.valueClass">
            {{ card.prefix }}${{ fmt(card.value) }}
          </div>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-6">
      <!-- Monthly Income/Expense Bar Chart -->
      <v-col cols="12" md="8">
        <v-card class="rounded-lg" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon color="primary" class="mr-2">mdi-chart-bar</v-icon>
            Income vs Expense (Last 6 Months)
          </v-card-title>
          <v-card-text>
            <ClientOnly>
              <VueECharts :option="monthlyBarOption" style="height: 300px" autoresize />
            </ClientOnly>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Expense Breakdown Pie -->
      <v-col cols="12" md="4">
        <v-card class="rounded-lg" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon color="error" class="mr-2">mdi-chart-donut</v-icon>
            Expense Breakdown
          </v-card-title>
          <v-card-text>
            <ClientOnly>
              <VueECharts v-if="expensePieOption.series" :option="expensePieOption" style="height: 250px" autoresize />
              <div v-else class="text-caption text-grey text-center py-8">No expense data</div>
            </ClientOnly>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Balance Trend Chart -->
    <v-row class="mt-4">
      <v-col cols="12">
        <v-card class="rounded-lg" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon color="success" class="mr-2">mdi-chart-line</v-icon>
            Balance Trend (This Month)
          </v-card-title>
          <v-card-text>
            <ClientOnly>
              <VueECharts :option="balanceTrendOption" style="height: 280px" autoresize />
            </ClientOnly>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Recent Transactions -->
    <v-card class="mt-4 rounded-lg" elevation="2">
      <v-card-title class="d-flex align-center">
        <v-icon color="primary" class="mr-2">mdi-history</v-icon>
        Recent Transactions
        <v-spacer />
        <v-btn variant="text" size="small" to="/transactions" class="text-none">View All →</v-btn>
      </v-card-title>
      <v-list lines="two" v-if="recentTransactions.length">
        <v-list-item v-for="tx in recentTransactions" :key="tx.id">
          <template v-slot:prepend>
            <v-avatar :color="typeColor(tx.transactionType)" size="36" class="mr-3">
              <v-icon size="18" color="white">{{ typeIcon(tx.transactionType) }}</v-icon>
            </v-avatar>
          </template>
          <v-list-item-title class="font-weight-medium">
            {{ tx.description }}
            <span class="text-caption text-grey ml-2">— {{ accountName(tx.accountId) }}</span>
          </v-list-item-title>
          <v-list-item-subtitle>{{ formatDate(tx.transactionTime) }}</v-list-item-subtitle>
          <template v-slot:append>
            <span class="text-body-1 font-weight-bold" :class="amountClass(tx.transactionType)">
              {{ amountPrefix(tx.transactionType) }}${{ fmt(tx.amount) }}
            </span>
          </template>
        </v-list-item>
      </v-list>
      <v-card-text v-else class="text-caption text-grey text-center py-6">
        No transactions yet. <v-btn variant="text" size="small" to="/transactions">Add your first transaction</v-btn>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })

const auth = useAuthStore()
const api = useApi()

interface Account { id: number; name: string; balance: number }
interface Tx { 
  id: number; transactionType: number; accountId: number; amount: number; 
  description: string; transactionTime: number; categoryId: number 
}

const accounts = shallowRef<Account[]>([])
const transactions = shallowRef<Tx[]>([])

const assets = computed(() => accounts.value.filter(a => a.balance >= 0).reduce((s, a) => s + a.balance, 0))
const liabilities = computed(() => accounts.value.filter(a => a.balance < 0).reduce((s, a) => s + Math.abs(a.balance), 0))
const netWorth = computed(() => assets.value - liabilities.value)
const income = computed(() => transactions.value.filter(t => t.transactionType === 2).reduce((s, t) => s + t.amount, 0))
const expense = computed(() => transactions.value.filter(t => t.transactionType === 3).reduce((s, t) => s + t.amount, 0))

const summaryCards = computed(() => [
  { title: 'Assets', value: assets.value, icon: 'mdi-wallet', color: 'success', prefix: '$', valueClass: 'text-success' },
  { title: 'Liabilities', value: liabilities.value, icon: 'mdi-credit-card', color: 'error', prefix: '-$', valueClass: 'text-error' },
  { title: 'Net Worth', value: netWorth.value, icon: 'mdi-chart-bell-curve', color: 'primary', prefix: '', valueClass: netWorth.value >= 0 ? 'text-success' : 'text-error' },
  { title: 'This Month', value: income.value - expense.value, icon: 'mdi-calendar-month', color: 'warning', prefix: '', valueClass: income.value >= expense.value ? 'text-success' : 'text-error' },
])

const recentTransactions = computed(() => transactions.value.slice(0, 6))

// Monthly bar chart data (last 6 months)
const monthlyBarOption = computed(() => {
  const now = new Date()
  const months: string[] = []
  const incomeData: number[] = []
  const expenseData: number[] = []
  
  for (let i = 5; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
    months.push(d.toLocaleDateString('en-US', { month: 'short' }))
    
    const monthStart = Math.floor(d.getTime() / 1000)
    const monthEnd = Math.floor(new Date(d.getFullYear(), d.getMonth() + 1, 1).getTime() / 1000)
    
    const monthTxs = transactions.value.filter(t => 
      t.transactionTime >= monthStart && t.transactionTime < monthEnd
    )
    
    incomeData.push(monthTxs.filter(t => t.transactionType === 2).reduce((s, t) => s + t.amount, 0) / 100)
    expenseData.push(monthTxs.filter(t => t.transactionType === 3).reduce((s, t) => s + t.amount, 0) / 100)
  }
  
  return {
    tooltip: { trigger: 'axis', formatter: '{b}<br/>Income: ${c0}<br/>Expense: ${c1}' },
    legend: { data: ['Income', 'Expense'], bottom: 0 },
    xAxis: { type: 'category', data: months, axisLabel: { fontSize: 12 } },
    yAxis: { type: 'value', axisLabel: { formatter: (v: number) => '$' + (v / 1000).toFixed(0) + 'k' } },
    series: [
      { name: 'Income', data: incomeData, type: 'bar', itemStyle: { color: '#4CAF50' } },
      { name: 'Expense', data: expenseData, type: 'bar', itemStyle: { color: '#F44336' } }
    ],
    grid: { left: 60, right: 20, top: 20, bottom: 50, containLabel: true },
  }
})

// Expense pie chart
const expensePieOption = computed(() => {
  const categoryMap = new Map<string, number>()
  transactions.value.filter(t => t.transactionType === 3).forEach(t => {
    const key = t.description || 'Other'
    categoryMap.set(key, (categoryMap.get(key) || 0) + t.amount)
  })
  
  if (categoryMap.size === 0) return {}
  
  const colors = ['#F44336', '#E57373', '#EF5350', '#E53935', '#D32F2F', '#C62828', '#B71C1C', '#FF5252']
  const data = [...categoryMap.entries()].slice(0, 8).map(([name, amount], i) => ({
    name, value: amount / 100, itemStyle: { color: colors[i % colors.length] }
  }))
  
  return {
    tooltip: { trigger: 'item', formatter: '{b}: ${c} ({d}%)' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data,
      label: { show: false }
    }]
  }
})

// Balance trend (daily cumulative)
const balanceTrendOption = computed(() => {
  const txs = [...transactions.value].sort((a, b) => a.transactionTime - b.transactionTime)
  if (txs.length === 0) return {}
  
  // Group by day
  const dayMap = new Map<string, { income: number; expense: number }>()
  txs.forEach(t => {
    const d = new Date(t.transactionTime * 1000)
    const key = d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
    const day = dayMap.get(key) || { income: 0, expense: 0 }
    if (t.transactionType === 2) day.income += t.amount
    if (t.transactionType === 3) day.expense += t.amount
    dayMap.set(key, day)
  })
  
  const labels: string[] = []
  const data: number[] = []
  let balance = 0
  
  dayMap.forEach((day, label) => {
    labels.push(label)
    balance += day.income - day.expense
    data.push(balance / 100)
  })
  
  return {
    tooltip: { trigger: 'axis', formatter: (params: any) => `${params[0].name}<br/>Balance: $${params[0].value.toLocaleString()}` },
    xAxis: { type: 'category', data: labels, axisLabel: { fontSize: 11 } },
    yAxis: { type: 'value', axisLabel: { formatter: (v: number) => '$' + (v / 1000).toFixed(0) + 'k' } },
    series: [{ data, type: 'line', smooth: true, areaStyle: { opacity: 0.2 }, itemStyle: { color: '#1976D2' }, lineStyle: { width: 3 } }],
    grid: { left: 60, right: 20, top: 20, bottom: 30, containLabel: true },
  }
})

function fmt(c: number) { return (c / 100).toLocaleString('en-US', { minimumFractionDigits: 2 }) }
function formatDate(ts: number) { return new Date(ts * 1000).toLocaleDateString('en-US') }
function accountName(id: number) { return accounts.value.find(a => a.id === id)?.name || '-' }

function typeColor(type: number) {
  switch (type) {
    case 2: return 'success'
    case 3: return 'error'
    case 4: return 'info'
    case 5: return 'info'
    default: return 'grey'
  }
}

function typeIcon(type: number) {
  switch (type) {
    case 2: return 'mdi-arrow-down-bold'
    case 3: return 'mdi-arrow-up-bold'
    case 4: return 'mdi-swap-horizontal'
    case 5: return 'mdi-swap-horizontal'
    default: return 'mdi-cash'
  }
}

function amountClass(type: number) {
  if (type === 2) return 'text-success'
  if (type === 3) return 'text-error'
  if (type === 4) return 'text-error'
  if (type === 5) return 'text-success'
  return ''
}

function amountPrefix(type: number) {
  if (type === 2 || type === 5) return '+'
  return '-'
}

onMounted(async () => {
  await auth.fetchCurrentUser()
  const [acc, txs] = await Promise.all([
    api.get<Account[]>('/accounts'),
    api.get<Tx[]>('/transactions?limit=200'),
  ])
  accounts.value = acc
  transactions.value = txs
})
</script>