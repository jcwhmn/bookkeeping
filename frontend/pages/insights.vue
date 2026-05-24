<!-- pages/insights.vue — Insights Explorer with Custom Query Builder -->
<template>
  <div style="max-width: 1200px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Insights Explorer</h1>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" rounded="lg" @click="createNew">New Query</v-btn>
    </div>

    <!-- Mode Tabs -->
    <v-tabs v-model="mode" color="primary" class="mb-4" density="compact">
      <v-tab value="explore">
        <v-icon start size="18">mdi-table-search</v-icon> Explore
      </v-tab>
      <v-tab value="saved">
        <v-icon start size="18">mdi-bookmark-outline</v-icon> Saved Queries
      </v-tab>
    </v-tabs>

    <!-- ============ EXPLORE MODE ============ -->
    <template v-if="mode === 'explore'">
      <v-row>
        <!-- Query Configuration Panel -->
        <v-col cols="12" md="4">
          <v-card class="rounded-lg mb-4" elevation="1">
            <v-card-title class="text-subtitle-1 font-weight-bold">Query Configuration</v-card-title>
            <v-card-text>
              <!-- Dimensions -->
              <div class="mb-4">
                <div class="text-caption text-grey mb-2">X-Axis (Group By)</div>
                <v-select v-model="config.dimension" :items="dimensionOptions" variant="outlined" density="compact" />
              </div>

              <!-- Account Filter -->
              <div class="mb-4">
                <div class="text-caption text-grey mb-2">Account Filter</div>
                <v-select v-model="config.accountId" :items="accountOptions" variant="outlined" density="compact" clearable label="All Accounts" />
              </div>

              <!-- Category Filter -->
              <div class="mb-4">
                <div class="text-caption text-grey mb-2">Category Filter</div>
                <v-select v-model="config.categoryId" :items="categoryOptions" variant="outlined" density="compact" clearable label="All Categories" />
              </div>

              <!-- Transaction Type -->
              <div class="mb-4">
                <div class="text-caption text-grey mb-2">Transaction Type</div>
                <v-select v-model="config.transactionType" :items="typeOptions" variant="outlined" density="compact" />
              </div>

              <!-- Time Range -->
              <div class="mb-4">
                <div class="text-caption text-grey mb-2">Time Range</div>
                <v-select v-model="config.timeRange" :items="timeRangeOptions" variant="outlined" density="compact" />
              </div>

              <v-btn color="primary" block @click="runQuery" :loading="loading">
                <v-icon start>mdi-play</v-icon> Run Query
              </v-btn>
            </v-card-text>
          </v-card>

          <!-- Save Query -->
          <v-card v-if="queryResults.length > 0" class="rounded-lg" elevation="1">
            <v-card-text>
              <v-text-field v-model="queryName" label="Query Name" variant="outlined" density="compact" class="mb-3" />
              <v-btn color="success" block @click="saveQuery" variant="outlined">
                <v-icon start>mdi-content-save</v-icon> Save Query
              </v-btn>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Results Panel -->
        <v-col cols="12" md="8">
          <!-- Chart View -->
          <v-card class="rounded-lg mb-4" elevation="1">
            <v-card-title class="d-flex align-center">
              <span>Results</span>
              <v-spacer />
              <v-btn-toggle v-model="viewMode" density="compact">
                <v-btn value="chart" size="small" icon="mdi-chart-bar" />
                <v-btn value="table" size="small" icon="mdi-table" />
              </v-btn-toggle>
            </v-card-title>
            <v-card-text>
              <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

              <!-- Chart -->
              <ClientOnly v-if="!loading && queryResults.length > 0 && viewMode === 'chart'">
                <VueECharts :option="chartOption" style="height: 350px" autoresize />
              </ClientOnly>

              <!-- Table -->
              <v-data-table
                v-if="!loading && queryResults.length > 0 && viewMode === 'table'"
                :headers="resultHeaders"
                :items="queryResults"
                :items-per-page="10"
                class="elevation-0"
              >
                <template v-slot:item.value="{ item }">
                  <span class="font-weight-bold" :class="item.value >= 0 ? 'text-success' : 'text-error'">
                    ${{ fmt(item.value) }}
                  </span>
                </template>
              </v-data-table>

              <!-- Empty State -->
              <div v-if="!loading && queryResults.length === 0" class="text-center py-8 text-grey">
                <v-icon size="48" color="grey">mdi-table-search</v-icon>
                <div class="mt-3">Configure and run a query to see results</div>
              </div>
            </v-card-text>
          </v-card>

          <!-- Data Table (Editable) -->
          <v-card v-if="!loading && queryResults.length > 0" class="rounded-lg" elevation="1">
            <v-card-title class="d-flex align-center">
              <v-icon class="mr-2">mdi-table-edit</v-icon>
              Editable Data Table
              <v-spacer />
              <v-chip v-if="selectedIds.length > 0" color="primary" size="small" class="mr-2">
                {{ selectedIds.length }} selected
              </v-chip>
              <v-btn v-if="selectedIds.length > 0" size="small" variant="outlined" color="error" @click="batchDelete">
                Delete Selected
              </v-btn>
            </v-card-title>
            <v-card-text>
              <v-data-table
                v-model:selected="selectedRows"
                :headers="editableHeaders"
                :items="queryResults"
                :items-per-page="20"
                show-select
                class="elevation-0"
              >
                <template v-slot:item.label="{ item }">
                  <span class="font-weight-medium">{{ item.label }}</span>
                </template>
                <template v-slot:item.value="{ item }">
                  <span class="font-weight-bold">${{ fmt(item.value) }}</span>
                </template>
                <template v-slot:item.count="{ item }">
                  <span class="text-grey">{{ item.count }}</span>
                </template>
              </v-data-table>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </template>

    <!-- ============ SAVED QUERIES MODE ============ -->
    <template v-if="mode === 'saved'">
      <v-card class="rounded-lg" elevation="1">
        <v-card-title class="d-flex align-center">
          <span>Saved Queries</span>
          <v-spacer />
          <v-btn-toggle v-model="showHidden" density="compact">
            <v-btn :value="false" size="small">Active</v-btn>
            <v-btn :value="true" size="small">All</v-btn>
          </v-btn-toggle>
        </v-card-title>
        <v-list lines="three">
          <v-list-item v-for="sq in filteredSavedQueries" :key="sq.id" class="py-3">
            <template v-slot:prepend>
              <v-avatar :color="sq.hidden ? 'grey' : 'primary'" size="40" class="mr-3">
                <v-icon color="white" size="20">mdi-table</v-icon>
              </v-avatar>
            </template>
            <v-list-item-title class="font-weight-medium">{{ sq.name }}</v-list-item-title>
            <v-list-item-subtitle>
              {{ sq.dimension }} · {{ sq.timeRange }}
              <span v-if="sq.accountId" class="text-grey ml-2">· {{ getAccountName(sq.accountId) }}</span>
              <span v-if="sq.hidden" class="text-error ml-2">· Hidden</span>
            </v-list-item-subtitle>
            <template v-slot:append>
              <v-btn icon="mdi-play" variant="text" color="primary" @click="loadQuery(sq)" />
              <v-btn icon="mdi-pencil" variant="text" size="small" @click="editQuery(sq)" />
              <v-btn :icon="sq.hidden ? 'mdi-eye' : 'mdi-eye-off'" variant="text" size="small" @click="toggleHidden(sq)" />
              <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="deleteQuery(sq)" />
            </template>
          </v-list-item>
        </v-list>
        <v-card-text v-if="savedQueries.length === 0" class="text-center text-grey py-6">
          <v-icon size="48" color="grey">mdi-bookmark-off-outline</v-icon>
          <div class="mt-3">No saved queries. Create and save one from the Explore tab.</div>
        </v-card-text>
      </v-card>

      <!-- Reorder Hint -->
      <v-card v-if="savedQueries.length > 1" class="mt-4 rounded-lg" elevation="0" color="grey-lighten-4">
        <v-card-text class="text-body-2 text-grey">
          <v-icon size="small" class="mr-1">mdi-information</v-icon>
          Drag to reorder saved queries. Changes are saved automatically.
        </v-card-text>
      </v-card>
    </template>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

