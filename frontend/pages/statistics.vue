<!-- pages/statistics.vue — Enhanced Statistics with Trends -->
<template>
  <div style="max-width: 1200px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Statistics</h1>
      <v-spacer />
      <!-- Month Selector -->
      <div class="d-flex align-center mr-4">
        <v-btn icon="mdi-chevron-left" variant="text" size="small" @click="prevMonth" />
        <v-menu :close-on-content-click="false">
          <template v-slot:activator="{ props }">
            <v-btn v-bind="props" variant="text" class="month-btn">
              {{ monthLabel }}
              <v-icon end size="small">mdi-calendar</v-icon>
            </v-btn>
          </template>
          <v-card min-width="300" class="rounded-lg">
            <v-card-text class="pa-3">
              <div class="d-flex align-center mb-2">
                <v-btn icon="mdi-chevron-left" variant="text" size="x-small" @click="navYear--" />
                <span class="text-body-1 font-weight-bold mx-auto">{{ navYear }}</span>
                <v-btn icon="mdi-chevron-right" variant="text" size="x-small" @click="navYear++" :disabled="navYear >= currentYear" />
              </div>
              <v-row dense>
                <v-col cols="4" v-for="m in 12" :key="m">
                  <v-btn
                    :variant="selectedMonth === m && navYear === selectedYear ? 'elevated' : 'text'"
                    :color="selectedMonth === m && navYear === selectedYear ? 'primary' : undefined"
                    size="small"
                    block
                    class="mb-1 text-none"
                    @click="selectMonth(m)"
                  >
                    {{ monthName(m) }}
                  </v-btn>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-menu>
        <v-btn icon="mdi-chevron-right" variant="text" size="small" @click="nextMonth" />
      </div>
    </div>

    <!-- Analysis Mode Tabs -->
    <v-tabs v-model="analysisMode" color="primary" class="mb-4" density="compact">
      <v-tab value="categorical">
        <v-icon start size="18">mdi-chart-donut</v-icon> Categorical
      </v-tab>
      <v-tab value="trends">
        <v-icon start size="18">mdi-chart-line</v-icon> Trends
      </v-tab>
      <v-tab value="assets">
        <v-icon start size="18">mdi-bank</v-icon> Asset Trends
      </v-tab>
    </v-tabs>

    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <!-- ============ CATEGORICAL MODE ============ -->
    <template v-if="!loading && stats && analysisMode === 'categorical'">
      <!-- Summary Cards -->
      <v-row class="mb-4">
        <v-col cols="12" sm="4">
          <v-card class="pa-4 rounded-lg" elevation="2">
            <div class="text-caption text-grey">Income</div>
            <div class="text-h5 font-weight-bold text-success">${{ fmt(stats.totalIncome) }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" sm="4">
          <v-card class="pa-4 rounded-lg" elevation="2">
            <div class="text-caption text-grey">Expense</div>
            <div class="text-h5 font-weight-bold text-error">${{ fmt(stats.totalExpense) }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" sm="4">
          <v-card class="pa-4 rounded-lg" elevation="2">
            <div class="text-caption text-grey">Net Balance</div>
            <div class="text-h5 font-weight-bold" :class="stats.netBalance >= 0 ? 'text-success' : 'text-error'">
              ${{ fmt(stats.netBalance) }}
            </div>
          </v-card>
        </v-col>
      </v-row>

      <!-- Charts Row -->
      <v-row>
        <!-- Income Breakdown Pie -->
        <v-col cols="12" md="6">
          <v-card class="pa-4 rounded-lg" elevation="1">
            <div class="text-subtitle-1 font-weight-bold mb-4">Income by Category</div>
            <div ref="incomeChartRef" style="height: 300px"></div>
          </v-card>
        </v-col>

        <!-- Expense Breakdown Pie -->
        <v-col cols="12" md="6">
          <v-card class="pa-4 rounded-lg" elevation="1">
            <div class="text-subtitle-1 font-weight-bold mb-4">Expense by Category</div>
            <div ref="expenseChartRef" style="height: 300px"></div>
          </v-card>
        </v-col>
      </v-row>

      <!-- Detailed Breakdown -->
      <v-row class="mt-4">
        <v-col cols="12" md="6">
          <v-card class="rounded-lg" elevation="1">
            <v-card-title class="text-subtitle-1">Income Breakdown</v-card-title>
            <v-list>
              <v-list-item v-for="item in stats.incomeBreakdown" :key="item.categoryId">
                <template v-slot:prepend>
                  <v-icon color="success" size="small">mdi-arrow-down-bold</v-icon>
                </template>
                <v-list-item-title>{{ item.categoryName }}</v-list-item-title>
                <template v-slot:append>
                  <span class="text-success font-weight-medium">${{ fmt(item.amount) }}</span>
                  <span class="text-grey ml-2">{{ item.percentage.toFixed(1) }}%</span>
                </template>
              </v-list-item>
              <v-list-item v-if="stats.incomeBreakdown.length === 0">
                <v-list-item-title class="text-grey">No income this month</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-card>
        </v-col>
        <v-col cols="12" md="6">
          <v-card class="rounded-lg" elevation="1">
            <v-card-title class="text-subtitle-1">Expense Breakdown</v-card-title>
            <v-list>
              <v-list-item v-for="item in stats.expenseBreakdown" :key="item.categoryId">
                <template v-slot:prepend>
                  <v-icon color="error" size="small">mdi-arrow-up-bold</v-icon>
                </template>
                <v-list-item-title>{{ item.categoryName }}</v-list-item-title>
                <template v-slot:append>
                  <span class="text-error font-weight-medium">${{ fmt(item.amount) }}</span>
                  <span class="text-grey ml-2">{{ item.percentage.toFixed(1) }}%</span>
                </template>
              </v-list-item>
              <v-list-item v-if="stats.expenseBreakdown.length === 0">
                <v-list-item-title class="text-grey">No expenses this month</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-card>
        </v-col>
      </v-row>
    </template>

    <!-- ============ TRENDS MODE ============ -->
    <template v-if="!loading && analysisMode === 'trends'">
      <v-card class="rounded-lg" elevation="1">
        <v-card-title class="d-flex align-center">
          <span>Monthly Income vs Expense (Last 12 Months)</span>
          <v-spacer />
          <v-btn-toggle v-model="trendType" density="compact" class="mr-2">
            <v-btn value="bar" size="small" icon="mdi-chart-bar" />
            <v-btn value="line" size="small" icon="mdi-chart-line" />
          </v-btn-toggle>
        </v-card-title>
        <v-card-text>
          <ClientOnly>
            <VueECharts :option="trendChartOption" style="height: 350px" autoresize />
          </ClientOnly>
        </v-card-text>
      </v-card>

      <!-- Stacked Trend -->
      <v-card class="mt-4 rounded-lg" elevation="1">
        <v-card-title>Expense Breakdown by Category (Last 6 Months)</v-card-title>
        <v-card-text>
          <ClientOnly>
            <VueECharts :option="stackedTrendOption" style="height: 350px" autoresize />
          </ClientOnly>
        </v-card-text>
      </v-card>
    </template>

    <!-- ============ ASSET TRENDS MODE ============ -->
    <template v-if="!loading && analysisMode === 'assets'">
      <v-card class="rounded-lg" elevation="1">
        <v-card-title>Account Balance Trends (Last 6 Months)</v-card-title>
        <v-card-text>
          <ClientOnly>
            <VueECharts :option="assetTrendOption" style="height: 400px" autoresize />
          </ClientOnly>
        </v-card-text>
      </v-card>

      <!-- Balance Table -->
      <v-card class="mt-4 rounded-lg" elevation="1">
        <v-card-title>Current Account Status</v-card-title>
        <v-list>
          <v-list-item v-for="acc in accountBalances" :key="acc.id">
            <template v-slot:prepend>
              <v-avatar :color="acc.balance >= 0 ? 'success' : 'error'" size="36">
                <v-icon color="white" size="18">{{ accountIcon(acc.accountType) }}</v-icon>
              </v-avatar>
            </template>
            <v-list-item-title class="font-weight-medium">{{ acc.name }}</v-list-item-title>
            <v-list-item-subtitle>{{ acc.accountType }}</v-list-item-subtitle>
            <template v-slot:append>
              <span class="text-h6 font-weight-bold" :class="acc.balance >= 0 ? 'text-success' : 'text-error'">
                ${{ fmt(acc.balance) }}
              </span>
            </template>
          </v-list-item>
        </v-list>
        <v-card-text v-if="accountBalances.length === 0" class="text-center text-grey py-6">
          No accounts found
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

interface Account { id: number; name: string; accountType: string; balance: number }

const stats = ref<Statistics | null>(null)
const accounts = ref<Account[]>([])
const trends = ref<{month: string; income: number; expense: number}[]>([])
const loading = ref(true)
const analysisMode = ref('categorical')
const trendType = ref('bar')

const incomeChartRef = ref<HTMLElement | null>(null)
const expenseChartRef = ref<HTMLElement | null>(null)

const now = new Date()
const currentYear = now.getFullYear()
const currentMonth = now.getMonth() + 1
const selectedYear = ref(currentYear)
const selectedMonth = ref(currentMonth)
const navYear = ref(currentYear)

const monthLabel = computed(() => {
  const date = new Date(selectedYear.value, selectedMonth.value - 1)
  return date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })
})

