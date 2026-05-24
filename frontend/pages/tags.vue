<!-- pages/tags.vue — Enhanced Tags Management with Groups -->
<template>
  <div style="max-width: 1100px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Tags</h1>
      <v-spacer />
      <v-btn variant="outlined" size="small" class="mr-2" @click="showGroupDialog = true">
        <v-icon start size="16">mdi-folder-outline</v-icon> Groups
      </v-btn>
      <v-btn color="primary" prepend-icon="mdi-plus" rounded="lg" @click="openCreate()">Add Tag</v-btn>
    </div>

    <!-- Tag Groups Sidebar + Tags Grid -->
    <v-row>
      <!-- Groups Sidebar -->
      <v-col cols="12" md="3">
        <v-card class="rounded-lg" elevation="1">
          <v-card-title class="text-subtitle-1 font-weight-bold">Groups</v-card-title>
          <v-list density="compact">
            <v-list-item
              :active="selectedGroup === null"
              @click="selectedGroup = null"
              class="cursor-pointer"
            >
              <template v-slot:prepend><v-icon>mdi-tag-multiple</v-icon></template>
              <v-list-item-title>All Tags</v-list-item-title>
              <v-list-item-subtitle>{{ tags.length }} tags</v-list-item-subtitle>
            </v-list-item>
            <v-list-item
              v-for="group in tagGroups"
              :key="group.id"
              :active="selectedGroup === group.id"
              @click="selectedGroup = group.id"
              class="cursor-pointer"
            >
              <template v-slot:prepend><v-icon :color="group.color">mdi-folder</v-icon></template>
              <v-list-item-title>{{ group.name }}</v-list-item-title>
              <v-list-item-subtitle>{{ group.tagCount }} tags</v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>

      <!-- Tags Grid -->
      <v-col cols="12" md="9">
        <!-- Batch Actions Bar (when items selected) -->
        <v-card v-if="selectedTags.length > 0" class="mb-4 pa-3 rounded-lg" elevation="2" color="primary-lighten-5">
          <div class="d-flex align-center">
            <span class="text-body-2 font-weight-medium mr-3">{{ selectedTags.length }} selected</span>
            <v-btn size="small" variant="outlined" class="mr-2" @click="batchAddGroup">
              <v-icon start size="16">mdi-folder-plus</v-icon> Add to Group
            </v-btn>
            <v-btn size="small" variant="outlined" class="mr-2" @click="batchRemoveGroup">
              <v-icon start size="16">mdi-folder-remove</v-icon> Remove from Group
            </v-btn>
            <v-btn size="small" variant="outlined" color="error" @click="batchDelete">
              <v-icon start size="16">mdi-delete</v-icon> Delete
            </v-btn>
            <v-spacer />
            <v-btn size="small" variant="text" @click="selectedTags = []">Clear</v-btn>
          </div>
        </v-card>

        <!-- Loading -->
        <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

        <!-- Tags Cards -->
        <v-card v-if="!loading" class="rounded-lg" elevation="1">
          <v-list lines="two">
            <v-list-item v-for="tag in filteredTags" :key="tag.id" class="py-3">
              <template v-slot:prepend>
                <v-checkbox
                  :model-value="selectedTags.includes(tag.id)"
                  @update:model-value="toggleTag(tag.id)"
                  hide-details
                  class="mr-2"
                />
                <v-avatar size="40" :style="{ backgroundColor: tag.color + '20' }">
                  <v-icon :color="tag.color">mdi-tag</v-icon>
                </v-avatar>
              </template>
              <v-list-item-title class="font-weight-medium">
                <span :style="{ color: tag.color }">●</span> {{ tag.name }}
                <v-chip v-if="tag.groupName" size="x-small" class="ml-2" variant="outlined">
                  {{ tag.groupName }}
                </v-chip>
              </v-list-item-title>
              <v-list-item-subtitle>
                Created {{ formatDate(tag.createdTime) }}
                <span v-if="tag.transactionCount" class="text-grey ml-2">— {{ tag.transactionCount }} transactions</span>
              </v-list-item-subtitle>
              <template v-slot:append>
                <div class="action-icons">
                  <v-btn icon="mdi-pencil" variant="text" size="small" color="primary" @click="openEdit(tag)" />
                  <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="confirmDelete(tag)" />
                </div>
              </template>
            </v-list-item>
          </v-list>

          <v-alert v-if="filteredTags.length === 0" type="info" variant="tonal" class="ma-4 rounded-lg">
            {{ selectedGroup ? 'No tags in this group.' : 'No tags yet.' }}
            <v-btn variant="text" size="small" @click="openCreate()">Create one</v-btn>
          </v-alert>
        </v-card>
      </v-col>
    </v-row>

    <!-- Create/Edit Tag Dialog -->
    <v-dialog v-model="dialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          {{ editingTag ? 'Edit Tag' : 'Add Tag' }}
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="dialog = false" />
        </v-card-title>
        <v-card-text>
          <v-text-field v-model="form.name" label="Tag Name" variant="outlined" density="comfortable" class="mb-4" :rules="[required]" />
          <v-select v-model="form.groupId" :items="groupOptions" label="Group (Optional)" variant="outlined" density="comfortable" class="mb-4" clearable />
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

    <!-- Tag Groups Dialog -->
    <v-dialog v-model="showGroupDialog" max-width="500">
      <v-card class="rounded-lg">
        <v-card-title class="d-flex align-center">
          Tag Groups
          <v-spacer />
          <v-btn icon="mdi-plus" variant="text" size="small" @click="openCreateGroup" />
          <v-btn icon="mdi-close" variant="text" size="small" @click="showGroupDialog = false" />
        </v-card-title>
        <v-card-text>
          <v-list lines="one">
            <v-list-item v-for="group in tagGroups" :key="group.id" class="px-0">
              <template v-slot:prepend>
                <v-icon :color="group.color" class="mr-3">mdi-folder</v-icon>
              </template>
              <v-list-item-title>{{ group.name }}</v-list-item-title>
              <template v-slot:append>
                <v-btn icon="mdi-pencil" variant="text" size="small" @click="openEditGroup(group)" />
                <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="deleteGroup(group)" />
              </template>
            </v-list-item>
          </v-list>
          <v-alert v-if="tagGroups.length === 0" type="info" variant="tonal" class="mt-2 rounded-lg">
            No groups yet. Click + to create one.
          </v-alert>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- Create/Edit Group Dialog -->
    <v-dialog v-model="showGroupEditDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title>{{ editingGroup ? 'Edit Group' : 'New Group' }}</v-card-title>
        <v-card-text>
          <v-text-field v-model="groupForm.name" label="Group Name" variant="outlined" density="comfortable" class="mb-4" :rules="[required]" />
          <div class="text-caption text-grey mb-2">Color</div>
          <div class="d-flex flex-wrap ga-2">
            <v-btn
              v-for="color in presetColors"
              :key="color"
              :color="color"
              :icon="groupForm.color === color ? 'mdi-check' : 'mdi-circle'"
              :variant="groupForm.color === color ? 'elevated' : 'outlined'"
              size="small"
              @click="groupForm.color = color"
            />
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showGroupEditDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="saveGroup" :loading="savingGroup">{{ editingGroup ? 'Save' : 'Create' }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Batch Group Selection Dialog -->
    <v-dialog v-model="showBatchGroupDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title>{{ batchAction === 'add' ? 'Add to Group' : 'Remove from Group' }}</v-card-title>
        <v-card-text>
          <v-select v-model="batchTargetGroupId" :items="groupOptions" label="Select Group" variant="outlined" density="comfortable" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showBatchGroupDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="executeBatchGroup" :loading="executingBatch">{{ batchAction === 'add' ? 'Add' : 'Remove' }}</v-btn>
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

interface Tag { id: number; name: string; color: string; groupId?: number; groupName?: string; createdTime: number; transactionCount?: number }
interface TagGroup { id: number; name: string; color: string; tagCount: number }

const tags = shallowRef<Tag[]>([])
const tagGroups = shallowRef<TagGroup[]>([])
const loading = ref(true)
const dialog = ref(false)
const saving = ref(false)
const editingTag = ref<Tag | null>(null)

const selectedTags = ref<number[]>([])
const selectedGroup = ref<number | null>(null)

const deleteDialog = ref(false)
const deletingTag = ref<Tag | null>(null)
const deleting = ref(false)

// Group dialogs
const showGroupDialog = ref(false)
const showGroupEditDialog = ref(false)
const showBatchGroupDialog = ref(false)
const editingGroup = ref<TagGroup | null>(null)
const savingGroup = ref(false)
const executingBatch = ref(false)
const batchAction = ref('')
const batchTargetGroupId = ref<number | null>(null)

const required = (v: any) => !!v || 'Required'

const presetColors = [
  '#F44336', '#E91E63', '#9C27B0', '#673AB7',
  '#3F51B5', '#2196F3', '#03A9F4', '#00BCD4',
  '#009688', '#4CAF50', '#8BC34A', '#CDDC39',
  '#FFEB3B', '#FFC107', '#FF9800', '#FF5722',
]

const form = reactive({ name: '', color: '#1976D2', groupId: null as number | null })
const groupForm = reactive({ name: '', color: '#1976D2' })

const groupOptions = computed(() => tagGroups.value.map(g => ({ title: g.name, value: g.id })))
const filteredTags = computed(() =>
  selectedGroup.value ? tags.value.filter(t => t.groupId === selectedGroup.value) : tags.value
)

function formatDate(ts: number) {
  return new Date(ts * 1000).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

function toggleTag(id: number) {
  const idx = selectedTags.value.indexOf(id)
  if (idx >= 0) selectedTags.value.splice(idx, 1)
  else selectedTags.value.push(id)
}

function openCreate() {
  editingTag.value = null
  form.name = ''
  form.color = '#1976D2'
  form.groupId = selectedGroup.value
  dialog.value = true
}

function openEdit(tag: Tag) {
  editingTag.value = tag
  form.name = tag.name
  form.color = tag.color
  form.groupId = tag.groupId || null
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
    selectedTags.value = selectedTags.value.filter(id => tags.value.some(t => t.id === id))
    await fetchData()
  } finally { deleting.value = false }
}

async function fetchData() {
  loading.value = true
  try {
    const [tagResp, groupResp] = await Promise.all([
      api.get<{tags: Tag[]}>('/tags/with_count.json'),
      api.get<TagGroup[]>('/tag_groups'),
    ])
    tags.value = tagResp.tags || tagResp
    tagGroups.value = groupResp
  } catch {
    // Fallback to simple list
    tags.value = await api.get<Tag[]>('/tags')
    tagGroups.value = []
  } finally { loading.value = false }
}

async function save() {
  if (!form.name.trim()) return
  saving.value = true
  try {
    const payload = { name: form.name, color: form.color, groupId: form.groupId }
    if (editingTag.value) {
      await api.put(`/tags/${editingTag.value.id}`, payload)
    } else {
      await api.post('/tags', payload)
    }
    dialog.value = false
    await fetchData()
  } finally { saving.value = false }
}

// Group management
function openCreateGroup() {
  editingGroup.value = null
  groupForm.name = ''
  groupForm.color = '#1976D2'
  showGroupEditDialog.value = true
}

function openEditGroup(group: TagGroup) {
  editingGroup.value = group
  groupForm.name = group.name
  groupForm.color = group.color
  showGroupEditDialog.value = true
}

async function saveGroup() {
  if (!groupForm.name.trim()) return
  savingGroup.value = true
  try {
    const payload = { name: groupForm.name, color: groupForm.color }
    if (editingGroup.value) {
      await api.put(`/tag_groups/${editingGroup.value.id}`, payload)
    } else {
      await api.post('/tag_groups', payload)
    }
    showGroupEditDialog.value = false
    await fetchData()
  } finally { savingGroup.value = false }
}

async function deleteGroup(group: TagGroup) {
  await api.delete(`/tag_groups/${group.id}`)
  await fetchData()
}

// Batch operations
function batchAddGroup() { batchAction.value = 'add'; batchTargetGroupId.value = null; showBatchGroupDialog.value = true }
function batchRemoveGroup() { batchAction.value = 'remove'; batchTargetGroupId.value = null; showBatchGroupDialog.value = true }

async function executeBatchGroup() {
  if (!batchTargetGroupId.value && batchAction.value === 'add') return
  executingBatch.value = true
  try {
    if (batchAction.value === 'add') {
      await api.post('/tags/batch_add_group.json', { tagIds: selectedTags.value, groupId: batchTargetGroupId.value })
    } else {
      await api.post('/tags/batch_clear_group.json', { tagIds: selectedTags.value })
    }
    showBatchGroupDialog.value = false
    selectedTags.value = []
    await fetchData()
  } finally { executingBatch.value = false }
}

async function batchDelete() {
  for (const id of selectedTags.value) {
    await api.delete(`/tags/${id}`)
  }
  selectedTags.value = []
  await fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.cursor-pointer { cursor: pointer; }
.action-icons { opacity: 0; transition: opacity 0.15s; }
:hover .action-icons { opacity: 1; }
</style>