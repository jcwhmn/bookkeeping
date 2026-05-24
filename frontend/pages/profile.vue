<!-- pages/profile.vue — Enhanced User Profile with Settings -->
<template>
  <div style="max-width: 900px; margin: 0 auto">
    <h1 class="text-h4 font-weight-bold mb-6">Profile & Settings</h1>

    <!-- Tabs -->
    <v-tabs v-model="activeTab" color="primary" class="mb-4" density="compact">
      <v-tab value="basic">
        <v-icon start size="18">mdi-account-outline</v-icon> Basic
      </v-tab>
      <v-tab value="security">
        <v-icon start size="18">mdi-shield-outline</v-icon> Security
      </v-tab>
      <v-tab value="tokens">
        <v-icon start size="18">mdi-key-outline</v-icon> Tokens
      </v-tab>
      <v-tab value="data">
        <v-icon start size="18">mdi-database-outline</v-icon> Data
      </v-tab>
    </v-tabs>

    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

    <!-- ============ BASIC TAB ============ -->
    <v-card v-if="!loading && activeTab === 'basic'" class="rounded-lg" elevation="1">
      <v-card-text class="pa-6">
        <!-- Avatar -->
        <div class="d-flex align-center mb-6">
          <v-avatar size="80" color="primary" class="mr-4">
            <v-img v-if="user?.avatar" :src="user.avatar" />
            <span v-else class="text-white text-h4">{{ (user?.nickname || user?.username || '?')[0]?.toUpperCase() }}</span>
          </v-avatar>
          <div>
            <div class="text-h6 font-weight-bold">{{ user?.nickname || user?.username }}</div>
            <div class="text-body-2 text-grey">{{ user?.email }}</div>
            <div class="mt-2">
              <v-btn variant="outlined" size="small" @click="triggerAvatarUpload">Upload Avatar</v-btn>
              <v-btn v-if="user?.avatar" variant="text" size="small" color="error" class="ml-2" @click="removeAvatar">Remove</v-btn>
            </div>
          </div>
          <input ref="avatarInput" type="file" accept="image/*" style="display:none" @change="uploadAvatar" />
        </div>

        <!-- Basic Info Form -->
        <v-form ref="formRef">
          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field v-model="form.nickname" label="Nickname" variant="outlined" density="comfortable" class="mb-3" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field v-model="form.email" label="Email" variant="outlined" density="comfortable" class="mb-3" :rules="[emailRule]" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-select v-model="form.defaultAccountId" :items="accountOptions" label="Default Account" variant="outlined" density="comfortable" class="mb-3" clearable />
            </v-col>
            <v-col cols="12" sm="6">
              <v-select v-model="form.transactionEditScope" :items="editScopeOptions" label="Transaction Edit Scope" variant="outlined" density="comfortable" class="mb-3" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-select v-model="form.firstDayOfWeek" :items="weekStartOptions" label="Week Starts On" variant="outlined" density="comfortable" class="mb-3" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-select v-model="form.fiscalYearStart" :items="monthOptions" label="Fiscal Year Starts" variant="outlined" density="comfortable" class="mb-3" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field v-model="form.dateFormat" label="Date Format" variant="outlined" density="comfortable" class="mb-3" hint="e.g., YYYY-MM-DD" persistent-hint />
            </v-col>
          </v-row>

          <v-divider class="my-4" />

          <div class="d-flex align-center mb-4">
            <div>
              <div class="text-subtitle-1 font-weight-bold">Change Password</div>
              <div class="text-caption text-grey">Leave blank to keep current password</div>
            </div>
          </div>

          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field v-model="form.newPassword" label="New Password" type="password" variant="outlined" density="comfortable" class="mb-3" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field v-model="form.confirmPassword" label="Confirm Password" type="password" variant="outlined" density="comfortable" class="mb-3" :rules="[matchRule]" />
            </v-col>
          </v-row>

          <v-btn color="primary" rounded="lg" @click="saveProfile" :loading="saving">Save Changes</v-btn>
        </v-form>
      </v-card-text>
    </v-card>

    <!-- ============ SECURITY TAB ============ -->
    <v-card v-if="!loading && activeTab === 'security'" class="rounded-lg" elevation="1">
      <v-card-text class="pa-6">
        <div class="text-subtitle-1 font-weight-bold mb-4">Password</div>
        <v-btn variant="outlined" @click="showPasswordDialog = true">Change Password</v-btn>

        <v-divider class="my-4" />

        <div class="text-subtitle-1 font-weight-bold mb-4">Two-Factor Authentication</div>
        <v-chip v-if="user?.twoFactorEnabled" color="success" class="mb-2">Enabled</v-chip>
        <v-chip v-else color="grey" class="mb-2">Not Enabled</v-chip>
        <div class="text-body-2 text-grey mb-3">Add an extra layer of security to your account</div>
        <v-btn :color="user?.twoFactorEnabled ? 'error' : 'primary'" variant="outlined" @click="toggle2FA">
          {{ user?.twoFactorEnabled ? 'Disable 2FA' : 'Enable 2FA' }}
        </v-btn>

        <v-divider class="my-4" />

        <div class="text-subtitle-1 font-weight-bold mb-4">OAuth2 Connections</div>
        <div class="text-body-2 text-grey mb-3">Manage your linked third-party accounts</div>
        <v-chip v-if="false" color="success" class="mr-2">Google</v-chip>
        <v-chip v-if="false" color="grey">GitHub</v-chip>
        <v-alert v-if="oauthConnections.length === 0" type="info" variant="tonal" class="mt-2 rounded-lg">
          No OAuth2 connections. Link your account with Google or GitHub for easier login.
        </v-alert>
        <v-btn variant="outlined" class="mt-3" @click="showOAuthDialog = true">Connect OAuth2</v-btn>
      </v-card-text>
    </v-card>

    <!-- ============ TOKENS TAB ============ -->
    <v-card v-if="!loading && activeTab === 'tokens'" class="rounded-lg" elevation="1">
      <v-card-text class="pa-6">
        <div class="d-flex align-center mb-4">
          <div class="flex-grow-1">
            <div class="text-subtitle-1 font-weight-bold">API & MCP Tokens</div>
            <div class="text-caption text-grey">Manage your API and MCP access tokens</div>
          </div>
          <v-btn color="primary" size="small" @click="showTokenDialog = true">
            <v-icon start size="16">mdi-plus</v-icon> Generate Token
          </v-btn>
        </div>

        <v-data-table
          :headers="tokenHeaders"
          :items="tokens"
          :items-per-page="10"
          class="elevation-0"
        >
          <template v-slot:item.tokenType="{ item }">
            <v-chip size="small" :color="item.tokenType === 8 ? 'primary' : 'info'" variant="tonal">
              {{ item.tokenType === 8 ? 'API' : 'MCP' }}
            </v-chip>
          </template>

          <template v-slot:item.lastSeen="{ item }">
            <span class="text-grey">{{ item.lastSeen ? new Date(item.lastSeen * 1000).toLocaleDateString() : '-' }}</span>
          </template>

          <template v-slot:item.actions="{ item }">
            <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="revokeToken(item)" />
          </template>
        </v-data-table>

        <v-alert v-if="tokens.length === 0" type="info" variant="tonal" class="mt-4 rounded-lg">
          No tokens generated yet. Generate an API or MCP token to access your data programmatically.
        </v-alert>
      </v-card-text>
    </v-card>

    <!-- ============ DATA TAB ============ -->
    <v-card v-if="!loading && activeTab === 'data'" class="rounded-lg" elevation="1">
      <v-card-text class="pa-6">
        <!-- Data Statistics -->
        <div class="text-subtitle-1 font-weight-bold mb-4">Data Statistics</div>
        <v-row>
          <v-col cols="6" sm="4" v-for="stat in dataStats" :key="stat.label">
            <v-card class="pa-3 rounded-lg" elevation="0" color="grey-lighten-4">
              <div class="text-caption text-grey">{{ stat.label }}</div>
              <div class="text-h6 font-weight-bold">{{ stat.value }}</div>
            </v-card>
          </v-col>
        </v-row>

        <v-divider class="my-4" />

        <!-- Export -->
        <div class="text-subtitle-1 font-weight-bold mb-4">Export Data</div>
        <div class="d-flex ga-2">
          <v-btn variant="outlined" prepend-icon="mdi-download" @click="exportData('csv')">Export CSV</v-btn>
          <v-btn variant="outlined" prepend-icon="mdi-download" @click="exportData('tsv')">Export TSV</v-btn>
        </div>

        <v-divider class="my-4" />

        <!-- Clear Operations -->
        <div class="text-subtitle-1 font-weight-bold mb-4 text-error">Danger Zone</div>
        <v-alert type="warning" variant="tonal" class="mb-4 rounded-lg">
          <div class="text-body-2">These operations are irreversible. Please export your data before proceeding.</div>
        </v-alert>

        <div class="d-flex flex-column ga-3">
          <div class="d-flex align-center">
            <v-btn variant="outlined" color="warning" @click="confirmClear('transactions')">Clear All Transactions</v-btn>
            <span class="text-body-2 text-grey ml-3">Delete all transactions but keep accounts and categories</span>
          </div>
          <div class="d-flex align-center">
            <v-btn variant="outlined" color="error" @click="confirmClear('all')">Clear All Data</v-btn>
            <span class="text-body-2 text-grey ml-3">Delete ALL your data including accounts, categories, and transactions</span>
          </div>
        </div>
      </v-card-text>
    </v-card>

    <!-- Password Dialog -->
    <v-dialog v-model="showPasswordDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title>Change Password</v-card-title>
        <v-card-text>
          <v-text-field v-model="passwordForm.oldPassword" label="Current Password" type="password" variant="outlined" density="comfortable" class="mb-3" />
          <v-text-field v-model="passwordForm.newPassword" label="New Password" type="password" variant="outlined" density="comfortable" class="mb-3" />
          <v-text-field v-model="passwordForm.confirmPassword" label="Confirm New Password" type="password" variant="outlined" density="comfortable" :rules="[matchRule]" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showPasswordDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="changePassword" :loading="changing">Update</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Token Generation Dialog -->
    <v-dialog v-model="showTokenDialog" max-width="400">
      <v-card class="rounded-lg">
        <v-card-title>Generate Token</v-card-title>
        <v-card-text>
          <v-select v-model="tokenForm.type" :items="[{title:'API Token', value:'api'}, {title:'MCP Token', value:'mcp'}]" label="Token Type" variant="outlined" density="comfortable" class="mb-3" />
          <v-text-field v-model="tokenForm.expiresIn" label="Expires In (seconds)" type="number" variant="outlined" density="comfortable" hint="Default: 31536000 (1 year)" persistent-hint />
          <v-text-field v-model="tokenForm.password" label="Your Password" type="password" variant="outlined" density="comfortable" class="mt-3" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showTokenDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="generateToken" :loading="generating">Generate</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- New Token Result Dialog -->
    <v-dialog v-model="showNewTokenDialog" max-width="500">
      <v-card class="rounded-lg">
        <v-card-title class="text-success">
          <v-icon color="success" class="mr-2">mdi-check-circle</v-icon>
          Token Generated
        </v-card-title>
        <v-card-text>
          <v-alert type="warning" variant="tonal" class="mb-3 rounded-lg">
            <strong>Important:</strong> Copy this token now. You won't be able to see it again!
          </v-alert>
          <v-textarea v-model="newTokenValue" readonly variant="outlined" rows="3" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" @click="copyToken">Copy to Clipboard</v-btn>
          <v-btn variant="text" @click="showNewTokenDialog = false">Done</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Clear Confirmation Dialog -->
    <v-dialog v-model="showClearDialog" max-width="500">
      <v-card class="rounded-lg">
        <v-card-title class="text-error">
          <v-icon color="error" class="mr-2">mdi-alert-circle</v-icon>
          Confirm Data Deletion
        </v-card-title>
        <v-card-text>
          <v-alert type="error" variant="tonal" class="rounded-lg">
            <div class="font-weight-bold mb-2">This action cannot be undone!</div>
            <div>You are about to <strong>{{ clearType === 'all' ? 'delete ALL your data' : 'clear ALL transactions' }}</strong>.</div>
            <div class="mt-2">Please type your password to confirm:</div>
          </v-alert>
          <v-text-field v-model="clearPassword" label="Password" type="password" variant="outlined" density="comfortable" class="mt-3" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showClearDialog = false">Cancel</v-btn>
          <v-btn color="error" @click="executeClear" :loading="clearing">Delete {{ clearType === 'all' ? 'All Data' : 'Transactions' }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()
const router = useRouter()

interface User { id: number; username: string; nickname: string; email: string; avatar?: string; twoFactorEnabled?: boolean; defaultAccountId?: number; transactionEditScope?: number; firstDayOfWeek?: number; fiscalYearStart?: number; dateFormat?: string }
interface Account { id: number; name: string }
interface DataStat { label: string; value: number }
interface Token { tokenId: string; tokenType: number; userAgent?: string; lastSeen: number; isCurrent: boolean }

const auth = useAuthStore()
const user = computed(() => auth.user as User | null)
const accounts = ref<Account[]>([])

const activeTab = ref('basic')
const loading = ref(true)
const saving = ref(false)
const changing = ref(false)
const generating = ref(false)
const clearing = ref(false)

const formRef = ref()
const avatarInput = ref<HTMLInputElement>()

// Basic form
const form = reactive({
  nickname: '', email: '', defaultAccountId: null as number | null,
  transactionEditScope: 0, firstDayOfWeek: 0, fiscalYearStart: 1, dateFormat: 'YYYY-MM-DD',
  newPassword: '', confirmPassword: '',
})

// Password dialog
const showPasswordDialog = ref(false)
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

// Token dialog
const showTokenDialog = ref(false)
const showNewTokenDialog = ref(false)
const newTokenValue = ref('')
const tokens = ref<Token[]>([])
const tokenForm = reactive({ type: 'api', expiresIn: 31536000, password: '' })

// Clear dialog
const showClearDialog = ref(false)
const clearType = ref('')
const clearPassword = ref('')

// OAuth
const showOAuthDialog = ref(false)
const oauthConnections = ref<string[]>([])

const accountOptions = computed(() => accounts.value.map(a => ({ title: a.name, value: a.id })))
const editScopeOptions = [
  { title: 'No Restriction', value: 0 },
  { title: 'Today + 24 Hours', value: 1 },
  { title: 'This Week', value: 2 },
  { title: 'This Month', value: 3 },
  { title: 'This Year', value: 4 },
  { title: 'No Editing', value: 5 },
]
const weekStartOptions = [
  { title: 'Sunday', value: 0 },
  { title: 'Monday', value: 1 },
]
const monthOptions = Array.from({ length: 12 }, (_, i) => ({
  title: new Date(2000, i).toLocaleDateString('en-US', { month: 'long' }), value: i + 1
}))

const tokenHeaders = [
  { title: 'Type', key: 'tokenType', width: '100px' },
  { title: 'Last Seen', key: 'lastSeen', width: '140px' },
  { title: '', key: 'actions', width: '80px', align: 'end' as const },
]

const dataStats = ref<DataStat[]>([])

const emailRule = (v: string) => /.+@.+\..+/.test(v) || 'Invalid email'
const matchRule = (v: string) => v === form.newPassword || v === passwordForm.confirmPassword || "Passwords don't match"

async function fetchData() {
  loading.value = true
  try {
    const [acc, profile, stats] = await Promise.all([
      api.get<Account[]>('/accounts'),
      api.get<User>('/users/profile/get.json'),
      api.get<DataStat[]>('/users/data/statistics.json'),
    ])
    accounts.value = acc
    
    // Populate form
    form.nickname = profile.nickname || ''
    form.email = profile.email || ''
    form.defaultAccountId = profile.defaultAccountId || null
    form.transactionEditScope = profile.transactionEditScope || 0
    form.firstDayOfWeek = profile.firstDayOfWeek || 0
    form.fiscalYearStart = profile.fiscalYearStart || 1
    form.dateFormat = profile.dateFormat || 'YYYY-MM-DD'

    dataStats.value = stats
  } catch (e) {
    console.error('Failed to fetch profile:', e)
  } finally { loading.value = false }
}

async function saveProfile() {
  saving.value = true
  try {
    await api.post('/users/profile/update.json', {
      nickname: form.nickname,
      email: form.email,
      defaultAccountId: form.defaultAccountId,
      transactionEditScope: form.transactionEditScope,
      firstDayOfWeek: form.firstDayOfWeek,
      fiscalYearStart: form.fiscalYearStart,
      dateFormat: form.dateFormat,
      newPassword: form.newPassword || null,
    })
    await auth.fetchCurrentUser()
    showSnackbar('Profile saved successfully')
  } catch (e) {
    console.error('Failed to save profile:', e)
  } finally { saving.value = false }
}

function triggerAvatarUpload() { avatarInput.value?.click() }

async function uploadAvatar(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  const fd = new FormData()
  fd.append('file', file)
  try {
    await api.post('/users/avatar/update.json', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
    await auth.fetchCurrentUser()
    showSnackbar('Avatar updated')
  } catch (e) { console.error('Avatar upload failed:', e) }
}

async function removeAvatar() {
  try {
    await api.post('/users/avatar/remove.json')
    await auth.fetchCurrentUser()
    showSnackbar('Avatar removed')
  } catch (e) { console.error('Avatar removal failed:', e) }
}

async function changePassword() {
  changing.value = true
  try {
    await api.post('/users/password/change.json', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    })
    showPasswordDialog.value = false
    showSnackbar('Password changed successfully')
  } catch (e) { console.error('Password change failed:', e) }
  finally { changing.value = false }
}

async function toggle2FA() {
  // Stub: would call 2FA enable/disable API
  showSnackbar('2FA toggle not implemented yet')
}

async function fetchTokens() {
  tokens.value = await api.get<Token[]>('/tokens/list.json')
}

async function generateToken() {
  generating.value = true
  try {
    const endpoint = tokenForm.type === 'api' ? '/tokens/generate/api.json' : '/tokens/generate/mcp.json'
    const resp = await api.post(endpoint, {
      expiresInSeconds: tokenForm.expiresIn,
      password: tokenForm.password,
    })
    newTokenValue.value = resp.token
    showTokenDialog.value = false
    showNewTokenDialog.value = true
    await fetchTokens()
  } catch (e) { console.error('Token generation failed:', e) }
  finally { generating.value = false }
}

function copyToken() {
  navigator.clipboard.writeText(newTokenValue.value)
  showSnackbar('Token copied to clipboard')
}

async function revokeToken(token: Token) {
  try {
    await api.post('/tokens/revoke.json', { tokenId: token.tokenId, password: '' })
    await fetchTokens()
    showSnackbar('Token revoked')
  } catch (e) { console.error('Token revocation failed:', e) }
}

function confirmClear(type: string) {
  clearType.value = type
  clearPassword.value = ''
  showClearDialog.value = true
}

async function executeClear() {
  clearing.value = true
  try {
    const endpoint = clearType.value === 'all' ? '/data/clear/all.json' : '/data/clear/transactions.json'
    await api.post(endpoint, { password: clearPassword.value })
    showClearDialog.value = false
    showSnackbar(`${clearType.value === 'all' ? 'All data' : 'Transactions'} cleared`)
    await fetchData()
  } catch (e) { console.error('Clear failed:', e) }
  finally { clearing.value = false }
}

function exportData(format: string) {
  window.open(`/api/v1/data/export.${format}`, '_blank')
}

function showSnackbar(msg: string) {
  // Would use Vuetify snackbar in real app
  console.log(msg)
}

onMounted(async () => {
  await auth.fetchCurrentUser()
  await fetchData()
  await fetchTokens()
})
</script>