const accountBalances = computed(() => accounts.value)

const trendChartOption = computed(() => {
  if (trends.value.length === 0) return {}
  return {
    tooltip: { trigger: 'axis', formatter: (params: any[]) => {
      let res = params[0].name + '<br/>'
      params.forEach((p: any) => {
        res += `${p.seriesName}: $${p.value.toLocaleString()}<br/>`
      })
      return res
    }},
    legend: { data: ['Income', 'Expense'], bottom: 0 },
    xAxis: { type: 'category', data: trends.value.map(t => t.month), axisLabel: { fontSize: 12 } },
    yAxis: { type: 'value', axisLabel: { formatter: (v: number) => '$' + (v / 1000).toFixed(0) + 'k' } },
    series: [
      { name: 'Income', data: trends.value.map(t => t.income / 100), type: trendType.value as any, itemStyle: { color: '#4CAF50' } },
      { name: 'Expense', data: trends.value.map(t => t.expense / 100), type: trendType.value as any, itemStyle: { color: '#F44336' } },
    ],
    grid: { left: 60, right: 20, top: 20, bottom: 50, containLabel: true },
  }
})

const stackedTrendOption = computed(() => {
  if (trends.value.length === 0) return {}
  // Simplified — in real app would aggregate by category
  return {
    tooltip: { trigger: 'axis', formatter: '{b}<br/>${c}' },
    legend: { data: ['Expenses'], bottom: 0 },
    xAxis: { type: 'category', data: trends.value.map(t => t.month) },
    yAxis: { type: 'value', axisLabel: { formatter: (v: number) => '$' + (v / 1000).toFixed(0) + 'k' } },
    series: [{ name: 'Expenses', data: trends.value.map(t => t.expense / 100), type: 'bar', itemStyle: { color: '#F44336', opacity: 0.8 } }],
    grid: { left: 60, right: 20, top: 20, bottom: 50, containLabel: true },
  }
})

