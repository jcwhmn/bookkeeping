// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-05-19',

  devtools: { enabled: true },

  modules: [
    '@nuxtjs/i18n',
    '@pinia/nuxt',
    '@vite-pwa/nuxt',
  ],

  css: [
    'vuetify/styles',
    '@mdi/font/css/materialdesignicons.css',
    '~/assets/css/design-tokens.css',
    '~/assets/css/global.css',
  ],

  app: {
    head: {
      link: [
        {
          rel: 'preconnect',
          href: 'https://fonts.googleapis.com',
        },
        {
          rel: 'preconnect',
          href: 'https://fonts.gstatic.com',
          crossorigin: '',
        },
        {
          rel: 'stylesheet',
          href: 'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap',
        },
      ],
    },
  },

  build: {
    transpile: ['vuetify'],
  },

  vite: {
    define: {
      'process.env.DEBUG': false,
    },
  },

  i18n: {
    defaultLocale: 'en-US',
    lazy: false,
    langDir: null as any,
    locales: [
      { code: 'en-US', iso: 'en-US', name: 'English' },
      { code: 'zh-CN', iso: 'zh-CN', name: '中文' },
    ],
    strategy: 'no_prefix',
    detectBrowserLanguage: {
      useCookie: true,
      cookieKey: 'locale',
    },
  },

  runtimeConfig: {
    public: {
      apiBase: 'http://localhost:8080/api/v1',
    },
  },

  // PWA Configuration
  pwa: {
    registerType: 'autoUpdate',
    manifest: {
      name: 'Bookkeeping',
      short_name: 'Bookkeeping',
      description: 'Personal bookkeeping application',
      theme_color: '#533afd',
      background_color: '#ffffff',
      display: 'standalone',
      orientation: 'portrait-primary',
      start_url: '/',
      icons: [
        {
          src: '/icons/icon-192x192.png',
          sizes: '192x192',
          type: 'image/png',
        },
        {
          src: '/icons/icon-512x512.png',
          sizes: '512x512',
          type: 'image/png',
        },
      ],
    },
    workbox: {
      navigateFallback: '/',
      globPatterns: ['**/*.{js,css,html,png,svg,ico}'],
      runtimeCaching: [
        {
          urlPattern: /^https:\/\/fonts\.googleapis\.com\/.*/i,
          handler: 'CacheFirst',
          options: {
            cacheName: 'google-fonts-cache',
            expiration: {
              maxEntries: 10,
              maxAgeSeconds: 60 * 60 * 24 * 365, // 1 year
            },
          },
        },
        {
          urlPattern: /^https:\/\/fonts\.gstatic\.com\/.*/i,
          handler: 'CacheFirst',
          options: {
            cacheName: 'gstatic-fonts-cache',
            expiration: {
              maxEntries: 10,
              maxAgeSeconds: 60 * 60 * 24 * 365,
            },
          },
        },
        {
          urlPattern: /\/api\/v1\/.*/i,
          handler: 'NetworkFirst',
          options: {
            cacheName: 'api-cache',
            expiration: {
              maxEntries: 50,
              maxAgeSeconds: 60 * 60 * 24, // 1 day
            },
            networkTimeoutSeconds: 10,
          },
        },
      ],
    },
    client: {
      installPrompt: true,
    },
    // Disable PWA service worker generation in dev mode.
    // workbox-build is CommonJS and cannot be dynamically require()'d in
    // an ESM context (Nuxt 4 + Vite 7), causing "Dynamic require of
    // 'workbox-build' is not supported" errors during `nuxt dev`.
    // PWA is still generated in production builds (`nuxt build`).
    devOptions: {
      enabled: false,
    },
  },
})