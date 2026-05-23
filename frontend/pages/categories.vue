<!-- pages/categories.vue — Designed Categories page -->
<template>
  <div style="max-width: 1200px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Categories</h1>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" rounded="lg" @click="openCreate()">Add Category</v-btn>
    </div>

    <!-- Type Tabs -->
    <v-tabs v-model="activeTab" color="primary" class="mb-4" density="compact">
      <v-tab value="EXPENSE">
        <v-icon start size="18">mdi-arrow-up-bold</v-icon> Expense
      </v-tab>
      <v-tab value="INCOME">
        <v-icon start size="18">mdi-arrow-down-bold</v-icon> Income
      </v-tab>
    </v-tabs>

    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <!-- Category List -->
    <v-card class="rounded-lg" elevation="1" v-if="!loading">
      <v-list lines="one" v-if="filteredCategories.length">
        <v-list-item v-for="cat in filteredCategories" :key="cat.id" class="hover-row">
          <template v-slot:prepend>
            <v-icon :color="activeTab === 'EXPENSE' ? 'error' : 'success'" size="22" class="mr-3">
              {{ activeTab === 'EXPENSE' ? 'mdi-circle-medium' : 'mdi-circle-medium' }}
            </v-icon>
          </template>
          <v-list-item-title class="font-weight-medium">{{ cat.name }}</v-list-item-title>
          <template v-slot:append>
            <div class="action-icons">
              <v-btn icon="mdi-pencil-outline" variant="text" size="x-small" density="compact" class="mr-1" />
              <v-btn icon="mdi-delete-outline" variant="text" size="x-small" density="compact" color="error" />
            </div>
          </template>
        </v-list-item>
      </v-list>
      <v-card-text v-else class="text-caption text-grey text-center py-6">
        No {{ activeTab.toLowerCase() }} categories yet.
      </v-card-text>
    </v-card>

    <!-- Create Dialog -->
    <v-dialog v-model="dialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          New Category
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="dialog = false" />
        </v-card-title>
        <v-card-text>
          <v-text-field v-model="form.name" label="Category Name" variant="outlined" density="comfortable" :rules="[required]" class="mb-3" />
          <div class="mb-3">
            <div class="text-caption text-grey mb-2">Type</div>
            <v-btn-toggle v-model="form.type" mandatory divided color="primary" density="compact" class="w-100">
              <v-btn value="EXPENSE" size="large" class="text-none flex-grow-1 text-error">Expense</v-btn>
              <v-btn value="INCOME" size="large" class="text-none flex-grow-1 text-success">Income</v-btn>
            </v-btn-toggle>
          </div>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="lg" @click="save" :loading="saving">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

interface Category { id: number; name: string; categoryType: string }

const categories = shallowRef<Category[]>([])
const loading = ref(true)
const activeTab = ref('EXPENSE')
const dialog = ref(false)
const saving = ref(false)
const required = (v: string) => !!v || 'Required'
const form = reactive({ name: '', type: 'EXPENSE' })

const filteredCategories = computed(() => categories.value.filter(c => c.categoryType === activeTab.value))

function openCreate() { form.name = ''; form.type = activeTab.value; dialog.value = true }

async function fetchData() {
  loading.value = true
  try { categories.value = await api.get<Category[]>('/categories') }
  finally { loading.value = false }
}

async function save() {
  saving.value = true
  try {
    await api.post(`/categories?name=${encodeURIComponent(form.name)}&type=${form.type}`)
    dialog.value = false
    await fetchData()
  } finally { saving.value = false }
}

onMounted(fetchData)
</script>

<style scoped>
.hover-row .action-icons { opacity: 0; transition: opacity 0.15s; }
.hover-row:hover .action-icons { opacity: 1; }
</style>
