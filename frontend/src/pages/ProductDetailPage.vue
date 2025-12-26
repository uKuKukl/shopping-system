<template>
  <div class="page">
    <div class="card" style="padding: 24px;">
      <el-row :gutter="24">
        <el-col :xs="24" :md="10">
          <div class="card" style="padding: 12px;">
            <img v-if="images.length" :src="images[0].imageUrl" style="width: 100%; border-radius: 12px;" />
            <div v-else class="muted">暂无图片</div>
          </div>
        </el-col>
        <el-col :xs="24" :md="14">
          <div class="section-title">{{ product?.name }}</div>
          <div class="muted" style="margin-bottom: 12px;">{{ product?.description || '暂无描述' }}</div>
          <div style="font-size: 24px; font-weight: 700; color: var(--accent);">¥{{ product?.price }}</div>
          <div class="muted" style="margin: 8px 0;">库存：{{ product?.stock }}</div>
          <div style="display: flex; gap: 12px; align-items: center;">
            <el-input-number v-model="quantity" :min="1" />
            <el-button type="primary" @click="addToCart">加入购物车</el-button>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import api from '../utils/request'
import { useCartStore } from '../stores/cart'
import { ElMessage } from 'element-plus'

const route = useRoute()
const cart = useCartStore()
const product = ref(null)
const images = ref([])
const quantity = ref(1)

const loadDetail = async () => {
  const data = await api.get(`/api/products/${route.params.id}`)
  product.value = data.product
  images.value = data.images || []
}

const addToCart = async () => {
  await cart.addItem({ productId: product.value.id, quantity: quantity.value })
  ElMessage.success('已加入购物车')
}

onMounted(loadDetail)
</script>
