// plugins/vuetify.ts - Stripe-inspired theme for Bookkeeping
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/styles'

export default defineNuxtPlugin((nuxtApp) => {
  const vuetify = createVuetify({
    components,
    directives,
    theme: {
      defaultTheme: 'bookkeeping',
      themes: {
        bookkeeping: {
          dark: false,
          colors: {
            // Primary - Indigo
            primary: '#533afd',
            'primary-darken-1': '#4434d4',
            'primary-darken-2': '#2e2b8c',
            'primary-lighten-1': '#665efd',
            'primary-lighten-2': '#b9b9f9',

            // Secondary - Muted text
            secondary: '#64748d',
            'secondary-darken-1': '#273951',

            // Surface & Background
            background: '#ffffff',
            surface: '#f6f9fc',
            'surface-bright': '#ffffff',
            'surface-light': '#f6f9fc',
            'surface-variant': '#e3e8ee',

            // Semantic colors
            error: '#ea2261',
            success: '#1aae39',
            warning: '#9b6829',
            info: '#533afd',

            // Text colors
            'on-background': '#0d253d',
            'on-surface': '#0d253d',
            'on-primary': '#ffffff',
            'on-secondary': '#ffffff',
            'on-error': '#ffffff',
            'on-success': '#ffffff',
            'on-warning': '#ffffff',
            'on-info': '#ffffff',

            // Hairline/border
            'on-surface-variant': '#e3e8ee',

            // Outline (same as hairline for consistency)
            outline: '#e3e8ee',
            'outline-variant': '#e3e8ee',
          },
        },
      },
    },
    defaults: {
      // All buttons are pill-shaped (Stripe signature)
      VBtn: {
        rounded: 'pill',
        fontWeight: 400,
      },
      // Cards use hairline border instead of elevation
      VCard: {
        rounded: 'lg',
        elevation: 0,
        border: true,
      },
      // Text fields use outlined variant
      VTextField: {
        variant: 'outlined',
        density: 'comfortable',
        color: 'primary',
      },
      VSelect: {
        variant: 'outlined',
        density: 'comfortable',
        color: 'primary',
      },
      VTextarea: {
        variant: 'outlined',
        density: 'comfortable',
        color: 'primary',
      },
      // Form inputs rounded corners
      VTextField: {
        rounded: 'lg',
      },
      // Chips use pill shape
      VChip: {
        rounded: 'pill',
      },
      // Dialogs use rounded corners
      VDialog: {
        rounded: 'lg',
      },
      // Navigation drawer
      VNavigationDrawer: {
        rounded: 0,
      },
      // List items
      VList: {
        rounded: 'lg',
      },
      VListItem: {
        rounded: 'lg',
      },
    },
  })

  nuxtApp.vueApp.use(vuetify)
})