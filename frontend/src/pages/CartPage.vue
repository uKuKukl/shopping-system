<template>
  <div class="page">
    <div class="section-title">我的购物车</div>
    <div class="card" style="padding: 16px;">
      <el-table v-if="cart?.items?.length" :data="cart.items" style="width: 100%">
        <el-table-column label="选择" width="80">
          <template #default="scope">
            <el-checkbox v-model="scope.row.checked" @change="toggleChecked(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="price" label="单价" />
        <el-table-column label="数量" width="160">
          <template #default="scope">
            <el-input-number v-model="scope.row.quantity" :min="1" @change="qty => updateQty(scope.row, qty)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="danger" size="small" @click="removeItem(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-else class="muted">购物车为空</div>
      <div style="display: flex; justify-content: space-between; margin-top: 16px;">
        <div class="muted">合计：¥{{ cart?.totalAmount || 0 }}</div>
        <el-button type="primary" :disabled="!cart?.items?.length" @click="goCheckout">去结算</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'

const cartStore = useCartStore()
const router = useRouter()
const cart = computed(() => cartStore.cart)

const load = () => cartStore.fetchCart()

const updateQty = (row, qty) => {
  cartStore.updateItem(row.id, { quantity: qty, checked: row.checked })
}

const toggleChecked = (row) => {
  cartStore.updateItem(row.id, { checked: row.checked })
}

const removeItem = (row) => {
  cartStore.deleteItem(row.id)
}

const goCheckout = () => {
  router.push('/checkout')
}

onMounted(load)
</script>
