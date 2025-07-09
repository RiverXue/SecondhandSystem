import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
const Home = () => import('../pages/Home.vue')
const Login = () => import('../pages/Login.vue')
const Register = () => import('../pages/Register.vue')
const UserCenter = () => import('../pages/UserCenter.vue')
const GoodsDetail = () => import('../pages/GoodsDetail.vue')
const Publish = () => import('../pages/Publish.vue')
const UserProfile = () => import('../views/UserProfile.vue')
const Favorites = () => import('../views/Favorites.vue')
const OrderList = () => import('../views/OrderList.vue')
const MyPublished = () => import('../pages/MyPublished.vue')

const routes: Array<RouteRecordRaw> = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/user-center', component: UserCenter },
  { path: '/goods/detail/:id', component: GoodsDetail },
  { path: '/publish', component: Publish },
  { path: '/user/profile', name: 'UserProfile', component: UserProfile, meta: { requiresAuth: true } },
  { path: '/favorites', name: 'Favorites', component: Favorites, meta: { requiresAuth: true } },
  { path: '/orders', name: 'OrderList', component: OrderList, meta: { requiresAuth: true } },
  { path: '/my-published', name: 'MyPublished', component: MyPublished, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('accessToken')
  // 不需要登录的页面
  const publicPages = ['/login', '/register']
  const requiresAuth = !publicPages.includes(to.path)

  if (requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router