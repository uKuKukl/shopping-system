<template>
  <div class="page">
    <div class="section-title">报表</div>
    <el-row :gutter="16">
      <el-col :xs="24" :md="8">
        <div class="card" style="padding: 16px;">
          <div class="section-title" style="font-size: 18px;">商品销售</div>
          <div v-for="row in productSales" :key="row.productId" style="margin-bottom: 8px;">
            {{ row.productName }} - {{ row.totalQty }} 件 / ¥{{ row.totalAmount }}
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :md="8">
        <div class="card" style="padding: 16px;">
          <div class="section-title" style="font-size: 18px;">分类销售</div>
          <div v-for="row in categorySales" :key="row.categoryId" style="margin-bottom: 8px;">
            {{ row.categoryName }} - {{ row.totalQty }} 件 / ¥{{ row.totalAmount }}
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :md="8">
        <div class="card" style="padding: 16px;">
          <div class="section-title" style="font-size: 18px;">每日销售</div>
          <div ref="chartRef" class="chart" v-if="dailySales.length"></div>
          <div v-else class="muted">暂无数据</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import api from '../../utils/request'

const productSales = ref([])
const categorySales = ref([])
const dailySales = ref([])
const chartRef = ref(null)
let chartInstance = null
let resizeBound = false

const load = async () => {
  productSales.value = await api.get('/api/admin/reports/product-sales')
  categorySales.value = await api.get('/api/admin/reports/category-sales')
  dailySales.value = await api.get('/api/admin/reports/daily-sales')
}

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
    grid: { left: 40, right: 16, top: 20, bottom: 40 },
    xAxis: {
      type: 'category',
      data: labels,
      axisLabel: { rotate: 30, color: '#6f6458' },
      axisTick: { alignWithLabel: true }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#6f6458' },
      splitLine: { lineStyle: { color: '#eee4d6' } }
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
            { offset: 0, color: '#f59e0b' },
            { offset: 1, color: '#d97706' }
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
</script>

<style scoped>
.chart {
  width: 100%;
  height: 220px;
}
</style>
