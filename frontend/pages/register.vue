<!-- pages/register.vue — Designed register page -->
<template>
  <v-app>
    <v-main class="bg-grey-lighten-4">
      <v-container class="fill-height d-flex align-center justify-center">
        <v-card width="420" class="pa-8 rounded-lg" elevation="3">
          <div class="text-center mb-6">
            <v-icon size="48" color="primary" class="mb-2">mdi-account-plus-outline</v-icon>
            <h1 class="text-h4 font-weight-bold text-primary">Create Account</h1>
            <p class="text-body-2 text-grey-darken-1 mt-1">Start tracking your finances</p>
          </div>

          <v-alert v-if="error" type="error" variant="tonal" closable density="compact" class="mb-4" @click:close="error = ''">
            {{ error }}
          </v-alert>

          <v-form @submit.prevent="handleRegister">
            <v-text-field v-model="username" label="Username" variant="outlined" density="comfortable"
              prepend-inner-icon="mdi-account-outline" :rules="[required, minLen(3)]" autocomplete="username" bg-color="white" class="mb-3" />
            <v-text-field v-model="email" label="Email" variant="outlined" density="comfortable"
              prepend-inner-icon="mdi-email-outline" :rules="[required, emailRule]" autocomplete="email" bg-color="white" class="mb-3" />
            <v-text-field v-model="password" label="Password" type="password" variant="outlined" density="comfortable"
              prepend-inner-icon="mdi-lock-outline" :rules="[required, minLen(6)]" autocomplete="new-password" bg-color="white" class="mb-4" />

            <v-btn type="submit" color="primary" size="large" block :loading="loading" rounded="lg"
              class="text-none font-weight-bold mb-4" height="48">Register</v-btn>
          </v-form>

          <v-btn variant="outlined" size="large" block rounded="lg" class="text-none" height="48" to="/login">
            Back to Sign In
          </v-btn>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'empty' })
const auth = useAuthStore()
const router = useRouter()
const loading = ref(false)
const error = ref('')
const username = ref('')
const email = ref('')
const password = ref('')
const required = (v: string) => !!v || 'Required'
const minLen = (n: number) => (v: string) => v?.length >= n || `Min ${n} characters`
const emailRule = (v: string) => /.+@.+\..+/.test(v) || 'Invalid email'

async function handleRegister() {
  if (!username.value || !email.value || !password.value) return
  loading.value = true; error.value = ''
  try {
    await auth.register(username.value, email.value, password.value)
    await router.push('/login')
  } catch (e: any) { error.value = e?.statusMessage || 'Registration failed' }
  finally { loading.value = false }
}
</script>
