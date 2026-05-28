# Season 6 Planning — PWA Support

**Started**: 2026-05-28

---

## ✅ Completed

### PWA Support
- [x] `@vite-pwa/nuxt` module added
- [x] `nuxt.config.ts` PWA configuration
- [x] `public/manifest.json` — app manifest
- [x] `public/sw.js` — service worker
- [x] `public/icons/` — app icons (placeholder PNGs)
- [x] `composables/useOffline.ts` — offline queue store
- [x] `package.json` — PWA dependency

---

## Features Enabled

| Feature | Description |
|---------|-------------|
| **Installable** | Add to home screen from browser |
| **Offline Mode** | App works without internet |
| **Fast Loading** | Service worker caches assets |
| **App Shell** | Instant load with cached shell |

---

## Usage

### Install on Mobile
1. Open site in Chrome/Safari
2. Tap "Add to Home Screen"
3. App appears as native-like icon

### Offline Behavior
- Pages load from cache
- API calls fail gracefully
- Transactions queued locally
- Auto-sync when online

---

## Remaining

| Task | Priority | Notes |
|------|----------|-------|
| **Proper Icons** | Medium | Generate real app icons |
| **Full Offline Sync** | High | Implement transaction sync |
| **Push Notifications** | Low | Budget alerts |

---

## Files Created

```
frontend/
├── public/
│   ├── manifest.json
│   ├── sw.js
│   └── icons/
│       └── icon-*.png (8 sizes)
├── composables/
│   └── useOffline.ts
├── nuxt.config.ts
└── package.json
```

---

## Next: Scheduled Transactions

See [SCHEDULED-TRANSACTIONS.md](SCHEDULED-TRANSACTIONS.md) for next feature.