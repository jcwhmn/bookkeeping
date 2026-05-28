/**
 * Offline support composable for Bookkeeping
 * Provides local storage queue for offline transactions
 */

import { defineStore } from 'pinia'

export interface OfflineTransaction {
  id: string
  type: 'create' | 'update' | 'delete'
  data: any
  timestamp: number
}

export const useOfflineStore = defineStore('offline', {
  state: () => ({
    queue: [] as OfflineTransaction[],
    isOnline: true,
    lastSync: null as number | null,
  }),

  getters: {
    pendingCount: (state) => state.queue.length,
    hasPending: (state) => state.queue.length > 0,
  },

  actions: {
    // Add transaction to offline queue
    queueTransaction(tx: Omit<OfflineTransaction, 'id' | 'timestamp'>) {
      const item: OfflineTransaction = {
        ...tx,
        id: crypto.randomUUID(),
        timestamp: Date.now(),
      }
      this.queue.push(item)
      this.saveQueue()
      return item.id
    },

    // Remove transaction from queue after successful sync
    dequeue(id: string) {
      this.queue = this.queue.filter(t => t.id !== id)
      this.saveQueue()
    },

    // Clear entire queue
    clearQueue() {
      this.queue = []
      this.saveQueue()
    },

    // Save queue to localStorage
    saveQueue() {
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem('bookkeeping_offline_queue', JSON.stringify(this.queue))
      }
    },

    // Load queue from localStorage
    loadQueue() {
      if (typeof localStorage !== 'undefined') {
        const saved = localStorage.getItem('bookkeeping_offline_queue')
        if (saved) {
          try {
            this.queue = JSON.parse(saved)
          } catch (e) {
            this.queue = []
          }
        }
      }
    },

    // Sync all pending transactions
    async syncPending(api: any) {
      if (!this.isOnline || this.queue.length === 0) return

      const toSync = [...this.queue]
      const synced: string[] = []
      const errors: { id: string; error: string }[] = []

      for (const tx of toSync) {
        try {
          switch (tx.type) {
            case 'create':
              await api.post('/transactions', tx.data)
              break
            case 'update':
              await api.put(`/transactions/${tx.data.id}`, tx.data)
              break
            case 'delete':
              await api.delete(`/transactions/${tx.data.id}`)
              break
          }
          synced.push(tx.id)
        } catch (e: any) {
          errors.push({ id: tx.id, error: e.message })
        }
      }

      // Remove synced items
      synced.forEach(id => this.dequeue(id))

      this.lastSync = Date.now()
      return { synced: synced.length, errors }
    },

    // Set online status
    setOnline(status: boolean) {
      this.isOnline = status
      if (status && this.queue.length > 0) {
        // Trigger sync when coming back online
        this.syncPending
      }
    },
  },
})

// Composable for using offline store
export function useOffline() {
  const store = useOfflineStore()
  
  // Initialize on mount
  if (typeof window !== 'undefined') {
    store.loadQueue()
    
    window.addEventListener('online', () => store.setOnline(true))
    window.addEventListener('offline', () => store.setOnline(false))
  }

  return {
    queue: computed(() => store.queue),
    pendingCount: computed(() => store.pendingCount),
    hasPending: computed(() => store.hasPending),
    isOnline: computed(() => store.isOnline),
    queueTransaction: store.queueTransaction,
    dequeue: store.dequeue,
    syncPending: (api: any) => store.syncPending(api),
  }
}