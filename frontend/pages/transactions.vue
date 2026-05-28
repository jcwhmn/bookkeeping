<!-- pages/transactions.vue — Transactions page with Edit, Delete, Date Picker, Transfer, Month Navigation -->
<template>
  <div style="max-width: 1200px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Transactions</h1>
      <v-spacer />
      <!-- Month Navigation -->
      <div class="d-flex align-center mr-4">
        <v-btn icon="mdi-chevron-left" variant="text" size="small" @click="prevMonth" :disabled="!canGoPrev" />
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
        <v-btn icon="mdi-chevron-right" variant="text" size="small" @click="nextMonth" :disabled="!canGoNext" />
      </div>
      <v-btn color="primary" prepend-icon="mdi-plus" rounded="lg" @click="openCreate()">Add</v-btn>
      <v-btn variant="outlined" prepend-icon="mdi-robot" class="ml-2" @click="showLLMDialog = true" title="AI Receipt Recognition">
        AI Scan
      </v-btn>
      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn v-bind="props" variant="outlined" prepend-icon="mdi-export" class="ml-2">
            Export
            <v-icon end size="small">mdi-chevron-down</v-icon>
          </v-btn>
        </template>
        <v-list density="compact">
          <v-list-item prepend-icon="mdi-file-delimited" title="Export CSV" @click="exportData('csv')" />
          <v-list-item prepend-icon="mdi-file-delimited" title="Export TSV" @click="exportData('tsv')" />
        </v-list>
      </v-menu>
      <v-btn variant="outlined" prepend-icon="mdi-import" class="ml-2" @click="importDialog = true">
        Import
      </v-btn>
    </div>

    <!-- Batch Actions Bar -->
    <v-card v-if="selectedIds.length > 0" class="mb-4 pa-3 rounded-lg" elevation="2" color="primary-lighten-5">
      <div class="d-flex align-center">
        <span class="text-body-2 font-weight-medium mr-3">{{ selectedIds.length }} selected</span>
        <v-btn size="small" variant="outlined" class="mr-2" @click="showBatchDialog('category')">
          <v-icon start size="16">mdi-tag-outline</v-icon> Change Category
        </v-btn>
        <v-btn size="small" variant="outlined" class="mr-2" @click="showBatchDialog('account')">
          <v-icon start size="16">mdi-bank</v-icon> Change Account
        </v-btn>
        <v-btn size="small" variant="outlined" class="mr-2" @click="showBatchDialog('tag')">
          <v-icon start size="16">mdi-tag-multiple</v-icon> Edit Tags
        </v-btn>
        <v-btn size="small" variant="outlined" color="error" @click="confirmBatchDelete">
          <v-icon start size="16">mdi-delete</v-icon> Delete
        </v-btn>
        <v-spacer />
        <v-btn size="small" variant="text" @click="selectedIds = []">Clear</v-btn>
      </div>
    </v-card>

    <!-- Filter Bar -->
    <v-card class="mb-4 pa-3 rounded-lg" elevation="1">
      <v-row dense align="center">
        <v-col cols="12" sm="3">
          <v-btn-toggle v-model="filter.type" mandatory divided color="primary" density="compact" class="w-100">
            <v-btn value="all" size="small" class="text-none flex-grow-1">All</v-btn>
            <v-btn value="2" size="small" class="text-none flex-grow-1">Income</v-btn>
            <v-btn value="3" size="small" class="text-none flex-grow-1">Expense</v-btn>
            <v-btn value="4" size="small" class="text-none flex-grow-1">Transfer</v-btn>
          </v-btn-toggle>
        </v-col>
        <v-col cols="12" sm="3">
          <v-select v-model="filter.accountId" :items="accountOptions" label="Account" variant="outlined" density="compact" hide-details clearable />
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field v-model="filter.search" label="Search" variant="outlined" density="compact" hide-details prepend-inner-icon="mdi-magnify" clearable />
        </v-col>
      </v-row>
    </v-card>

    <!-- Summary -->
    <v-row class="mb-4" v-if="filteredTransactions.length">
      <v-col cols="6" sm="3">
        <v-card class="pa-3 rounded-lg text-center" elevation="1">
          <div class="text-caption text-grey">Income</div>
          <div class="text-h6 font-weight-bold text-success">${{ fmt(totalIncome) }}</div>
        </v-card>
      </v-col>
      <v-col cols="6" sm="3">
        <v-card class="pa-3 rounded-lg text-center" elevation="1">
          <div class="text-caption text-grey">Expense</div>
          <div class="text-h6 font-weight-bold text-error">${{ fmt(totalExpense) }}</div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Loading -->
    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <!-- Grouped Transactions -->
    <template v-if="!loading">
      <template v-for="(group, date) in groupedTransactions" :key="date">
        <div class="text-subtitle-2 text-grey-darken-1 font-weight-bold mb-2 mt-4 sticky-header">
          {{ date }}
        </div>
        <v-card class="mb-2 rounded-lg" elevation="1" v-for="tx in group" :key="tx.id">
          <v-list-item 
            class="hover-item tx-type-{{ tx.transactionType }}" 
            @click="openEdit(tx)"
            style="cursor: pointer;"
          >
            <template v-slot:prepend>
              <v-checkbox
                :model-value="selectedIds.includes(tx.id)"
                @update:model-value="toggleSelect(tx.id)"
                @click.stop
                hide-details
                class="mr-2"
              />
              <v-avatar :color="typeColor(tx.transactionType)" size="40" class="mr-3">
                <v-icon color="white" size="20">{{ typeIcon(tx.transactionType) }}</v-icon>
              </v-avatar>
            </template>
            <v-list-item-title class="font-weight-medium">
              {{ tx.description || '-' }}
              <v-chip v-if="tx.transactionType === 3" size="x-small" :color="categoryColor(tx.categoryId)" class="ml-2" label>
                {{ categoryName(tx.categoryId) }}
              </v-chip>
              <v-chip v-if="tx.transactionType === 2" size="x-small" color="success" variant="tonal" class="ml-2" label>
                {{ categoryName(tx.categoryId) || 'Income' }}
              </v-chip>
              <v-chip v-if="tx.transactionType === 4" size="x-small" color="info" variant="tonal" class="ml-2" label>
                Transfer
              </v-chip>
              <v-chip v-if="tx.transactionType === 5" size="x-small" color="info" variant="outlined" class="ml-2" label>
                Transfer In
              </v-chip>
            </v-list-item-title>
            <v-list-item-subtitle>
              {{ accountName(tx.accountId) }} · {{ formatDate(tx.transactionTime) }}
            </v-list-item-subtitle>
            <template v-slot:append>
              <span class="text-body-1 font-weight-bold" :class="amountClass(tx.transactionType)">
                {{ amountPrefix(tx.transactionType) }}${{ fmt(tx.amount) }}
              </span>
              <v-btn icon="mdi-pencil" variant="text" size="small" color="primary" class="ml-2 edit-btn" @click.stop="openEdit(tx)" />
              <v-btn icon="mdi-delete" variant="text" size="small" color="error" class="delete-btn" @click.stop="confirmDelete(tx)" />
            </template>
          </v-list-item>
        </v-card>
      </template>

      <v-alert v-if="filteredTransactions.length === 0" type="info" variant="tonal" class="mt-4 rounded-lg">
        No transactions found. <v-btn variant="text" size="small" @click="openCreate()">Add one</v-btn>
      </v-alert>
    </template>

    <!-- Create/Edit Dialog -->
    <v-dialog v-model="dialog" max-width="520">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          {{ editingTx ? 'Edit Transaction' : 'Add Transaction' }}
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="dialog = false" />
        </v-card-title>
        <v-card-text>
          <!-- Type Selector -->
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
          <v-text-field v-model="amountStr" label="Amount" variant="outlined" density="comfortable" prefix="$" class="mb-3 amount-input" />

          <!-- From Account (always shown) -->
          <v-select v-model="form.accountId" :items="accountOptions" label="From Account" variant="outlined" density="comfortable" class="mb-3" :rules="[required]" />

          <!-- To Account (only for Transfer) -->
          <v-select v-if="form.transactionType === 4" v-model="form.destinationAccountId" 
            :items="toAccountOptions" label="To Account" variant="outlined" density="comfortable" class="mb-3" 
            :rules="[required]" />

          <!-- Category (not for Transfer) -->
          <v-select v-if="form.transactionType !== 4" v-model="form.categoryId" 
            :items="categoryOptions" label="Category" variant="outlined" density="comfortable" class="mb-3" />

          <!-- Date & Time Row -->
          <v-row dense class="mb-3">
            <v-col cols="7">
              <v-text-field v-model="form.date" label="Date" type="date" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="5">
              <v-text-field v-model="form.time" label="Time" type="time" variant="outlined" density="comfortable" />
            </v-col>
          </v-row>

          <!-- Notes -->
          <v-text-field v-model="form.description" label="Notes" variant="outlined" density="comfortable" :rules="[required]" />

          <!-- Pictures -->
          <div class="mt-4">
            <div class="d-flex align-center mb-2">
              <v-icon start size="small" color="grey">mdi-image</v-icon>
              <span class="text-caption text-grey">Receipts / Pictures</span>
              <v-spacer />
              <v-btn size="x-small" variant="text" color="primary" @click="triggerPictureUpload">
                <v-icon start size="small">mdi-plus</v-icon> Add Picture
              </v-btn>
              <input ref="pictureInput" type="file" accept="image/*" class="d-none" @change="onPictureSelected" />
            </div>
            <div v-if="pictures.length > 0" class="d-flex flex-wrap gap-2">
              <div v-for="pic in pictures" :key="pic.id" class="picture-thumb" style="position: relative;">
                <v-img :src="'/api/v1/transaction/pictures/' + pic.id + '/file'" width="80" height="80" cover class="rounded" />
                <v-btn icon="mdi-close" size="x-small" color="error" variant="flat" style="position: absolute; top: -8px; right: -8px;" @click="removePicture(pic.id)" />
              </div>
            </div>
            <div v-else class="text-caption text-grey-light text-center pa-3 rounded" style="border: 1px dashed grey;">
              No pictures attached
            </div>
          </div>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-btn v-if="editingTx" variant="text" color="error" @click="confirmDelete(editingTx)">Delete</v-btn>
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="lg" @click="save" :loading="saving">{{ editingTx ? 'Save Changes' : 'Save Transaction' }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center text-error">
          <v-icon color="error" class="mr-2">mdi-alert-circle</v-icon>
          Delete Transaction?
        </v-card-title>
        <v-card-text>
          <v-alert type="warning" variant="tonal" class="mb-3 rounded-lg">
            <div class="mb-2">Are you sure you want to delete this transaction?</div>
            <div class="text-body-2">
              <strong>{{ deletingTx?.description || '-' }}</strong><br/>
              {{ deletingTx ? amountPrefix(deletingTx.transactionType) : '' }}${{ deletingTx ? fmt(deletingTx.amount) : '0' }} on {{ deletingTx ? formatDate(deletingTx.transactionTime) : '' }}
            </div>
            <div class="mt-2 text-body-2 text-grey">
              ⚠️ This will revert {{ deletingTx ? amountPrefix(deletingTx.transactionType) : '' }}${{ deletingTx ? fmt(deletingTx.amount) : '0' }} to {{ deletingTx ? accountName(deletingTx.accountId) : '' }} account.
            </div>
          </v-alert>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">Keep Transaction</v-btn>
          <v-btn color="error" rounded="lg" @click="doDelete" :loading="deleting">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Batch Operation Dialog -->
    <v-dialog v-model="batchDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          {{ batchAction === 'category' ? 'Change Category' : batchAction === 'account' ? 'Change Account' : 'Edit Tags' }}
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="batchDialog = false" />
        </v-card-title>
        <v-card-text>
          <v-select v-if="batchAction === 'category'" v-model="batchCategoryId" :items="categoryOptions" label="New Category" variant="outlined" density="comfortable" class="mb-3" />
          <v-select v-if="batchAction === 'account'" v-model="batchAccountId" :items="accountOptions" label="New Account" variant="outlined" density="comfortable" class="mb-3" />
          <div v-if="batchAction === 'tag'" class="text-body-2 text-grey">Tag editing for selected transactions coming soon.</div>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="batchDialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="lg" @click="executeBatch" :loading="executingBatch">Apply to {{ selectedIds.length }} transactions</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Batch Delete Confirmation -->
    <v-dialog v-model="batchDeleteDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="text-error">
          <v-icon color="error" class="mr-2">mdi-alert-circle</v-icon>
          Delete {{ selectedIds.length }} Transactions?
        </v-card-title>
        <v-card-text>
          <v-alert type="warning" variant="tonal" class="rounded-lg">
            This will permanently delete {{ selectedIds.length }} transactions. This action cannot be undone.
          </v-alert>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="batchDeleteDialog = false">Cancel</v-btn>
          <v-btn color="error" rounded="lg" @click="doBatchDelete" :loading="deleting">Delete All</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Import Dialog -->
    <v-dialog v-model="importDialog" max-width="500">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          Import Transactions
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="importDialog = false" />
        </v-card-title>
        <v-card-text>
          <v-alert type="info" variant="tonal" class="mb-4 rounded-lg">
            Supported formats: CSV, TSV. Upload a file to preview and import transactions.
          </v-alert>
          <v-select v-model="importFormat" :items="[{title:'CSV', value:'csv'}, {title:'TSV', value:'tsv'}]" label="Format" variant="outlined" density="comfortable" class="mb-3" />
          <v-file-input v-model="importFile" label="Select File" variant="outlined" density="comfortable" accept=".csv,.tsv" />
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="importDialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="lg" @click="doImport" :loading="importing">Import</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- LLM Receipt Scan Dialog -->
    <v-dialog v-model="showLLMDialog" max-width="500">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          <v-icon color="primary" class="mr-2">mdi-robot</v-icon>
          AI Receipt Recognition
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="showLLMDialog = false" />
        </v-card-title>
        <v-card-text>
          <v-alert type="info" variant="tonal" class="mb-4 rounded-lg">
            Upload a receipt image and our AI will extract transaction details automatically.
          </v-alert>
          <v-file-input
            v-model="llmFile"
            label="Receipt Image"
            variant="outlined"
            density="comfortable"
            accept="image/*"
            prepend-icon="mdi-camera"
            class="mb-3"
          />
          <div v-if="llmResult" class="mt-4">
            <v-divider class="mb-4" />
            <div class="text-subtitle-2 font-weight-bold mb-2">Recognized Data:</div>
            <div class="text-body-2 mb-1"><strong>Type:</strong> {{ llmResult.transactionType === 2 ? 'Income' : 'Expense' }}</div>
            <div class="text-body-2 mb-1"><strong>Amount:</strong> ${{ llmResult.amountStr || 'N/A' }}</div>
            <div class="text-body-2 mb-1"><strong>Description:</strong> {{ llmResult.description || 'N/A' }}</div>
            <div class="text-body-2 mb-1"><strong>Date:</strong> {{ llmResult.dateStr || 'N/A' }}</div>
          </div>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="showLLMDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="scanReceipt" :loading="llmLoading" :disabled="!llmFile">
            <v-icon start>mdi-robot</v-icon> Scan Receipt
          </v-btn>
          <v-btn v-if="llmResult" color="success" @click="applyLLMResult">
            Apply to Form
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

interface Tx { id: number; transactionType: number; accountId: number; categoryId: number; amount: number; description: string; transactionTime: number; relatedId?: number }
interface Account { id: number; name: string }
interface Category { id: number; name: string; categoryType: string }

const transactions = shallowRef<Tx[]>([])
const accounts = shallowRef<Account[]>([])
const categories = shallowRef<Category[]>([])
const loading = ref(true)
const dialog = ref(false)
const saving = ref(false)
const editingTx = ref<Tx | null>(null)
const amountStr = ref('')
const required = (v: any) => !!v || 'Required'

// Batch selection
const selectedIds = ref<number[]>([])

// Batch dialog
const batchDialog = ref(false)
const batchAction = ref('')
const batchCategoryId = ref<number | null>(null)
const batchAccountId = ref<number | null>(null)
const batchDeleteDialog = ref(false)
const executingBatch = ref(false)

// Import dialog
const importDialog = ref(false)
const importFile = ref<File | null>(null)
const importFormat = ref('csv')
const importing = ref(false)

// LLM dialog
const showLLMDialog = ref(false)
const llmFile = ref<File | null>(null)
const llmResult = ref<any>(null)
const llmLoading = ref(false)

// Picture upload
const pictureInput = ref<HTMLInputElement | null>(null)
const pictures = ref<{ id: number; fileName: string }[]>([])
const uploadingPic = ref(false)

function triggerPictureUpload() {
  pictureInput.value?.click()
}

async function onPictureSelected(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file || !editingTx.value) return
  uploadingPic.value = true
  try {
    const fd = new FormData()
    fd.append('transaction_id', editingTx.value.id.toString())
    fd.append('file', file)
    const result = await api.post('/transaction/pictures/upload.json', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    pictures.value.push({ id: result.id, fileName: result.fileName })
  } catch (e) { console.error('Upload failed:', e) }
  finally { uploadingPic.value = false }
}

async function removePicture(pictureId: number) {
  try {
    await api.post('/transaction/pictures/remove.json', null, {
      params: { picture_id: pictureId }
    })
    pictures.value = pictures.value.filter(p => p.id !== pictureId)
  } catch (e) { console.error('Remove failed:', e) }
}

async function loadPictures(transactionId: number) {
  try {
    const pics = await api.get<{ id: number; fileName: string }[]>('/transaction/pictures/list.json', {
      params: { transaction_id: transactionId }
    })
    pictures.value = pics
  } catch (e) { pictures.value = [] }
}

function exportData(format: string) {
  let url = `/api/v1/data/export.${format}`
  if (selectedYear.value && selectedMonth.value) {
    url += `?year=${selectedYear.value}&month=${selectedMonth.value}`
  }
  window.open(url, '_blank')
}

async function doImport() {
  if (!importFile.value) return
  importing.value = true
  try {
    const fd = new FormData()
    fd.append('file', importFile.value)
    fd.append('format', importFormat.value)
    const resp = await api.post('/transactions/import.json', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    importDialog.value = false
    importFile.value = null
    console.log('Import job:', resp)
    alert(`Import initiated. Job ID: ${resp.jobId || 'pending'}`)
  } catch (e) { console.error('Import failed:', e) }
  finally { importing.value = false }
}

async function scanReceipt() {
  if (!llmFile.value) return
  llmLoading.value = true
  try {
    const fd = new FormData()
    fd.append('picture', llmFile.value)
    const resp = await api.post('/llm/transactions/recognize_receipt_image.json', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (resp.status === 'not_configured') {
      alert('LLM provider not configured. Please set up your LLM API key in application settings.')
    } else {
      llmResult.value = resp
    }
  } catch (e) { console.error('LLM scan failed:', e) }
  finally { llmLoading.value = false }
}

function applyLLMResult() {
  if (!llmResult.value) return
  // Pre-fill form with recognized data
  openCreate()
  if (llmResult.value.transactionType) form.transactionType = llmResult.value.transactionType
  if (llmResult.value.amountStr) amountStr.value = llmResult.value.amountStr
  if (llmResult.value.description) form.description = llmResult.value.description
  if (llmResult.value.dateStr) form.date = llmResult.value.dateStr
  showLLMDialog.value = false
}

// Delete dialog
const deleteDialog = ref(false)
const deletingTx = ref<Tx | null>(null)
const deleting = ref(false)

// Form state
const form = reactive({
  transactionType: 2 as number,
  accountId: 0 as number,
  destinationAccountId: null as number | null,
  categoryId: 0 as number,
  description: '',
  date: new Date().toISOString().split('T')[0],
  time: new Date().toTimeString().slice(0, 5),
})

// Month Navigation
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

const canGoPrev = computed(() => {
  if (selectedYear.value < currentYear) return true
  return selectedMonth.value > 1
})

const canGoNext = computed(() => {
  if (selectedYear.value > 2020) return true
  return selectedYear.value < currentYear || selectedMonth.value < currentMonth
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

function selectMonth(m: number) {
  selectedMonth.value = m
  fetchData()
}

function monthName(m: number) {
  return new Date(2000, m - 1).toLocaleDateString('en-US', { month: 'short' })
}

const filter = reactive({ type: 'all', accountId: null as number | null, search: '' })

const accountOptions = computed(() => accounts.value.map(a => ({ title: a.name, value: a.id })))
const toAccountOptions = computed(() => accounts.value
  .filter(a => a.id !== form.accountId)
  .map(a => ({ title: a.name, value: a.id })))
const categoryOptions = computed(() => categories.value
  .filter(c => form.transactionType === 2 ? c.categoryType === 'INCOME' : c.categoryType === 'EXPENSE')
  .map(c => ({ title: c.name, value: c.id })))

const filteredTransactions = computed(() => {
  let txs = transactions.value
  if (filter.type !== 'all') txs = txs.filter(t => t.transactionType === Number(filter.type))
  if (filter.accountId) txs = txs.filter(t => t.accountId === filter.accountId)
  if (filter.search) {
    const q = filter.search.toLowerCase()
    txs = txs.filter(t => t.description?.toLowerCase().includes(q) || accountName(t.accountId).toLowerCase().includes(q))
  }
  return txs
})

const groupedTransactions = computed(() => {
  const groups: Record<string, Tx[]> = {}
  const now = new Date()
  filteredTransactions.value.forEach(tx => {
    const d = new Date(tx.transactionTime * 1000)
    let key: string
    if (isSameDay(d, now)) key = 'Today'
    else if (isSameDay(d, new Date(now.getTime() - 86400000))) key = 'Yesterday'
    else key = d.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' })
    if (!groups[key]) groups[key] = []
    groups[key].push(tx)
  })
  return groups
})

function isSameDay(a: Date, b: Date) { return a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate() }

const totalIncome = computed(() => filteredTransactions.value.filter(t => t.transactionType === 2).reduce((s, t) => s + t.amount, 0))
const totalExpense = computed(() => filteredTransactions.value.filter(t => t.transactionType === 3).reduce((s, t) => s + t.amount, 0))

function fmt(c: number) { return (c / 100).toLocaleString('en-US', { minimumFractionDigits: 2 }) }
function formatDate(ts: number) { return new Date(ts * 1000).toLocaleDateString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) }
function formatTime(ts: number) { return new Date(ts * 1000).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }) }
function accountName(id: number) { return accounts.value.find(a => a.id === id)?.name || '-' }
function categoryName(id: number) { return categories.value.find(c => c.id === id)?.name || '-' }
function categoryColor(id: number) { const colors = ['blue', 'purple', 'teal', 'orange', 'pink', 'cyan', 'deep-purple', 'amber']; return colors[id % colors.length] }

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

function openCreate() {
  editingTx.value = null
  form.transactionType = 2
  form.accountId = accounts.value[0]?.id || 0
  form.destinationAccountId = null
  form.categoryId = 0
  form.description = ''
  form.date = new Date().toISOString().split('T')[0]
  form.time = new Date().toTimeString().slice(0, 5)
  amountStr.value = ''
  dialog.value = true
}

function openEdit(tx: Tx) {
  editingTx.value = tx
  form.transactionType = tx.transactionType === 5 ? 4 : tx.transactionType  // Show TRANSFER_IN as Transfer type
  form.accountId = tx.accountId
  form.destinationAccountId = null
  form.categoryId = tx.categoryId || 0
  form.description = tx.description || ''
  
  // Parse date/time from transactionTime
  const d = new Date(tx.transactionTime * 1000)
  form.date = d.toISOString().split('T')[0]
  form.time = d.toTimeString().slice(0, 5)
  
  amountStr.value = (tx.amount / 100).toString()
  pictures.value = []
  loadPictures(tx.id)
  dialog.value = true
}

function confirmDelete(tx: Tx) {
  deletingTx.value = tx
  deleteDialog.value = true
}

async function doDelete() {
  if (!deletingTx.value) return
  deleting.value = true
  try {
    await api.delete(`/transactions/${deletingTx.value.id}`)
    deleteDialog.value = false
    deletingTx.value = null
    await fetchData()
  } finally {
    deleting.value = false
  }
}

async function fetchData() {
  loading.value = true
  try {
    // Build query params
    let url = '/transactions?limit=100'
    if (selectedYear.value && selectedMonth.value) {
      url += `&year=${selectedYear.value}&month=${selectedMonth.value}`
    }
    if (filter.type !== 'all') url += `&transactionType=${filter.type}`
    if (filter.accountId) url += `&accountId=${filter.accountId}`
    if (filter.search) url += `&search=${encodeURIComponent(filter.search)}`
    
    const [txs, acc, cats] = await Promise.all([
      api.get<Tx[]>(url),
      api.get<Account[]>('/accounts'),
      api.get<Category[]>('/categories'),
    ])
    transactions.value = txs
    accounts.value = acc
    categories.value = cats
  } finally { loading.value = false }
}

function dateTimeToUnix(date: string, time: string): number {
  const d = new Date(`${date}T${time}:00`)
  return Math.floor(d.getTime() / 1000)
}

async function save() {
  saving.value = true
  try {
    const payload = {
      transactionType: form.transactionType,
      accountId: form.accountId,
      categoryId: form.transactionType !== 4 ? (form.categoryId || null) : null,
      destinationAccountId: form.transactionType === 4 ? form.destinationAccountId : null,
      amount: Math.round(parseFloat(amountStr.value || '0') * 100),
      description: form.description,
      transactionTime: dateTimeToUnix(form.date, form.time),
    }

    if (editingTx.value) {
      await api.put(`/transactions/${editingTx.value.id}`, payload)
    } else {
      await api.post('/transactions', payload)
    }
    dialog.value = false
    await fetchData()
  } finally { saving.value = false }
}

// Batch operations
function toggleSelect(id: number) {
  const idx = selectedIds.value.indexOf(id)
  if (idx >= 0) selectedIds.value.splice(idx, 1)
  else selectedIds.value.push(id)
}

function showBatchDialog(action: string) {
  batchAction.value = action
  batchCategoryId.value = null
  batchAccountId.value = null
  batchDialog.value = true
}

async function executeBatch() {
  executingBatch.value = true
  try {
    if (batchAction.value === 'category' && batchCategoryId.value) {
      await api.post('/transactions/batch_update/category.json', { transactionIds: selectedIds.value, categoryId: batchCategoryId.value })
    } else if (batchAction.value === 'account' && batchAccountId.value) {
      await api.post('/transactions/batch_update/account.json', { transactionIds: selectedIds.value, accountId: batchAccountId.value })
    }
    batchDialog.value = false
    selectedIds.value = []
    await fetchData()
  } catch (e) { console.error('Batch update failed:', e) }
  finally { executingBatch.value = false }
}

function confirmBatchDelete() {
  batchDeleteDialog.value = true
}

async function doBatchDelete() {
  deleting.value = true
  try {
    await api.post('/transactions/batch_delete.json', { transactionIds: selectedIds.value })
    batchDeleteDialog.value = false
    selectedIds.value = []
    await fetchData()
  } catch (e) { console.error('Batch delete failed:', e) }
  finally { deleting.value = false }
}

onMounted(fetchData)
</script>

<style scoped>
.sticky-header { background: #FAFAFA; padding: 8px 12px; border-radius: 8px; }
.hover-item { border-left: 3px solid transparent; transition: border-color 0.15s; }
.tx-type-2:hover { border-left-color: #4CAF50; }
.tx-type-3:hover { border-left-color: #F44336; }
.tx-type-4:hover { border-left-color: #1976D2; }
.tx-type-5:hover { border-left-color: #1976D2; }
.edit-btn, .delete-btn { opacity: 0; transition: opacity 0.15s; }
:hover .edit-btn, :hover .delete-btn { opacity: 1; }
.amount-input :deep(input) { font-size: 24px !important; font-weight: 600 !important; }
.month-btn { text-transform: none !important; letter-spacing: normal !important; }
</style>