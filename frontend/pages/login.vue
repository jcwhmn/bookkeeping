<!-- pages/login.vue — Designed login page -->
<template>
  <v-app>
    <v-main class="bg-grey-lighten-4">
      <v-container class="fill-height d-flex align-center justify-center">
        <v-card width="420" class="pa-8 rounded-lg" elevation="3">
          <!-- Logo & Title -->
          <div class="text-center mb-6">
            <v-icon size="48" color="primary" class="mb-2">mdi-calculator-variant</v-icon>
            <h1 class="text-h4 font-weight-bold text-primary">Bookkeeping</h1>
            <p class="text-body-2 text-grey-darken-1 mt-1">Personal finance, simplified</p>
          </div>

          <!-- Error -->
          <v-alert v-if="error" type="error" variant="tonal" closable density="compact" class="mb-4" @click:close="error = ''">
            {{ error }}
          </v-alert>

          <!-- Form -->
          <v-form @submit.prevent="handleLogin" ref="formRef">
            <v-text-field
              v-model="username"
              label="Username"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-account-outline"
              :rules="[required]"
              autocomplete="username"
              bg-color="white"
              class="mb-3"
            />

            <v-text-field
              v-model="password"
              label="Password"
              type="password"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-lock-outline"
              :append-inner-icon="showPwd ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="showPwd = !showPwd"
              :rules="[required]"
              autocomplete="current-password"
              bg-color="white"
              class="mb-4"
            />

            <v-btn
              type="submit"
              color="primary"
              size="large"
              block
              :loading="loading"
              rounded="lg"
              class="text-none font-weight-bold mb-4"
              height="48"
            >
              Sign In
            </v-btn>
          </v-form>

          <!-- Divider -->
          <div class="d-flex align-center mb-4">
            <v-divider />
            <span class="mx-3 text-body-2 text-grey">or</span>
            <v-divider />
          </div>

          <!-- Register -->
          <v-btn
            variant="outlined"
            size="large"
            block
            rounded="lg"
            class="text-none"
            height="48"
            to="/register"
          >
            Create an account
          </v-btn>

          <!-- Footer -->
          <div class="text-center mt-6">
            <v-btn variant="text" size="small" density="compact" class="text-grey text-caption">EN</v-btn>
            <span class="text-grey-lighten-1 text-caption mx-1">|</span>
            <v-btn variant="text" size="small" density="compact" class="text-grey text-caption">中文</v-btn>
            <div class="text-caption text-grey-lighten-1 mt-1">v0.1.0</div>
          </div>
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
const password = ref('')
const showPwd = ref(false)
const required = (v: string) => !!v || 'Required'

async function handleLogin() {
  if (!username.value || !password.value) return
  loading.value = true
  error.value = ''
  try {
    await auth.login(username.value, password.value)
    await router.push('/')
  } catch (e: any) {
    error.value = e?.statusMessage || 'Invalid username or password'
  } finally {
    loading.value = false
  }
}
</script>
