<template>
  <div class="page">
    <div class="section-title">精选商品</div>
    <div class="card" style="padding: 16px; margin-bottom: 20px;">
      <div style="display: flex; gap: 12px; flex-wrap: wrap; align-items: center;">
        <el-input v-model="query" placeholder="搜索商品" style="max-width: 320px;" />
        <el-button type="primary" @click="search">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <div style="margin-top: 12px; display: flex; gap: 8px; flex-wrap: wrap; align-items: center;">
        <el-tag :type="selectedCategoryId === null ? 'warning' : 'info'" @click="selectCategory(null)" style="cursor: pointer;">
          全部
        </el-tag>
        <el-tag
          v-for="cat in categories"
          :key="cat.id"
          :type="selectedCategoryId === cat.id ? 'warning' : 'info'"
          @click="selectCategory(cat.id)"
          style="cursor: pointer;"
        >
          {{ cat.name }}
        </el-tag>
      </div>
    </div>

    <div class="card" style="padding: 16px;">
      <el-row :gutter="16">
        <el-col v-for="item in products" :key="item.id" :xs="24" :sm="12" :md="8" style="margin-bottom: 16px;">
          <div class="card" style="padding: 16px; height: 100%;">
            <div style="font-weight: 600; font-size: 16px;">{{ item.name }}</div>
            <div class="muted" style="margin: 6px 0; min-height: 40px;">{{ item.description || '暂无描述' }}</div>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <div style="font-weight: 700; color: var(--accent);">¥{{ item.price }}</div>
              <el-button size="small" @click="goDetail(item.id)">查看</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
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
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/request'

const router = useRouter()
const categories = ref([])
const products = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(9)
const query = ref('')
const selectedCategoryId = ref(null)

const loadCategories = async () => {
  categories.value = await api.get('/api/categories')
}

const loadProducts = async () => {
  const data = await api.get('/api/products', {
    params: { page: page.value, size: size.value, categoryId: selectedCategoryId.value }
  })
  products.value = data.content
  total.value = data.totalElements
}

const search = async () => {
  if (!query.value) {
    return loadProducts()
  }
  const data = await api.get('/api/products/search', {
    params: { q: query.value, page: page.value, size: size.value, categoryId: selectedCategoryId.value }
  })
  products.value = data.content
  total.value = data.totalElements
}

const resetSearch = () => {
  query.value = ''
  page.value = 0
  loadProducts()
}

const handlePage = (p) => {
  page.value = p - 1
  if (query.value) {
    search()
  } else {
    loadProducts()
  }
}

const selectCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  page.value = 0
  if (query.value) {
    search()
  } else {
    loadProducts()
  }
}

const goDetail = (id) => {
  router.push(`/product/${id}`)
}

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>
