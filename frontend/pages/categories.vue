<!-- pages/categories.vue — RESTful API Categories page -->
<template>
  <div style="max-width: 1200px; margin: 0 auto">
    <div class="d-flex align-center mb-4">
      <h1 class="text-h4 font-weight-bold">Categories</h1>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" rounded="pill" @click="openCreate()">Add Category</v-btn>
    </div>

    <!-- Type Tabs -->
    <v-tabs v-model="activeTab" color="primary" class="mb-4" density="compact">
      <v-tab value="EXPENSE">
        <v-icon start size="18">mdi-arrow-up-bold</v-icon> Expense
      </v-tab>
      <v-tab value="INCOME">
        <v-icon start size="18">mdi-arrow-down-bold</v-icon> Income
      </v-tab>
      <v-tab value="TRANSFER">
        <v-icon start size="18">mdi-swap-horizontal</v-icon> Transfer
      </v-tab>
    </v-tabs>

    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <!-- Category List -->
    <v-card v-if="!loading" class="stripe-card">
      <v-list lines="one" v-if="filteredCategories.length">
        <v-list-item 
          v-for="cat in filteredCategories" 
          :key="cat.id" 
          class="category-item"
          @click="openEdit(cat)"
        >
          <template v-slot:prepend>
            <div 
              class="category-icon mr-3" 
              :style="{ background: cat.color || 'linear-gradient(135deg, #667eea, #764ba2)' }"
            >
              <v-icon size="22" color="white">
                {{ cat.icon || 'mdi-circle-medium' }}
              </v-icon>
            </div>
          </template>
          <v-list-item-title class="font-weight-medium">{{ cat.name }}</v-list-item-title>
          <v-list-item-subtitle v-if="cat.comment">{{ cat.comment }}</v-list-item-subtitle>
          <template v-slot:append>
            <v-btn 
              icon="mdi-eye-off-outline" 
              variant="text" 
              size="small" 
              density="compact"
              @click.stop="toggleHidden(cat)"
              v-if="cat.hidden"
            />
            <v-btn 
              icon="mdi-pencil-outline" 
              variant="text" 
              size="small" 
              density="compact" 
              class="action-btn"
            />
          </template>
        </v-list-item>
      </v-list>
      <v-card-text v-else class="text-caption text-grey text-center py-8">
        No {{ activeTab.toLowerCase() }} categories yet.
      </v-card-text>
    </v-card>

    <!-- Create/Edit Dialog -->
    <v-dialog v-model="dialog" max-width="480">
      <v-card class="stripe-card">
        <v-card-title class="d-flex align-center">
          {{ isEditing ? 'Edit Category' : 'New Category' }}
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" size="small" @click="dialog = false" />
        </v-card-title>
        <v-card-text>
          <v-text-field 
            v-model="form.name" 
            label="Category Name" 
            variant="outlined" 
            density="comfortable" 
            :rules="[required]" 
            class="mb-3" 
          />

          <!-- Type Selection -->
          <div class="mb-4">
            <div class="text-caption text-grey mb-2">Type</div>
            <v-btn-toggle v-model="form.type" mandatory divided color="primary" density="compact" class="w-100">
              <v-btn value="EXPENSE" size="large" class="text-none flex-grow-1">Expense</v-btn>
              <v-btn value="INCOME" size="large" class="text-none flex-grow-1">Income</v-btn>
              <v-btn value="TRANSFER" size="large" class="text-none flex-grow-1">Transfer</v-btn>
            </v-btn-toggle>
          </div>

          <!-- Icon Selector -->
          <div class="mb-4">
            <div class="text-caption text-grey mb-2">Icon</div>
            <v-select
              v-model="form.icon"
              :items="iconOptions"
              item-title="title"
              item-value="value"
              variant="outlined"
              density="comfortable"
              placeholder="Select icon"
              clearable
            >
              <template v-slot:item="{ item, props }">
                <v-list-item v-bind="props">
                  <template v-slot:prepend>
                    <v-icon>{{ item.value }}</v-icon>
                  </template>
                </v-list-item>
              </template>
            </v-select>
          </div>

          <!-- Color Picker -->
          <div class="mb-4">
            <div class="text-caption text-grey mb-2">Color</div>
            <div class="d-flex align-center gap-2">
              <v-text-field
                v-model="form.color"
                label="Color (hex)"
                variant="outlined"
                density="comfortable"
                placeholder="#FF5722"
                :rules="[colorRule]"
                class="flex-grow-1"
              />
              <input 
                type="color" 
                v-model="form.color" 
                class="color-picker"
                title="Pick color"
              />
            </div>
          </div>

          <!-- Comment -->
          <v-textarea
            v-model="form.comment"
            label="Comment (optional)"
            variant="outlined"
            density="comfortable"
            rows="2"
            auto-grow
          />
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">Cancel</v-btn>
          <v-btn color="primary" rounded="pill" @click="save" :loading="saving">
            {{ isEditing ? 'Update' : 'Create' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

interface Category {
  id: number
  name: string
  categoryType: string
  icon?: string
  color?: string
  comment?: string
  hidden?: boolean
}

const categories = shallowRef<Category[]>([])
const loading = ref(true)
const activeTab = ref('EXPENSE')
const dialog = ref(false)
const saving = ref(false)
const editingId = ref<number | null>(null)
const required = (v: string) => !!v || 'Required'
const colorRule = (v: string) => !v || /^#[0-9A-Fa-f]{6}$/.test(v) || 'Invalid hex color (e.g., #FF5722)'

const form = reactive({
  name: '',
  type: 'EXPENSE',
  icon: '',
  color: '',
  comment: ''
})

// Icon options for selector
const iconOptions = [
  { title: 'Cash', value: 'mdi-cash' },
  { title: 'Cart', value: 'mdi-cart' },
  { title: 'Food', value: 'mdi-food' },
  { title: 'Food Apple', value: 'mdi-food-apple' },
  { title: 'Car', value: 'mdi-car' },
  { title: 'Home', value: 'mdi-home' },
  { title: 'Heart', value: 'mdi-heart' },
  { title: 'Star', value: 'mdi-star' },
  { title: 'Gift', value: 'mdi-gift' },
  { title: 'Medical', value: 'mdi-medical-bag' },
  { title: 'School', value: 'mdi-school' },
  { title: 'Account', value: 'mdi-account' },
  { title: 'Phone', value: 'mdi-phone' },
  { title: 'Laptop', value: 'mdi-laptop' },
  { title: 'Gamepad', value: 'mdi-gamepad-variant' },
  { title: 'Movie', value: 'mdi-movie' },
  { title: 'Coffee', value: 'mdi-coffee' },
  { title: 'Beer', value: 'mdi-glass-mug-variant' },
  { title: 'Plane', value: 'mdi-airplane' },
  { title: 'Train', value: 'mdi-train' },
  { title: 'Bus', value: 'mdi-bus' },
  { title: 'Pet', value: 'mdi-paw' },
  { title: 'Flower', value: 'mdi-flower' },
  { title: 'Dumbbell', value: 'mdi-dumbbell' },
  { title: 'Shirt', value: 'mdi-tshirt-crew' },
  { title: 'Shopping', value: 'mdi-shopping' },
  { title: 'Credit Card', value: 'mdi-credit-card' },
  { title: 'Bank', value: 'mdi-bank' },
  { title: 'Wallet', value: 'mdi-wallet' },
  { title: 'Tag', value: 'mdi-tag' },
  { title: 'Label', value: 'mdi-label' },
  { title: 'Briefcase', value: 'mdi-briefcase' },
  { title: 'Tools', value: 'mdi-tools' },
  { title: 'Wrench', value: 'mdi-wrench' },
  { title: 'Lightbulb', value: 'mdi-lightbulb' },
  { title: 'Power', value: 'mdi-power' },
  { title: 'Water', value: 'mdi-water' },
  { title: 'Fire', value: 'mdi-fire' },
  { title: 'Leaf', value: 'mdi-leaf' },
  { title: 'Puzzle', value: 'mdi-puzzle' },
  { title: 'Baby Face', value: 'mdi-baby-face' },
  { title: 'Dog', value: 'mdi-dog' },
  { title: 'Cat', value: 'mdi-cat' },
  { title: 'Music', value: 'mdi-music' },
  { title: 'Guitar', value: 'mdi-guitar-acoustic' },
  { title: 'Camera', value: 'mdi-camera' },
  { title: 'Book', value: 'mdi-book' },
  { title: 'Email', value: 'mdi-email' },
]

const isEditing = computed(() => editingId.value !== null)

const filteredCategories = computed(() => 
  categories.value.filter(c => c.categoryType === activeTab.value)
)

function openCreate() {
  editingId.value = null
  form.name = ''
  form.type = activeTab.value
  form.icon = ''
  form.color = ''
  form.comment = ''
  dialog.value = true
}

function openEdit(cat: Category) {
  editingId.value = cat.id
  form.name = cat.name
  form.type = cat.categoryType
  form.icon = cat.icon || ''
  form.color = cat.color || ''
  form.comment = cat.comment || ''
  dialog.value = true
}

async function fetchData() {
  loading.value = true
  try {
    // useApi already unwraps the ApiResponse envelope, so res IS the data
    const res = await api.get<Category[]>('/categories')
    categories.value = res || []
  } catch (e) {
    console.error('Failed to fetch categories:', e)
  } finally {
    loading.value = false
  }
}

function typeToNumber(type: string): number {
  switch (type) {
    case 'INCOME': return 1
    case 'EXPENSE': return 2
    case 'TRANSFER': return 3
    default: return 2
  }
}

async function save() {
  if (!form.name) return
  
  saving.value = true
  try {
    const payload = {
      name: form.name,
      type: typeToNumber(form.type),
      icon: form.icon || null,
      color: form.color || null,
      comment: form.comment || null
    }
    
    if (isEditing.value) {
      await api.put(`/categories/${editingId.value}`, payload)
    } else {
      await api.post('/categories', payload)
    }
    
    dialog.value = false
    await fetchData()
  } catch (e) {
    console.error('Failed to save category:', e)
  } finally {
    saving.value = false
  }
}

async function toggleHidden(cat: Category) {
  try {
    await api.patch(`/categories/${cat.id}/hidden`, { hidden: !cat.hidden })
    await fetchData()
  } catch (e) {
    console.error('Failed to toggle hidden:', e)
  }
}

onMounted(fetchData)
</script>

<style scoped>
.category-item {
  border-bottom: 1px solid var(--stripe-hairline, #e3e8ee);
}

.category-item:last-child {
  border-bottom: none;
}

.category-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.action-btn {
  opacity: 0;
  transition: opacity 0.15s;
}

.category-item:hover .action-btn {
  opacity: 1;
}

.color-picker {
  width: 48px;
  height: 48px;
  border: 1px solid #e3e8ee;
  border-radius: 8px;
  cursor: pointer;
  padding: 2px;
}

.color-picker::-webkit-color-swatch-wrapper {
  padding: 0;
}

.color-picker::-webkit-color-swatch {
  border: none;
  border-radius: 6px;
}
</style>
