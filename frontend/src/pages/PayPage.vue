<template>
  <div class="page">
    <div class="section-title">支付订单</div>
    <el-row :gutter="16">
      <el-col :xs="24" :md="12">
        <div class="card" style="padding: 16px;">
          <div class="section-title" style="font-size: 18px;">订单信息</div>
          <div class="muted">订单号：{{ order?.orderNo }}</div>
          <div class="muted">状态：{{ order?.status }}</div>
          <div style="margin-top: 12px; font-size: 20px; font-weight: 700;">应付：¥{{ order?.payAmount }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :md="12">
        <div class="card" style="padding: 16px;">
          <div class="section-title" style="font-size: 18px;">模拟支付</div>
          <el-form :model="form" label-position="top">
            <el-form-item label="支付渠道">
              <el-select v-model="form.payChannel" placeholder="选择支付渠道">
                <el-option label="模拟" value="MOCK" />
                <el-option label="银行" value="BANK" />
              </el-select>
            </el-form-item>
            <el-form-item label="持卡人">
              <el-input v-model="form.cardHolder" />
            </el-form-item>
            <el-form-item label="卡号后四位">
              <el-input v-model="form.cardLast4" maxlength="4" />
            </el-form-item>
            <el-form-item>
              <el-switch v-model="form.simulateFail" active-text="模拟失败" />
            </el-form-item>
            <el-button type="primary" @click="pay">确认支付</el-button>
          </el-form>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const form = reactive({ payChannel: 'MOCK', cardHolder: '', cardLast4: '', simulateFail: false })

const loadOrder = async () => {
  order.value = await api.get(`/api/orders/${route.params.orderNo}`)
}

const pay = async () => {
  const payload = {
    orderNo: order.value.orderNo,
    payChannel: form.payChannel,
    cardHolder: form.cardHolder,
    cardLast4: form.cardLast4,
    simulateResult: form.simulateFail ? 'FAILED' : undefined
  }
  const data = await api.post('/api/payments', payload)
  if (data.payStatus === 'SUCCESS') {
    ElMessage.success('支付成功')
    router.push(`/orders/${order.value.orderNo}`)
  } else {
    ElMessage.error('支付失败，可重试')
  }
}

onMounted(loadOrder)
</script>
