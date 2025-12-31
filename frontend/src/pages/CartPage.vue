<template>
  <div class="page cart-page">
    <header class="page-header">
      <div>
        <p class="eyebrow">购物车</p>
        <h1>我的购物车</h1>
        <p class="muted">检查商品、调整数量，随时去结算。</p>
      </div>
      <div class="total-pill">
        <span class="label">合计</span>
        <span class="value">¥{{ cart?.totalAmount || 0 }}</span>
      </div>
    </header>

    <section class="card panel">
      <el-table
        v-if="cart?.items?.length"
        :data="cart.items"
        style="width: 100%"
        size="large"
      >
        <el-table-column label="选择" width="80">
          <template #default="scope">
            <el-checkbox v-model="scope.row.checked" @change="toggleChecked(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品" min-width="200" />
        <el-table-column prop="price" label="单价" width="120" />
        <el-table-column label="数量" width="160">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.quantity"
              :min="1"
              @change="qty => updateQty(scope.row, qty)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="danger" size="small" @click="removeItem(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-else class="empty">
        <div class="empty-title">购物车为空</div>
        <p class="muted">去首页挑选一些商品吧。</p>
      </div>

      <div class="footer-bar">
        <div class="muted">共 {{ cart?.items?.length || 0 }} 件 · 合计 ¥{{ cart?.totalAmount || 0 }}</div>
        <el-button type="primary" :disabled="!cart?.items?.length" @click="goCheckout">去结算</el-button>
      </div>
    </section>
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

<style scoped>
.cart-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: linear-gradient(120deg, rgba(43, 185, 169, 0.08), rgba(124, 194, 255, 0.08));
  box-shadow: var(--shadow);
}

.page-header h1 {
  margin: 6px 0 4px;
  font-size: 24px;
  letter-spacing: -0.01em;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  color: var(--muted);
}

.total-pill {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid var(--border);
  box-shadow: 0 10px 30px rgba(14, 26, 43, 0.08);
}

.total-pill .label {
  color: var(--muted);
  font-size: 13px;
}

.total-pill .value {
  font-weight: 700;
  color: var(--accent);
}

.panel {
  padding: 14px 16px 18px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 6px;
  padding-top: 12px;
  border-top: 1px solid var(--border);
}

.empty {
  text-align: center;
  padding: 24px 12px;
}

.empty-title {
  font-weight: 700;
  margin-bottom: 6px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .footer-bar {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style>