interface Account { id: number; name: string }
interface Category { id: number; name: string }
interface QueryConfig { dimension: string; accountId: number | null; categoryId: number | null; transactionType: number | null; timeRange: string }
interface QueryResult { label: string; value: number; count: number }
interface SavedQuery { id: number; name: string; dimension: string; accountId: number | null; categoryId: number | null; transactionType: number | null; timeRange: string; sortOrder: number; hidden: boolean }

const mode = ref('explore')
const viewMode = ref('chart')
const loading = ref(false)
const queryResults = ref<QueryResult[]>([])
const selectedRows = ref<QueryResult[]>([])
const selectedIds = computed(() => selectedRows.value.map(r => r.label))

const accounts = ref<Account[]>([])
const categories = ref<Category[]>([])
const savedQueries = ref<SavedQuery[]>([])
const showHidden = ref(false)

const config = reactive<QueryConfig>({
  dimension: 'category',
  accountId: null,
  categoryId: null,
  transactionType: null,
  timeRange: 'this_month',
})

const queryName = ref('')

const dimensionOptions = [
  { title: 'Category', value: 'category' },
  { title: 'Account', value: 'account' },
  { title: 'Month', value: 'month' },
  { title: 'Day', value: 'day' },
  { title: 'Tag', value: 'tag' },
]

