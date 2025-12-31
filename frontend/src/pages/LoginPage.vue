<template>
  <div class="page">
    <div class="card" style="max-width: 420px; margin: 48px auto; padding: 24px;">
      <div class="auth-head">
        <p class="eyebrow">登录</p>
        <h1 class="auth-title">登录</h1>
        <p class="page-subtitle">欢迎回来，继续选购心仪的商品。</p>
      </div>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" style="width: 100%;" @click="handleLogin">登录</el-button>
      </el-form>
      <div style="margin-top: 16px; font-size: 14px;" class="muted">
        没有账号？
        <RouterLink to="/register">去注册</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ username: '', password: '' })

const handleLogin = async () => {
  await auth.login(form)
  ElMessage.success('登录成功')
  router.push('/')
}
</script>

<style scoped>
.auth-head {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 12px;
}

.auth-title {
  margin: 0;
  font-size: 22px;
  letter-spacing: -0.01em;
}
</style>
