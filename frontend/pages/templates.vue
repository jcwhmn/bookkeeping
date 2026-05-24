<!-- pages/templates.vue — Transaction Template Management -->
<template>
  <div style="max-width: 1000px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Templates</h1>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" rounded="lg" @click="openCreate()">Add Template</v-btn>
    </div>

    <!-- Loading -->
    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <!-- Templates Table -->
    <v-card v-if="!loading" class="rounded-lg" elevation="1">
      <v-data-table
        :headers="headers"
        :items="templates"
        :items-per-page="20"
        class="elevation-0"
      >
        <template v-slot:item.name="{ item }">
          <div class="d-flex align-center py-2">
            <v-avatar :color="typeColor(item.transactionType)" size="32" class="mr-3">
              <v-icon size="16" color="white">{{ typeIcon(item.transactionType) }}</v-icon>
            </v-avatar>
            <span class="font-weight-medium">{{ item.name }}</span>
          </div>
        </template>

        <template v-slot:item.transactionType="{ item }">
          <v-chip size="small" :color="typeColor(item.transactionType)" variant="tonal" label>
            {{ typeName(item.transactionType) }}
          </v-chip>
        </template>

        <template v-slot:item.amount="{ item }">
          <span class="font-weight-bold" :class="amountClass(item.transactionType)">
            {{ amountPrefix(item.transactionType) }}${{ fmt(item.amount) }}
          </span>
        </template>

        <template v-slot:item.categoryName="{ item }">
          <span class="text-grey-darken-1">{{ item.categoryName || '-' }}</span>
        </template>

        <template v-slot:item.accountName="{ item }">
          <span class="text-grey-darken-1">{{ item.accountName || '-' }}</span>
        </template>

        <template v-slot:item.actions="{ item }">
          <v-btn icon="mdi-pencil" variant="text" size="small" color="primary" @click="openEdit(item)" />
          <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="confirmDelete(item)" />
        </template>
      </v-data-table>

      <v-alert v-if="templates.length === 0" type="info" variant="tonal" class="ma-4 rounded-lg">
        No templates yet. <v-btn variant="text" size="small" @click="openCreate()">Create one</v-btn>
      </v-alert>
    </v-card>

    <!-- Create/Edit Dialog -->
    <v-dialog v-model="dialog" max-width="520">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          {{ editingTemplate ? 'Edit Template' : 'Add Template' }}
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="dialog = false" />
        </v-card-title>
        <v-card-text>
          <!-- Template Name -->
          <v-text-field v-model="form.name" label="Template Name" variant="outlined" density="comfortable" class="mb-3" :rules="[required]" />

          <!-- Type Selector -->
          <div class="mb-4">
            <div class="text-caption text-grey mb-2">Transaction Type</div>
            <v-btn-toggle v-model="form.transactionType" mandatory divided color="primary" density="compact" class="w-100">
              <v-btn :value="2" size="small" class="text-none flex-grow-1">
                <v-icon start size="16">mdi-arrow-down-bold</v-icon> Income
              </v-btn>
              <v-btn :value="3" size="small" class="text-none flex-grow-1">
                <v-icon start size="16">mdi-arrow-up-bold</v-icon> Expense
              </v-btn>
              <v-btn :value="4" size="small" class="text-none flex-grow-1">
                <v-icon start size="16">mdi-swap-horizontal</v-icon> Transfer
              </v-btn>
            </v-btn-toggle>
          </div>

          <!-- Amount -->
          <v-text-field v-model="amountStr" label="Amount" variant="outlined" density="comfortable" prefix="$" class="mb-3" />

          <!-- From Account -->
          <v-select v-model="form.accountId" :items="accountOptions" label="From Account" variant="outlined" density="comfortable" class="mb-3" :rules="[required]" />

          <!-- To Account (Transfer only) -->
          <v-select v-if="form.transactionType === 4" v-model="form.destinationAccountId" :items="accountOptions" label="To Account" variant="outlined" density="comfortable" class="mb-3" />

          <!-- Category (not for Transfer) -->
          <v-select v-if="form.transactionType !== 4" v-model="form.categoryId" :items="categoryOptions" label="Category" variant="outlined" density="comfortable" class="mb-3" />

          <!-- Description -->
          <v-text-field v-model="form.description" label="Description / Notes" variant="outlined" density="comfortable" />
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="lg" @click="save" :loading="saving">{{ editingTemplate ? 'Save' : 'Create' }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="text-error">
          <v-icon color="error" class="mr-2">mdi-alert-circle</v-icon>
          Delete Template?
        </v-card-title>
        <v-card-text>
          <v-alert type="warning" variant="tonal" class="rounded-lg">
            Delete <strong>{{ deletingTemplate?.name }}</strong>?
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

interface Account { id: number; name: string }
interface Category { id: number; name: string; categoryType: string }
interface Template {
  id: number; name: string; transactionType: number; accountId: number;
  categoryId: number | null; destinationAccountId: number | null;
  amount: number; description: string;
  categoryName?: string; accountName?: string;
}

const templates = shallowRef<Template[]>([])
const accounts = shallowRef<Account[]>([])
const categories = shallowRef<Category[]>([])
const loading = ref(true)
const dialog = ref(false)
const saving = ref(false)
const editingTemplate = ref<Template | null>(null)
const amountStr = ref('')

const deleteDialog = ref(false)
const deletingTemplate = ref<Template | null>(null)
const deleting = ref(false)

const required = (v: any) => !!v || 'Required'

const headers = [
  { title: 'Name', key: 'name', sortable: true },
  { title: 'Type', key: 'transactionType', sortable: true, width: '120px' },
  { title: 'Amount', key: 'amount', sortable: true, width: '120px' },
  { title: 'Category', key: 'categoryName', sortable: false, width: '140px' },
  { title: 'Account', key: 'accountName', sortable: false, width: '140px' },
  { title: '', key: 'actions', sortable: false, width: '100px', align: 'end' as const },
]

const form = reactive({
  name: '', transactionType: 3, accountId: null as number | null,
  destinationAccountId: null as number | null, categoryId: null as number | null, description: '',
})

const accountOptions = computed(() => accounts.value.map(a => ({ title: a.name, value: a.id })))
const categoryOptions = computed(() => categories.value
  .filter(c => form.transactionType === 2 ? c.categoryType === 'INCOME' : c.categoryType === 'EXPENSE')
  .map(c => ({ title: c.name, value: c.id })))

function typeName(type: number) {
  switch (type) { case 2: return 'Income'; case 3: return 'Expense'; case 4: return 'Transfer'; default: return 'Other' }
}
function typeColor(type: number) {
  switch (type) { case 2: return 'success'; case 3: return 'error'; case 4: return 'info'; default: return 'grey' }
}
function typeIcon(type: number) {
  switch (type) { case 2: return 'mdi-arrow-down-bold'; case 3: return 'mdi-arrow-up-bold'; case 4: return 'mdi-swap-horizontal'; default: return 'mdi-cash' }
}
function amountClass(type: number) { return type === 2 ? 'text-success' : type === 3 ? 'text-error' : 'text-info' }
function amountPrefix(type: number) { return type === 2 ? '+' : '-' }
function fmt(c: number) { return (c / 100).toLocaleString('en-US', { minimumFractionDigits: 2 }) }

function openCreate() {
  editingTemplate.value = null
  form.name = ''; form.transactionType = 3; form.accountId = accounts.value[0]?.id || null
  form.destinationAccountId = null; form.categoryId = null; form.description = ''
  amountStr.value = ''
  dialog.value = true
}

function openEdit(t: Template) {
  editingTemplate.value = t
  form.name = t.name; form.transactionType = t.transactionType; form.accountId = t.accountId
  form.destinationAccountId = t.destinationAccountId; form.categoryId = t.categoryId; form.description = t.description || ''
  amountStr.value = t.amount ? (t.amount / 100).toString() : ''
  dialog.value = true
}

function confirmDelete(t: Template) {
  deletingTemplate.value = t
  deleteDialog.value = true
}

async function doDelete() {
  if (!deletingTemplate.value) return
  deleting.value = true
  try {
    await api.delete(`/templates/${deletingTemplate.value.id}`)
    deleteDialog.value = false
    deletingTemplate.value = null
    await fetchData()
  } finally { deleting.value = false }
}

async function fetchData() {
  loading.value = true
  try {
    const [tmpl, acc, cats] = await Promise.all([
      api.get<Template[]>('/templates'),
      api.get<Account[]>('/accounts'),
      api.get<Category[]>('/categories'),
    ])
    templates.value = tmpl
    accounts.value = acc
    categories.value = cats
  } finally { loading.value = false }
}

async function save() {
  saving.value = true
  try {
    const payload = {
      name: form.name,
      transactionType: form.transactionType,
      accountId: form.accountId,
      categoryId: form.transactionType !== 4 ? form.categoryId : null,
      destinationAccountId: form.transactionType === 4 ? form.destinationAccountId : null,
      amount: Math.round(parseFloat(amountStr.value || '0') * 100),
      description: form.description,
    }
    if (editingTemplate.value) {
      await api.put(`/templates/${editingTemplate.value.id}`, payload)
    } else {
      await api.post('/templates', payload)
    }
    dialog.value = false
    await fetchData()
  } finally { saving.value = false }
}

onMounted(fetchData)
</script>