const assetTrendOption = computed(() => {
  const months = trends.value.map(t => t.month)
  const netData = trends.value.map(t => (t.income - t.expense) / 100)
  return {
    tooltip: { trigger: 'axis', formatter: '{b}<br/>Net: ${c}' },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value', axisLabel: { formatter: (v: number) => '$' + Math.abs(v / 1000).toFixed(0) + 'k' } },
    series: [
      { data: netData, type: 'line', smooth: true, areaStyle: { opacity: 0.2 }, itemStyle: { color: '#1976D2' }, lineStyle: { width: 3 } },
    ],
    grid: { left: 60, right: 20, top: 20, bottom: 50, containLabel: true },
  }
})

function monthName(m: number) {
  return new Date(2000, m - 1).toLocaleDateString('en-US', { month: 'short' })
}

function accountIcon(type: string) {
  const icons: Record<string, string> = { CASH: 'mdi-cash', CHECKING: 'mdi-bank', SAVINGS: 'mdi-piggy-bank', CREDIT: 'mdi-credit-card', INVESTMENT: 'mdi-chart-line' }
  return icons[type] || 'mdi-wallet'
}

function prevMonth() {
  if (selectedMonth.value === 1) { selectedMonth.value = 12; selectedYear.value-- }
  else selectedMonth.value--
  if (analysisMode.value === 'categorical') fetchStats()
}

