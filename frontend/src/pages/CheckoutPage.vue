<template>
  <div class="page">
    <header class="page-header">
      <div>
        <p class="eyebrow">结算</p>
        <h1>结算</h1>
        <p class="page-subtitle">填写收货信息，确认订单明细。</p>
      </div>
    </header>
    <el-row :gutter="16">
      <el-col :xs="24" :md="14">
        <div class="card" style="padding: 16px;">
          <el-form :model="form" label-position="top">
            <el-form-item label="收货人">
              <el-input v-model="form.receiverName" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="form.receiverPhone" />
            </el-form-item>
            <el-form-item label="收货地址">
              <el-input v-model="form.receiverAddress" type="textarea" />
            </el-form-item>
            <el-button type="primary" @click="submitOrder">提交订单</el-button>
          </el-form>
        </div>
      </el-col>
      <el-col :xs="24" :md="10">
        <div class="card" style="padding: 16px;">
          <div class="section-title" style="font-size: 18px;">订单明细</div>
          <div v-for="item in checkedItems" :key="item.id" style="margin-bottom: 8px;">
            <div>{{ item.productName }} × {{ item.quantity }}</div>
          </div>
          <div style="margin-top: 12px; font-weight: 700;">合计：¥{{ cart?.totalAmount || 0 }}</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/request'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const cartStore = useCartStore()
const cart = computed(() => cartStore.cart)
const checkedItems = computed(() => (cart.value?.items || []).filter(i => i.checked))
const form = reactive({ receiverName: '', receiverPhone: '', receiverAddress: '' })

const load = () => cartStore.fetchCart()

const submitOrder = async () => {
  const data = await api.post('/api/orders', form)
  router.push(`/pay/${data.orderNo}`)
}

onMounted(load)
</script>
