import { createRouter, createWebHistory } from 'vue-router'
import Home from './views/Home.vue'
import Publish from './views/Publish.vue'
import Profile from './views/Profile.vue'
import Login from './views/Login.vue'
import Explore from './views/Explore.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/publish',
    name: 'Publish',
    component: Publish,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/explore',
    name: 'Explore',
    component: Explore
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('token')
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router