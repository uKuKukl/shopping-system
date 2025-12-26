<template>
  <div class="page">
    <div class="card" style="max-width: 420px; margin: 48px auto; padding: 24px;">
      <div class="section-title">注册</div>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <div style="display: flex; gap: 8px; width: 100%;">
            <el-input v-model="emailLocal" placeholder="邮箱名" style="flex: 1;" />
            <el-select v-model="emailDomain" placeholder="选择域名" style="width: 160px;">
              <el-option v-for="item in emailDomains" :key="item" :label="`@${item}`" :value="item" />
              <el-option label="自定义" value="custom" />
            </el-select>
          </div>
          <el-input
            v-if="emailDomain === 'custom'"
            v-model="customDomain"
            placeholder="自定义域名，如 example.com"
            style="margin-top: 8px;"
          />
          <div class="muted" style="margin-top: 6px;">{{ emailPreview }}</div>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="可选" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" style="width: 100%;" @click="handleRegister">注册并登录</el-button>
      </el-form>
      <div style="margin-top: 16px; font-size: 14px;" class="muted">
        已有账号？
        <RouterLink to="/login">去登录</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ username: '', phone: '', password: '' })
const emailLocal = ref('')
const emailDomain = ref('qq.com')
const customDomain = ref('')
const emailDomains = ['qq.com', '163.com', '126.com', 'gmail.com', 'outlook.com', 'hotmail.com', 'icloud.com', 'yahoo.com']

const emailValue = computed(() => {
  const local = emailLocal.value.trim()
  const domain = emailDomain.value === 'custom' ? customDomain.value.trim() : emailDomain.value
  if (!local || !domain) {
    return ''
  }
  return `${local}@${domain}`
})

const emailPreview = computed(() => (emailValue.value ? `将使用邮箱：${emailValue.value}` : '邮箱可选'))

const handleRegister = async () => {
  const payload = { ...form, email: emailValue.value }
  await auth.register(payload)
  ElMessage.success('注册成功')
  router.push('/')
}
</script>