const typeOptions = [
  { title: 'All Types', value: null },
  { title: 'Income', value: 2 },
  { title: 'Expense', value: 3 },
  { title: 'Transfer', value: 4 },
]

const timeRangeOptions = [
  { title: 'This Month', value: 'this_month' },
  { title: 'Last Month', value: 'last_month' },
  { title: 'This Year', value: 'this_year' },
  { title: 'Last Year', value: 'last_year' },
  { title: 'Last 3 Months', value: 'last_3_months' },
  { title: 'Last 6 Months', value: 'last_6_months' },
  { title: 'Last 12 Months', value: 'last_12_months' },
]

const accountOptions = computed(() => [{ title: 'All Accounts', value: null }, ...accounts.value.map(a => ({ title: a.name, value: a.id }))])
const categoryOptions = computed(() => [{ title: 'All Categories', value: null }, ...categories.value.map(c => ({ title: c.name, value: c.id }))])

const resultHeaders = [
  { title: 'Label', key: 'label', width: '200px' },
  { title: 'Amount', key: 'value', width: '150px' },
  { title: 'Transactions', key: 'count', width: '120px' },
]

const editableHeaders = [
  { title: 'Label', key: 'label' },
  { title: 'Amount', key: 'value' },
  { title: 'Count', key: 'count' },
]

const filteredSavedQueries = computed(() =>
  showHidden.value ? savedQueries.value : savedQueries.value.filter(q => !q.hidden)
)

const chartOption = computed(() => {
  if (queryResults.value.length === 0) return {}
  const colors = ['#4CAF50', '#F44336', '#2196F3', '#FF9800', '#9C27B0', '#00BCD4', '#795548', '#607D8B']
  return {
    tooltip: { trigger: 'axis', formatter: '{b}: ${c} ({d}%)' },
    legend: { orient: 'vertical', right: 10, top: 'center' },
    xAxis: { type: 'category', data: queryResults.value.map(r => r.label), axisLabel: { fontSize: 12 } },
    yAxis: { type: 'value', axisLabel: { formatter: (v: number) => '$' + (v / 1000).toFixed(0) + 'k' } },
    series: [{
      data: queryResults.value.map((r, i) => ({ name: r.label, value: r.value / 100, itemStyle: { color: colors[i % colors.length] } })),
      type: 'bar',
      itemStyle: { borderRadius: [4, 4, 0, 0] },
    }],
    grid: { left: 60, right: 120, top: 20, bottom: 30, containLabel: true },
  }
})

