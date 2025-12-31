<template>
  <div class="page reports-page">
    <div class="page-header">
      <div>
        <p class="eyebrow">报表中心</p>
        <h1>每日经营概览</h1>
        <p class="muted">快速查看商品/品类表现、日度走势与热销榜，保持运营节奏。</p>
      </div>
    </div>

    <div class="grid grid-3">
      <section class="card panel">
        <header class="panel-head">
          <h3>商品销售</h3>
          <span class="tag">Top</span>
        </header>
        <div class="panel-list" v-if="productSales.length">
          <div class="row" v-for="row in productSales" :key="row.productId">
            <div class="name">{{ row.productName }}</div>
            <div class="meta">{{ row.totalQty }} 件 · ¥{{ row.totalAmount }}</div>
          </div>
        </div>
        <div v-else class="muted">暂无数据</div>
      </section>

      <section class="card panel">
        <header class="panel-head">
          <h3>分类销售</h3>
          <span class="tag soft">Overview</span>
        </header>
        <div class="panel-list" v-if="categorySales.length">
          <div class="row" v-for="row in categorySales" :key="row.categoryId">
            <div class="name">{{ row.categoryName }}</div>
            <div class="meta">{{ row.totalQty }} 件 · ¥{{ row.totalAmount }}</div>
          </div>
        </div>
        <div v-else class="muted">暂无数据</div>
      </section>

      <section class="card panel">
        <header class="panel-head">
          <h3>每日销售</h3>
          <span class="tag soft">趋势</span>
        </header>
        <div ref="chartRef" class="chart" v-if="dailySales.length"></div>
        <div v-else class="muted">暂无数据</div>
      </section>
    </div>

    <section class="card panel">
      <header class="panel-head">
        <div>
          <p class="eyebrow">热销榜</p>
          <h3>每日热销（Top {{ topN }}）</h3>
        </div>
      </header>
      <el-table v-if="dailyHotGroups.length" :data="dailyHotGroups" style="width: 100%" size="large">
        <el-table-column prop="paidDate" label="日期" width="140" />
        <el-table-column label="热销商品">
          <template #default="scope">
            <div class="hot-list">
              <div v-for="item in scope.row.items" :key="item.productId" class="hot-item">
                <span class="name">{{ item.productName }}</span>
                <span class="meta">{{ item.totalQty }} 件 · ¥{{ item.totalAmount }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div v-else class="muted">暂无数据</div>
    </section>

    <section class="card panel">
      <header class="panel-head">
        <div>
          <p class="eyebrow">明细</p>
          <h3>每日销售明细查询</h3>
        </div>
        <div class="filters">
          <el-date-picker
            v-model="detailDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
          <el-button type="primary" @click="loadDailyDetail">查询</el-button>
        </div>
      </header>

      <el-table
        v-if="dailySalesDetailWithSummary.length"
        :data="dailySalesDetailWithSummary"
        style="width: 100%"
        :row-class-name="rowClassName"
        size="large"
      >
        <el-table-column prop="paidDate" label="日期" width="140" />
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="totalQty" label="销量" width="100" />
        <el-table-column prop="orderCount" label="订单数" width="100" />
        <el-table-column prop="totalAmount" label="营业额" width="140" />
      </el-table>
      <div v-else class="muted">暂无数据</div>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import api from '../../utils/request'

const productSales = ref([])
const categorySales = ref([])
const dailySales = ref([])
const dailyHotProducts = ref([])
const topN = ref(3)
const dailySalesDetail = ref([])
const detailDate = ref('')
const chartRef = ref(null)
let chartInstance = null
let resizeBound = false

const load = async () => {
  productSales.value = await api.get('/api/admin/reports/product-sales')
  categorySales.value = await api.get('/api/admin/reports/category-sales')
  dailySales.value = await api.get('/api/admin/reports/daily-sales')
  dailyHotProducts.value = await api.get('/api/admin/reports/daily-hot-products', {
    params: { topN: topN.value }
  })
}

const loadDailyDetail = async () => {
  const params = detailDate.value ? { date: detailDate.value } : {}
  dailySalesDetail.value = await api.get('/api/admin/reports/daily-sales-detail', { params })
}

const dailySalesDetailWithSummary = computed(() => {
  if (!dailySalesDetail.value.length) return []
  const totals = dailySalesDetail.value.reduce(
    (acc, item) => {
      acc.totalQty += Number(item.totalQty || 0)
      acc.orderCount += Number(item.orderCount || 0)
      acc.totalAmount += Number(item.totalAmount || 0)
      return acc
    },
    { totalQty: 0, orderCount: 0, totalAmount: 0 }
  )
  const dateLabel = detailDate.value || '全部'
  return [
    ...dailySalesDetail.value,
    {
      paidDate: dateLabel,
      productName: '合计',
      totalQty: totals.totalQty,
      orderCount: totals.orderCount,
      totalAmount: totals.totalAmount,
      isSummary: true
    }
  ]
})

const rowClassName = ({ row }) => (row.isSummary ? 'summary-row' : '')

const renderChart = () => {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  if (!resizeBound) {
    window.addEventListener('resize', handleResize)
    resizeBound = true
  }
  const labels = dailySales.value.map((row) => row.paidDate)
  const amounts = dailySales.value.map((row) => Number(row.totalAmount || 0))
  chartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const item = params[0]
        return `${item.axisValue}<br/>销售额：¥${item.data}`
      }
    },
    grid: { left: 40, right: 16, top: 24, bottom: 40 },
    xAxis: {
      type: 'category',
      data: labels,
      axisLabel: { rotate: 30, color: '#5c6a7b' },
      axisTick: { alignWithLabel: true }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#5c6a7b' },
      splitLine: { lineStyle: { color: '#e2ecf5' } }
    },
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: amounts,
        barWidth: 24,
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#7cc2ff' },
            { offset: 1, color: '#2bb9a9' }
          ])
        }
      }
    ]
  })
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(async () => {
  await load()
  await nextTick()
  if (dailySales.value.length) {
    renderChart()
  }
})

