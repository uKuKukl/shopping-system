<template>
  <div class="page">
    <header class="page-header">
      <div>
        <p class="eyebrow">订单</p>
        <h1>订单详情</h1>
        <p class="page-subtitle">订单号：{{ order?.orderNo || '加载中' }}</p>
      </div>
    </header>
    <div class="card" style="padding: 16px;">
      <div class="muted">订单号：{{ order?.orderNo }}</div>
      <div class="muted">状态：{{ order?.status }}</div>
      <div class="muted">收货人：{{ order?.receiverName }} {{ order?.receiverPhone }}</div>
      <div class="muted">地址：{{ order?.receiverAddress }}</div>
      <div style="margin-top: 12px; font-weight: 700;">金额：¥{{ order?.payAmount }}</div>
      <el-button v-if="order?.status === 'PENDING_PAY'" type="primary" @click="goPay">去支付</el-button>
      <el-divider />
      <div class="section-title" style="font-size: 18px;">商品清单</div>
      <div v-for="item in order?.items || []" :key="item.id" style="margin-bottom: 8px;">
        {{ item.productName }} × {{ item.quantity }} (¥{{ item.unitPrice }})
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../utils/request'

const route = useRoute()
const router = useRouter()
const order = ref(null)

const load = async () => {
  order.value = await api.get(`/api/orders/${route.params.orderNo}`)
}

const goPay = () => {
  router.push(`/pay/${order.value.orderNo}`)
}

onMounted(load)
</script>