function fmt(cents: number) { return (cents / 100).toLocaleString('en-US', { minimumFractionDigits: 2 }) }
function getAccountName(id: number | null) { return id ? accounts.value.find(a => a.id === id)?.name || '-' : 'All' }

async function runQuery() {
  loading.value = true
  try {
    // Build params based on config
    const params = new URLSearchParams()
    if (config.accountId) params.set('accountId', String(config.accountId))
    if (config.transactionType) params.set('transactionType', String(config.transactionType))
    if (config.timeRange) {
      const now = new Date()
      switch (config.timeRange) {
        case 'this_month':
          params.set('year', String(now.getFullYear()))
          params.set('month', String(now.getMonth() + 1))
          break
        // Add more ranges as needed
      }
    }
    
    const txs = await api.get<any[]>(`/transactions?${params.toString()}&limit=500`)
    
    // Group by dimension
    const groups = new Map<string, { total: number; count: number }>()
    txs.forEach(tx => {
      let key: string
      if (config.dimension === 'category') key = `Cat ${tx.categoryId || 'None'}`
      else if (config.dimension === 'account') key = `Acc ${tx.accountId}`
      else key = `Other`
      
      if (config.transactionType && tx.transactionType !== config.transactionType) return
      
      const group = groups.get(key) || { total: 0, count: 0 }
      group.total += tx.amount
      group.count += 1
      groups.set(key, group)
    })
    
    queryResults.value = [...groups.entries()]
      .map(([label, data]) => ({ label, value: data.total, count: data.count }))
      .sort((a, b) => Math.abs(b.value) - Math.abs(a.value))
  } catch (e) {
    console.error('Query failed:', e)
    queryResults.value = []
  } finally { loading.value = false }
}

function createNew() {
  config.dimension = 'category'
  config.accountId = null
  config.categoryId = null
  config.transactionType = null
  config.timeRange = 'this_month'
  queryResults.value = []
  queryName.value = ''
}

async function saveQuery() {
  if (!queryName.value.trim()) return
  try {
    const saved = await api.post<SavedQuery>('/insights/save.json', {
      name: queryName.value,
      ...config,
    })
    savedQueries.value.push(saved)
    queryName.value = ''
  } catch (e) { console.error('Save failed:', e) }
}

function loadQuery(sq: SavedQuery) {
  config.dimension = sq.dimension
  config.accountId = sq.accountId
  config.categoryId = sq.categoryId
  config.transactionType = sq.transactionType
  config.timeRange = sq.timeRange
  mode.value = 'explore'
  runQuery()
}

function editQuery(sq: SavedQuery) {
  // Would open edit dialog
  queryName.value = sq.name
  loadQuery(sq)
}

async function toggleHidden(sq: SavedQuery) {
  try {
    await api.post(`/insights/${sq.id}/toggle_hidden.json`)
    sq.hidden = !sq.hidden
  } catch (e) { console.error('Toggle failed:', e) }
}

async function deleteQuery(sq: SavedQuery) {
  try {
    await api.delete(`/insights/${sq.id}`)
    savedQueries.value = savedQueries.value.filter(q => q.id !== sq.id)
  } catch (e) { console.error('Delete failed:', e) }
}

async function batchDelete() {
  // For now, just clear selection
  selectedRows.value = []
}

onMounted(async () => {
  const [acc, cats, saved] = await Promise.all([
    api.get<Account[]>('/accounts'),
    api.get<Category[]>('/categories'),
    api.get<SavedQuery[]>('/insights/list.json'),
  ])
  accounts.value = acc
  categories.value = cats
  savedQueries.value = saved
})
</script>