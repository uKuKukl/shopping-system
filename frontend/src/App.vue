<template>
  <div>
    <header class="navbar">
      <div class="navbar-inner">
        <div class="brand">ShopNow</div>
        <nav class="nav-links">
          <RouterLink to="/">首页</RouterLink>
          <RouterLink to="/cart">购物车</RouterLink>
          <RouterLink to="/orders">订单</RouterLink>
          <template v-if="isAdmin">
            <RouterLink to="/admin/products">商品管理</RouterLink>
            <RouterLink to="/admin/orders">订单管理</RouterLink>
            <RouterLink to="/admin/reports">报表</RouterLink>
          </template>
        </nav>
        <div class="nav-links">
          <span v-if="user" class="muted">Hi, {{ user.username }}</span>
          <RouterLink v-if="!user" to="/login">登录</RouterLink>
          <a v-else href="#" @click.prevent="logout">退出</a>
        </div>
      </div>
    </header>
    <main class="layout fade-in">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from './stores/auth'

const auth = useAuthStore()
const user = computed(() => auth.user)
const isAdmin = computed(() => auth.user?.role === 'ADMIN')

const logout = () => {
  auth.logout()
}
</script>
