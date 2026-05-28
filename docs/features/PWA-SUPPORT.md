# PWA Support — Season 6 Feature

**Status**: ✅ Implemented  
**Date**: 2026-05-28

---

## Overview

Progressive Web App (PWA) support enables:
- **Installable** — Add to home screen on mobile
- **Offline capable** — Works without internet
- **Fast loading** — Service worker caching
- **Background sync** — Queue transactions when offline

---

## Implementation

### 1. PWA Module (`@vite-pwa/nuxt`)

Added to `nuxt.config.ts`:
```typescript
modules: ['@vite-pwa/nuxt'],
pwa: {
  manifest: { ... },
  workbox: { ... },
  client: { installPrompt: true },
}
```

### 2. Service Worker

`public/sw.js` provides:
- Static asset caching
- Network-first for API calls
- Offline fallback to cached pages
- Background sync for transactions

### 3. Offline Store

`composables/useOffline.ts` provides:
- Local queue for pending transactions
- Automatic sync when back online
- Online/offline status tracking

---

## Files Created

```
frontend/
├── public/
│   ├── manifest.json         [PWA manifest]
│   ├── sw.js                 [Service worker]
│   └── icons/                [App icons]
│       ├── icon-72x72.png
│       ├── icon-96x96.png
│       ├── icon-128x128.png
│       ├── icon-144x144.png
│       ├── icon-152x152.png
│       ├── icon-192x192.png
│       ├── icon-384x384.png
│       └── icon-512x512.png
├── composables/
│   └── useOffline.ts         [Offline store]
├── nuxt.config.ts            [PWA config]
└── package.json              [vite-pwa dep]
```

---

## Configuration

### Manifest (in nuxt.config.ts)
```typescript
manifest: {
  name: 'Bookkeeping',
  short_name: 'Bookkeeping',
  theme_color: '#1976D2',
  background_color: '#ffffff',
  display: 'standalone',
  orientation: 'portrait-primary',
  start_url: '/',
  icons: [...]
}
```

### Caching Strategy

| Resource | Strategy |
|----------|----------|
| JS/CSS/HTML | Cache on first load |
| API calls | NetworkFirst (10s timeout) |
| Fonts | CacheFirst (1 year) |
| Images | CacheFirst |

---

## Usage

### For Users

1. **Install on mobile**: Visit site → Menu → "Add to Home Screen"
2. **Offline mode**: App works without internet
3. **Sync**: Transactions sync when back online

### For Developers

```typescript
// Use offline store
const { queueTransaction, syncPending, hasPending } = useOffline()

// Queue a transaction when offline
if (!isOnline.value) {
  queueTransaction({ type: 'create', data: transaction })
}

// Sync when back online
await syncPending(api)
```

---

## Browser Support

| Feature | Chrome | Safari | Firefox | Edge |
|---------|--------|--------|---------|------|
| PWA Install | ✅ | ✅ (iOS 16.4+) | ✅ | ✅ |
| Service Worker | ✅ | ✅ | ✅ | ✅ |
| Background Sync | ✅ | ❌ | ⚠️ | ✅ |
| Web Push | ✅ | ⚠️ | ⚠️ | ✅ |

---

## Next Steps

- [ ] Generate proper app icons (currently placeholder)
- [ ] Implement full offline transaction sync
- [ ] Add push notification support
- [ ] Test on actual mobile devices