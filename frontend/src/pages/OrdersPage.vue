<template>
  <div class="page">
    <header class="page-header">
      <div>
        <p class="eyebrow">订单</p>
        <h1>我的订单</h1>
        <p class="page-subtitle">查看状态、取消未支付订单，或进入详情。</p>
      </div>
    </header>
    <div class="card" style="padding: 16px;">
      <el-table v-if="orders.length" :data="orders" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="payAmount" label="金额" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            <el-button v-if="scope.row.status === 'PENDING_PAY'" type="warning" size="small" @click="cancel(scope.row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-else class="muted">暂无订单</div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const orders = ref([])

const load = async () => {
  orders.value = await api.get('/api/orders')
}

const viewDetail = (order) => {
  router.push(`/orders/${order.orderNo}`)
}

const cancel = async (order) => {
  await api.post(`/api/orders/${order.orderNo}/cancel`)
  ElMessage.success('已取消订单')
  load()
}

onMounted(load)
</script>
