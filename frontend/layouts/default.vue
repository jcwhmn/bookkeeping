<!-- layouts/default.vue — Designed header bar -->
<template>
  <v-app>
    <v-app-bar color="white" elevation="1" density="compact" v-if="auth.isAuthenticated">
      <template v-slot:prepend>
        <v-icon color="primary" size="28" class="ml-2">mdi-calculator-variant</v-icon>
        <v-app-bar-title class="font-weight-bold text-primary">Bookkeeping</v-app-bar-title>
      </template>

      <v-btn variant="text" to="/" active-class="text-primary" class="text-none">Dashboard</v-btn>
      <v-btn variant="text" to="/transactions" active-class="text-primary" class="text-none">Transactions</v-btn>
      <v-btn variant="text" to="/accounts" active-class="text-primary" class="text-none">Accounts</v-btn>
      <v-btn variant="text" to="/categories" active-class="text-primary" class="text-none">Categories</v-btn>
      <v-btn variant="text" to="/tags" active-class="text-primary" class="text-none">Tags</v-btn>
      <v-btn variant="text" to="/statistics" active-class="text-primary" class="text-none">Statistics</v-btn>
      <v-btn variant="text" to="/budgets" active-class="text-primary" class="text-none">Budgets</v-btn>
      <v-btn variant="text" to="/templates" active-class="text-primary" class="text-none">Templates</v-btn>
      <v-btn variant="text" to="/reports" active-class="text-primary" class="text-none">Reports</v-btn>
      <v-btn variant="text" to="/exchange" active-class="text-primary" class="text-none">Exchange</v-btn>
      <v-btn variant="text" to="/insights" active-class="text-primary" class="text-none">Insights</v-btn>
      <v-btn variant="text" to="/about" active-class="text-primary" class="text-none">About</v-btn>

      <v-spacer />

      <v-btn variant="text" size="small" class="text-none text-grey-darken-1 mr-2" to="/app-settings">
        <v-icon start size="18">mdi-cog-outline</v-icon>Settings
      </v-btn>

      <v-spacer />

      <v-btn variant="text" size="small" class="text-none text-grey-darken-1 mr-2">
        <v-icon start size="18">mdi-currency-usd</v-icon>USD
      </v-btn>

      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn icon v-bind="props" variant="text">
            <v-avatar color="primary" size="32">
              <span class="text-white text-caption">{{ (auth.user?.nickname || auth.user?.username || '?')[0]?.toUpperCase() }}</span>
            </v-avatar>
          </v-btn>
        </template>
        <v-list density="compact" width="180">
          <v-list-item prepend-icon="mdi-account-outline" to="/profile" title="Profile" />
          <v-divider />
          <v-list-item prepend-icon="mdi-logout" title="Sign Out" @click="auth.logout()" />
        </v-list>
      </v-menu>
    </v-app-bar>

    <v-main class="bg-grey-lighten-4">
      <v-container style="max-width: 1200px" class="pa-6">
        <slot />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
const auth = useAuthStore()
</script>
