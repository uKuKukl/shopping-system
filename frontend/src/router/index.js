import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

import HomePage from '../pages/HomePage.vue'
import LoginPage from '../pages/LoginPage.vue'
import RegisterPage from '../pages/RegisterPage.vue'
import ProductDetailPage from '../pages/ProductDetailPage.vue'
import CartPage from '../pages/CartPage.vue'
import CheckoutPage from '../pages/CheckoutPage.vue'
import PayPage from '../pages/PayPage.vue'
import OrdersPage from '../pages/OrdersPage.vue'
import OrderDetailPage from '../pages/OrderDetailPage.vue'
import AdminProductsPage from '../pages/admin/AdminProductsPage.vue'
import AdminOrdersPage from '../pages/admin/AdminOrdersPage.vue'
import AdminReportsPage from '../pages/admin/AdminReportsPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomePage },
    { path: '/login', component: LoginPage },
    { path: '/register', component: RegisterPage },
    { path: '/product/:id', component: ProductDetailPage },
    { path: '/cart', component: CartPage },
    { path: '/checkout', component: CheckoutPage },
    { path: '/pay/:orderNo', component: PayPage },
    { path: '/orders', component: OrdersPage },
    { path: '/orders/:orderNo', component: OrderDetailPage },
    { path: '/admin/products', component: AdminProductsPage, meta: { admin: true } },
    { path: '/admin/orders', component: AdminOrdersPage, meta: { admin: true } },
    { path: '/admin/reports', component: AdminReportsPage, meta: { admin: true } }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.admin && auth.user?.role !== 'ADMIN') {
    return '/login'
  }
  return true
})

export default router
