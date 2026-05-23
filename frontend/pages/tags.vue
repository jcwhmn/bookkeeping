<!-- pages/tags.vue — Tags Management -->
<template>
  <div style="max-width: 900px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Tags</h1>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" rounded="lg" @click="openCreate()">Add Tag</v-btn>
    </div>

    <!-- Loading -->
    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <!-- Tags Grid -->
    <v-card v-if="!loading" class="rounded-lg" elevation="1">
      <v-list lines="two">
        <v-list-item v-for="tag in tags" :key="tag.id" class="py-3">
          <template v-slot:prepend>
            <v-avatar size="40" :style="{ backgroundColor: tag.color + '20' }">
              <v-icon :color="tag.color">mdi-tag</v-icon>
            </v-avatar>
          </template>
          <v-list-item-title class="font-weight-medium">
            <span :style="{ color: tag.color }">●</span> {{ tag.name }}
          </v-list-item-title>
          <v-list-item-subtitle>
            Created {{ formatDate(tag.createdTime) }}
          </v-list-item-subtitle>
          <template v-slot:append>
            <v-btn icon="mdi-pencil" variant="text" size="small" color="primary" @click="openEdit(tag)" />
            <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="confirmDelete(tag)" />
          </template>
        </v-list-item>
      </v-list>

      <v-alert v-if="tags.length === 0" type="info" variant="tonal" class="ma-4 rounded-lg">
        No tags yet. <v-btn variant="text" size="small" @click="openCreate()">Create one</v-btn>
      </v-alert>
    </v-card>

    <!-- Create/Edit Dialog -->
    <v-dialog v-model="dialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          {{ editingTag ? 'Edit Tag' : 'Add Tag' }}
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="dialog = false" />
        </v-card-title>
        <v-card-text>
          <v-text-field v-model="form.name" label="Tag Name" variant="outlined" density="comfortable" class="mb-4" />
          <div class="mb-2 text-grey">Color</div>
          <div class="d-flex flex-wrap ga-2">
            <v-btn
              v-for="color in presetColors"
              :key="color"
              :color="color"
              :icon="form.color === color ? 'mdi-check' : 'mdi-circle'"
              :variant="form.color === color ? 'elevated' : 'outlined'"
              size="small"
              @click="form.color = color"
            />
          </div>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="lg" @click="save" :loading="saving">{{ editingTag ? 'Save' : 'Create' }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="text-error">
          <v-icon color="error" class="mr-2">mdi-alert-circle</v-icon>
          Delete Tag?
        </v-card-title>
        <v-card-text>
          <v-alert type="warning" variant="tonal" class="rounded-lg">
            Delete <strong>{{ deletingTag?.name }}</strong>? This won't delete transactions using this tag.
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

interface Tag { id: number; name: string; color: string; createdTime: number }

const tags = shallowRef<Tag[]>([])
const loading = ref(true)
const dialog = ref(false)
const saving = ref(false)
const editingTag = ref<Tag | null>(null)

const deleteDialog = ref(false)
const deletingTag = ref<Tag | null>(null)
const deleting = ref(false)

const form = reactive({ name: '', color: '#1976D2' })

const presetColors = [
  '#F44336', '#E91E63', '#9C27B0', '#673AB7',
  '#3F51B5', '#2196F3', '#03A9F4', '#00BCD4',
  '#009688', '#4CAF50', '#8BC34A', '#CDDC39',
  '#FFEB3B', '#FFC107', '#FF9800', '#FF5722',
]

function formatDate(ts: number) {
  return new Date(ts * 1000).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

function openCreate() {
  editingTag.value = null
  form.name = ''
  form.color = '#1976D2'
  dialog.value = true
}

function openEdit(tag: Tag) {
  editingTag.value = tag
  form.name = tag.name
  form.color = tag.color
  dialog.value = true
}

function confirmDelete(tag: Tag) {
  deletingTag.value = tag
  deleteDialog.value = true
}

async function doDelete() {
  if (!deletingTag.value) return
  deleting.value = true
  try {
    await api.delete(`/tags/${deletingTag.value.id}`)
    deleteDialog.value = false
    deletingTag.value = null
    await fetchTags()
  } finally { deleting.value = false }
}

async function fetchTags() {
  loading.value = true
  try {
    tags.value = await api.get<Tag[]>('/tags')
  } finally { loading.value = false }
}

async function save() {
  if (!form.name.trim()) return
  saving.value = true
  try {
    if (editingTag.value) {
      await api.put(`/tags/${editingTag.value.id}`, { name: form.name, color: form.color })
    } else {
      await api.post('/tags', { name: form.name, color: form.color })
    }
    dialog.value = false
    await fetchTags()
  } finally { saving.value = false }
}

onMounted(fetchTags)
</script>