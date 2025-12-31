<template>
  <div class="page admin-orders">
    <header class="page-header">
      <div>
        <p class="eyebrow">后台</p>
        <h1>订单管理</h1>
        <p class="page-subtitle">快速筛选、浏览与分页；保持数据整洁一致。</p>
      </div>
    </header>

    <section class="card panel">
      <div class="filters">
        <el-form :inline="true" :model="filters" class="filters-form">
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
    </section>

    <section class="card panel">
      <el-table :data="orders" style="width: 100%" size="large" v-if="orders.length">
        <el-table-column prop="orderNo" label="订单号" min-width="160" />
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column prop="status" label="状态" width="140">
          <template #default="scope">
            <el-tag :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payAmount" label="金额" width="120" />
        <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      </el-table>
      <div v-else class="empty">暂无订单</div>

      <div class="table-footer">
        <div class="muted">共 {{ total }} 条</div>
        <el-pagination
          layout="prev, pager, next"
          :total="total"
          :page-size="size"
          :current-page="page + 1"
          @current-change="handlePage"
        />
      </div>
    </section>
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

const statusLabel = (status) => {
  const map = {
    PENDING_PAY: '待支付',
    PAID: '已支付',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

const statusType = (status) => {
  switch (status) {
    case 'PENDING_PAY':
      return 'warning'
    case 'PAID':
      return 'success'
    case 'SHIPPED':
      return 'info'
    case 'COMPLETED':
      return 'success'
    case 'CANCELLED':
      return 'danger'
    default:
      return 'info'
  }
}

onMounted(load)
</script>

<style scoped>
.admin-orders {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.panel {
  padding: 14px 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filters-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 16px;
  align-items: center;
}

.table-footer {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty {
  text-align: center;
  color: var(--muted);
  padding: 24px 0;
}

@media (max-width: 768px) {
  .table-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
