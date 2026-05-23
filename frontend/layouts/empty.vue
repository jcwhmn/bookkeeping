<!-- layouts/empty.vue — No app bar for login/register -->
<template>
  <v-app>
    <v-main class="bg-grey-lighten-4">
      <v-container class="fill-height d-flex align-center justify-center" style="max-width: 520px">
        <slot />
      </v-container>
    </v-main>
  </v-app>
</template>