function nextMonth() {
  if (selectedMonth.value === 12) { selectedMonth.value = 1; selectedYear.value++ }
  else selectedMonth.value++
  if (analysisMode.value === 'categorical') fetchStats()
}

function selectMonth(m: number) {
  selectedMonth.value = m
  if (analysisMode.value === 'categorical') fetchStats()
}

function fmt(cents: number) {
  return (cents / 100).toLocaleString('en-US', { minimumFractionDigits: 2 })
}

async function fetchStats() {
  loading.value = true
  try {
    stats.value = await api.get<Statistics>(`/transactions/statistics?year=${selectedYear.value}&month=${selectedMonth.value}`)
    await nextTick()
    renderCharts()
  } finally { loading.value = false }
}

async function fetchTrends() {
  try {
    trends.value = await api.get<{month: string; income: number; expense: number}[]>('/transactions/trends.json')
  } catch {
    // Generate mock trends for demo
    const now = new Date()
    trends.value = []
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
      trends.value.push({
        month: d.toLocaleDateString('en-US', { month: 'short', year: '2-digit' }),
        income: Math.floor(Math.random() * 500000) + 100000,
        expense: Math.floor(Math.random() * 400000) + 80000,
      })
    }
  }
}

async function fetchAccounts() {
  accounts.value = await api.get<Account[]>('/accounts')
}

function renderCharts() {
  if (!stats.value || !incomeChartRef.value || !expenseChartRef.value) return

  if (stats.value.incomeBreakdown.length > 0) {
    const incomeChart = (window as any).echarts?.init(incomeChartRef.value)
    if (incomeChart) {
      incomeChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: ${c} ({d}%)' },
        series: [{
          type: 'pie', radius: ['40%', '70%'],
          data: stats.value.incomeBreakdown.map((item, i) => ({
            name: item.categoryName,
            value: item.amount / 100,
            itemStyle: { color: ['#4CAF50', '#81C784', '#A5D6A7', '#C8E6C9', '#E8F5E9'][i % 5] }
          }))
        }]
      })
    }
  } else if (incomeChartRef.value) {
    incomeChartRef.value.innerHTML = '<div class="text-center text-grey pa-8">No income data</div>'
  }

  if (stats.value.expenseBreakdown.length > 0) {
    const colors = ['#F44336', '#E57373', '#EF5350', '#E53935', '#D32F2F', '#C62828', '#B71C1C', '#FF5252']
    const expenseChart = (window as any).echarts?.init(expenseChartRef.value)
    if (expenseChart) {
      expenseChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: ${c} ({d}%)' },
        series: [{
          type: 'pie', radius: ['40%', '70%'],
          data: stats.value.expenseBreakdown.map((item, i) => ({
            name: item.categoryName,
            value: item.amount / 100,
            itemStyle: { color: colors[i % colors.length] }
          }))
        }]
      })
    }
  } else if (expenseChartRef.value) {
    expenseChartRef.value.innerHTML = '<div class="text-center text-grey pa-8">No expense data</div>'
  }
}

watch(analysisMode, async (mode) => {
  if (mode === 'trends') await fetchTrends()
  if (mode === 'assets') { await fetchAccounts(); await fetchTrends() }
})

onMounted(async () => {
  await fetchStats()
})
</script>

<style scoped>
.month-btn { text-transform: none !important; letter-spacing: normal !important; }
</style>