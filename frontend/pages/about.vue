<!-- pages/about.vue — About Page -->
<template>
  <div style="max-width: 800px; margin: 0 auto">
    <h1 class="text-h4 font-weight-bold mb-6">About</h1>

    <!-- App Info Card -->
    <v-card class="mb-4 rounded-lg" elevation="2">
      <v-card-text class="text-center pa-8">
        <v-avatar size="80" color="primary" class="mb-4">
          <v-icon size="48" color="white">mdi-calculator-variant</v-icon>
        </v-avatar>
        <div class="text-h5 font-weight-bold mb-2">ezBookkeeping</div>
        <div class="text-body-1 text-grey mb-1">Version {{ appVersion }}</div>
        <div class="text-caption text-grey">Built with Nuxt 4 + Vuetify 3</div>
      </v-card-text>
    </v-card>

    <!-- Backend Status -->
    <v-card class="mb-4 rounded-lg" elevation="1">
      <v-card-title>System Status</v-card-title>
      <v-card-text>
        <v-list>
          <v-list-item>
            <template v-slot:prepend>
              <v-icon :color="backendStatus === 'connected' ? 'success' : 'error'">
                {{ backendStatus === 'connected' ? 'mdi-check-circle' : 'mdi-alert-circle' }}
              </v-icon>
            </template>
            <v-list-item-title>Backend API</v-list-item-title>
            <v-list-item-subtitle>{{ backendUrl }}</v-list-item-subtitle>
            <template v-slot:append>
              <v-chip :color="backendStatus === 'connected' ? 'success' : 'error'" size="small">
                {{ backendStatus === 'connected' ? 'Connected' : 'Disconnected' }}
              </v-chip>
            </template>
          </v-list-item>
          <v-list-item>
            <template v-slot:prepend>
              <v-icon color="success">mdi-check-circle</v-icon>
            </template>
            <v-list-item-title>Database</v-list-item-title>
            <v-list-item-subtitle>PostgreSQL</v-list-item-subtitle>
            <template v-slot:append>
              <v-chip color="success" size="small">Connected</v-chip>
            </template>
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>

    <!-- Quick Links -->
    <v-card class="mb-4 rounded-lg" elevation="1">
      <v-card-title>Quick Links</v-card-title>
      <v-card-text>
        <v-list>
          <v-list-item href="https://github.com/jcwhmn/bookkeeping" target="_blank">
            <template v-slot:prepend>
              <v-icon color="grey-darken-1">mdi-github</v-icon>
            </template>
            <v-list-item-title>GitHub Repository</v-list-item-title>
            <template v-slot:append>
              <v-icon size="small">mdi-open-in-new</v-icon>
            </template>
          </v-list-item>
          <v-list-item href="https://github.com/jcwhmn/bookkeeping/issues" target="_blank">
            <template v-slot:prepend>
              <v-icon color="grey-darken-1">mdi-bug</v-icon>
            </template>
            <v-list-item-title>Report an Issue</v-list-item-title>
            <template v-slot:append>
              <v-icon size="small">mdi-open-in-new</v-icon>
            </template>
          </v-list-item>
          <v-list-item href="https://github.com/jcwhmn/bookkeeping#readme" target="_blank">
            <template v-slot:prepend>
              <v-icon color="grey-darken-1">mdi-book-open-variant</v-icon>
            </template>
            <v-list-item-title>Documentation</v-list-item-title>
            <template v-slot:append>
              <v-icon size="small">mdi-open-in-new</v-icon>
            </template>
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>

    <!-- Technology Stack -->
    <v-card class="mb-4 rounded-lg" elevation="1">
      <v-card-title>Technology Stack</v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="6" sm="4" v-for="tech in techStack" :key="tech.name">
            <v-card class="pa-3 text-center" elevation="0" color="grey-lighten-4" rounded-lg>
              <div class="text-subtitle-2 font-weight-bold">{{ tech.name }}</div>
              <div class="text-caption text-grey">{{ tech.version }}</div>
            </v-card>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Licenses -->
    <v-card class="rounded-lg" elevation="1">
      <v-card-title>Open Source Licenses</v-card-title>
      <v-card-text>
        <v-list density="compact">
          <v-list-item v-for="license in licenses" :key="license.name">
            <template v-slot:prepend>
              <v-icon size="small" color="grey">mdi-file-document</v-icon>
            </template>
            <v-list-item-title class="text-body-2">{{ license.name }}</v-list-item-title>
            <v-list-item-subtitle class="text-caption">{{ license.license }}</v-list-item-subtitle>
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>

    <!-- Version Check -->
    <v-card class="mt-4 rounded-lg" elevation="0" color="grey-lighten-4">
      <v-card-text class="text-center text-body-2 text-grey">
        <v-icon size="small" class="mr-1">mdi-clock-outline</v-icon>
        Last checked: {{ lastCheck }}
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
const api = useApi()

const appVersion = ref('1.0.0')
const backendStatus = ref('checking')
const backendUrl = ref('/api/v1')
const lastCheck = ref(new Date().toLocaleString())

const techStack = [
  { name: 'Nuxt 4', version: '4.4.6' },
  { name: 'Vue 3', version: '3.5.34' },
  { name: 'Vuetify 3', version: '4.0.7' },
  { name: 'Pinia', version: '3.0.4' },
  { name: 'ECharts', version: '6.1.0' },
  { name: 'Spring Boot', version: '4.0.6' },
  { name: 'PostgreSQL', version: '17+' },
  { name: 'Flyway', version: '11.4.1' },
  { name: 'JWT', version: '0.12.6' },
]

const licenses = [
  { name: 'MIT License', license: 'ezBookkeeping' },
  { name: 'Apache 2.0', license: 'Spring Boot' },
  { name: 'MIT', license: 'Vue, Nuxt, Vuetify' },
  { name: 'Apache 2.0', license: 'ECharts' },
]

onMounted(async () => {
  try {
    const resp = await api.get<{version: string}>('/health')
    backendStatus.value = 'connected'
    if (resp.version) appVersion.value = resp.version
  } catch {
    backendStatus.value = 'disconnected'
  }
  lastCheck.value = new Date().toLocaleString()
})
</script>