watch(dailySales, async () => {
  await nextTick()
  if (dailySales.value.length) {
    renderChart()
  } else if (chartInstance) {
    chartInstance.clear()
  }
})

onBeforeUnmount(() => {
  if (resizeBound) {
    window.removeEventListener('resize', handleResize)
  }
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

const dailyHotGroups = computed(() => {
  const map = new Map()
  for (const row of dailyHotProducts.value) {
    if (!map.has(row.paidDate)) {
      map.set(row.paidDate, [])
    }
    map.get(row.paidDate).push(row)
  }
  return Array.from(map.entries())
    .map(([paidDate, items]) => ({ paidDate, items }))
})
</script>

<style scoped>
.chart {
  width: 100%;
  height: 220px;
}

.summary-row td {
  font-weight: 600;
}

.reports-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  background: linear-gradient(120deg, rgba(43, 185, 169, 0.1), rgba(124, 194, 255, 0.1));
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 18px 20px;
  box-shadow: var(--shadow);
}

.page-header h1 {
  margin: 6px 0 4px;
  font-size: 26px;
  letter-spacing: -0.01em;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  color: var(--muted);
}

.grid {
  display: grid;
  gap: 14px;
}

.grid-3 {
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.panel {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.panel-head h3 {
  margin: 0;
  font-size: 18px;
}

.tag {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(43, 185, 169, 0.12);
  color: #2bb9a9;
  font-weight: 700;
  font-size: 12px;
}

.tag.soft {
  background: rgba(124, 194, 255, 0.14);
  color: #3c8edc;
}

.panel-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  font-size: 14px;
}

.row .name {
  font-weight: 600;
}

.row .meta {
  color: var(--muted);
  white-space: nowrap;
}

.hot-list {
  display: grid;
  gap: 6px;
}

.hot-item {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  font-size: 14px;
}

.hot-item .name {
  font-weight: 600;
}

.hot-item .meta {
  color: var(--muted);
}

.filters {
  display: flex;
  align-items: center;
  gap: 10px;
}

@media (max-width: 768px) {
  .panel-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .hot-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .filters {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
