<template>
  <div class="page">
    <div class="section-title">订单管理</div>
    <div class="card" style="padding: 16px; margin-bottom: 16px;">
      <el-form :inline="true" :model="filters">
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="全部">
            <el-option label="待支付" value="PENDING_PAY" />
            <el-option label="已支付" value="PAID" />
            <el-option label="已发货" value="SHIPPED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="filters.userId" placeholder="用户ID" />
        </el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
      </el-form>
    </div>

    <div class="card" style="padding: 16px;">
      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="payAmount" label="金额" width="120" />
        <el-table-column prop="createdAt" label="创建时间" />
      </el-table>
      <div style="margin-top: 12px; text-align: right;">
        <el-pagination
          layout="prev, pager, next"
          :total="total"
          :page-size="size"
          :current-page="page + 1"
          @current-change="handlePage"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../../utils/request'

const orders = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)
const filters = reactive({ status: '', userId: '' })

const load = async () => {
  const params = {
    page: page.value,
    size: size.value,
    status: filters.status || undefined,
    userId: filters.userId || undefined
  }
  const data = await api.get('/api/admin/orders', { params })
  orders.value = data.content
  total.value = data.totalElements
}

const handlePage = (p) => {
  page.value = p - 1
  load()
}

onMounted(load)
</script>
