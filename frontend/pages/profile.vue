<!-- pages/profile.vue -->
<template>
  <div style="max-width: 1200px; margin: 0 auto">
    <h1 class="text-h4 mb-6">Profile</h1>

    <v-row justify="center">
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>User Profile</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="handleUpdate">
              <v-text-field
                v-model="form.email"
                label="Email"
                variant="outlined"
                :rules="[required, emailRule]"
              />

              <v-text-field
                v-model="form.nickname"
                label="Nickname"
                variant="outlined"
              />

              <v-text-field
                v-model="form.defaultCurrency"
                label="Default Currency"
                variant="outlined"
                maxlength="3"
              />

              <v-text-field
                v-model="form.language"
                label="Language"
                variant="outlined"
                maxlength="10"
              />

              <v-btn
                type="submit"
                color="primary"
                :loading="saving"
                block
                class="mt-4"
              >
                Save Changes
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })

const auth = useAuthStore()
const api = useApi()
const saving = ref(false)

const required = (v: string) => !!v || 'Required'
const emailRule = (v: string) => /.+@.+\..+/.test(v) || 'Invalid email'

const form = reactive({
  email: '',
  nickname: '',
  defaultCurrency: 'USD',
  language: 'en-US',
})

onMounted(async () => {
  await auth.fetchCurrentUser()
  if (auth.user) {
    form.email = auth.user.email
    form.nickname = auth.user.nickname
    form.defaultCurrency = auth.user.defaultCurrency
    form.language = auth.user.language
  }
})

async function handleUpdate() {
  saving.value = true
  try {
    await api.put('/users/me', {
      email: form.email,
      nickname: form.nickname,
      defaultCurrency: form.defaultCurrency,
      language: form.language,
    })
    await auth.fetchCurrentUser()
  } finally {
    saving.value = false
  }
}
</script>
