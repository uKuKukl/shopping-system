import { defineStore } from 'pinia'
import api from '../utils/request'

export const useCartStore = defineStore('cart', {
  state: () => ({
    cart: null
  }),
  actions: {
    async fetchCart() {
      this.cart = await api.get('/api/cart')
    },
    async addItem(payload) {
      this.cart = await api.post('/api/cart/items', payload)
    },
    async updateItem(itemId, payload) {
      this.cart = await api.patch(`/api/cart/items/${itemId}`, payload)
    },
    async deleteItem(itemId) {
      this.cart = await api.delete(`/api/cart/items/${itemId}`)
    }
  }
})
