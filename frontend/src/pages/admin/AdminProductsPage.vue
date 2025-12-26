<template>
  <div class="page">
    <div class="section-title">商品管理</div>
    <div class="card" style="padding: 16px; margin-bottom: 16px;">
      <el-form :model="form" label-position="top">
        <el-row :gutter="12">
          <el-col :span="6">
            <el-form-item label="分类ID">
              <el-input v-model.number="form.categoryId" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="SKU">
              <el-input v-model="form.sku" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="名称">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="价格">
              <el-input v-model.number="form.price" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="库存">
              <el-input v-model.number="form.stock" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select v-model="form.status">
                <el-option label="上架" value="ON_SALE" />
                <el-option label="下架" value="OFF_SALE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="描述">
              <el-input v-model="form.description" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-button type="primary" @click="createProduct">新增商品</el-button>
      </el-form>
    </div>

    <div class="card" style="padding: 16px;">
      <el-table :data="products" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="info" @click="openImages(scope.row)">图片</el-button>
            <el-button size="small" type="warning" @click="toggleStatus(scope.row)">
              {{ scope.row.status === 'ON_SALE' ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
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

    <el-dialog v-model="showEdit" title="编辑商品" width="600px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="分类ID">
          <el-input v-model.number="editForm.categoryId" />
        </el-form-item>
        <el-form-item label="SKU">
          <el-input v-model="editForm.sku" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model.number="editForm.price" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input v-model.number="editForm.stock" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option label="上架" value="ON_SALE" />
            <el-option label="下架" value="OFF_SALE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEdit = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showImages" title="商品图片管理" width="720px">
      <div class="muted" style="margin-bottom: 12px;">
        当前商品：{{ imageProduct?.name }} (ID: {{ imageProduct?.id }})
      </div>
      <el-form :model="imageForm" label-position="top" style="margin-bottom: 12px;">
        <el-form-item label="图片地址">
          <el-input v-model="imageForm.imageUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="imageForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="imageForm.isPrimary">设为主图</el-checkbox>
        </el-form-item>
        <el-button type="primary" @click="addImage">添加图片</el-button>
      </el-form>

      <el-table :data="images" style="width: 100%">
        <el-table-column label="预览" width="120">
          <template #default="scope">
            <img :src="scope.row.imageUrl" style="width: 80px; height: 60px; object-fit: cover; border-radius: 6px;" />
          </template>
        </el-table-column>
        <el-table-column label="地址">
          <template #default="scope">
            <el-input v-model="scope.row.imageUrl" />
          </template>
        </el-table-column>
        <el-table-column label="排序" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.sortOrder" :min="0" />
          </template>
        </el-table-column>
        <el-table-column label="主图" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.isPrimary" type="success">主图</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="saveImage(scope.row)">保存</el-button>
            <el-button size="small" type="success" @click="setPrimary(scope.row)">设为主图</el-button>
            <el-button size="small" type="danger" @click="removeImage(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../../utils/request'
import { ElMessage } from 'element-plus'

const products = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)
const showEdit = ref(false)
const editForm = reactive({})
const showImages = ref(false)
const images = ref([])
const imageProduct = ref(null)
const imageForm = reactive({ imageUrl: '', sortOrder: 0, isPrimary: false })

const form = reactive({
  categoryId: null,
  sku: '',
  name: '',
  description: '',
  price: null,
  stock: null,
  status: 'ON_SALE'
})

const load = async () => {
  const data = await api.get('/api/admin/products', { params: { page: page.value, size: size.value } })
  products.value = data.content
  total.value = data.totalElements
}

const handlePage = (p) => {
  page.value = p - 1
  load()
}

const createProduct = async () => {
  await api.post('/api/admin/products', form)
  ElMessage.success('已新增')
  Object.assign(form, { categoryId: null, sku: '', name: '', description: '', price: null, stock: null, status: 'ON_SALE' })
  load()
}

const openEdit = (row) => {
  Object.assign(editForm, row)
  showEdit.value = true
}

const saveEdit = async () => {
  await api.put(`/api/admin/products/${editForm.id}`, editForm)
  ElMessage.success('已更新')
  showEdit.value = false
  load()
}

const toggleStatus = async (row) => {
  const status = row.status === 'ON_SALE' ? 'OFF_SALE' : 'ON_SALE'
  await api.patch(`/api/admin/products/${row.id}/status`, { status })
  ElMessage.success('状态已更新')
  load()
}

const openImages = async (row) => {
  imageProduct.value = row
  showImages.value = true
  imageForm.imageUrl = ''
  imageForm.sortOrder = 0
  imageForm.isPrimary = false
  await loadImages()
}

const loadImages = async () => {
  if (!imageProduct.value) return
  images.value = await api.get(`/api/admin/products/${imageProduct.value.id}/images`)
}

const addImage = async () => {
  if (!imageProduct.value) return
  await api.post(`/api/admin/products/${imageProduct.value.id}/images`, {
    imageUrl: imageForm.imageUrl,
    sortOrder: imageForm.sortOrder,
    isPrimary: imageForm.isPrimary
  })
  ElMessage.success('已添加图片')
  imageForm.imageUrl = ''
  imageForm.sortOrder = 0
  imageForm.isPrimary = false
  await loadImages()
}

const saveImage = async (row) => {
  await api.patch(`/api/admin/products/images/${row.id}`, {
    imageUrl: row.imageUrl,
    sortOrder: row.sortOrder,
    isPrimary: row.isPrimary
  })
  ElMessage.success('已保存')
  await loadImages()
}

const setPrimary = async (row) => {
  await api.patch(`/api/admin/products/images/${row.id}`, { isPrimary: true })
  ElMessage.success('已设为主图')
  await loadImages()
}

const removeImage = async (row) => {
  await api.delete(`/api/admin/products/images/${row.id}`)
  ElMessage.success('已删除')
  await loadImages()
}

onMounted(load)
</script>
