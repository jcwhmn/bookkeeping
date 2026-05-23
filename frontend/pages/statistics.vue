<!-- pages/statistics.vue — Statistics Dashboard -->
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

    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <template v-if="!loading && stats">
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

const stats = ref<Statistics | null>(null)
const loading = ref(true)
const incomeChartRef = ref<HTMLElement | null>(null)
const expenseChartRef = ref<HTMLElement | null>(null)

// Month navigation
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

function monthName(m: number) {
  return new Date(2000, m - 1).toLocaleDateString('en-US', { month: 'short' })
}

function prevMonth() {
  if (selectedMonth.value === 1) {
    selectedMonth.value = 12
    selectedYear.value--
  } else {
    selectedMonth.value--
  }
  fetchStats()
}

function nextMonth() {
  if (selectedMonth.value === 12) {
    selectedMonth.value = 1
    selectedYear.value++
  } else {
    selectedMonth.value++
  }
  fetchStats()
}

function selectMonth(m: number) {
  selectedMonth.value = m
  fetchStats()
}

function fmt(cents: number) {
  return (cents / 100).toLocaleString('en-US', { minimumFractionDigits: 2 })
}

async function fetchStats() {
  loading.value = true
  try {
    stats.value = await api.get<Statistics>(
      `/transactions/statistics?year=${selectedYear.value}&month=${selectedMonth.value}`
    )
    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  if (!stats.value || !incomeChartRef.value || !expenseChartRef.value) return

  // Income Pie Chart
  if (stats.value.incomeBreakdown.length > 0) {
    const incomeChart = window.echarts.init(incomeChartRef.value)
    incomeChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: ${c} ({d}%)' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: stats.value.incomeBreakdown.map((item, i) => ({
          name: item.categoryName,
          value: item.amount / 100,
          itemStyle: { color: ['#4CAF50', '#81C784', '#A5D6A7', '#C8E6C9', '#E8F5E9'][i % 5] }
        }))
      }]
    })
  } else if (incomeChartRef.value) {
    incomeChartRef.value.innerHTML = '<div class="text-center text-grey pa-8">No income data</div>'
  }

  // Expense Pie Chart
  if (stats.value.expenseBreakdown.length > 0) {
    const colors = ['#F44336', '#E57373', '#EF5350', '#E53935', '#D32F2F', '#C62828', '#B71C1C', '#FF5252']
    const expenseChart = window.echarts.init(expenseChartRef.value)
    expenseChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: ${c} ({d}%)' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: stats.value.expenseBreakdown.map((item, i) => ({
          name: item.categoryName,
          value: item.amount / 100,
          itemStyle: { color: colors[i % colors.length] }
        }))
      }]
    })
  } else if (expenseChartRef.value) {
    expenseChartRef.value.innerHTML = '<div class="text-center text-grey pa-8">No expense data</div>'
  }
}

onMounted(fetchStats)
</script>

<style scoped>
.month-btn { text-transform: none !important; letter-spacing: normal !important; }
</style>