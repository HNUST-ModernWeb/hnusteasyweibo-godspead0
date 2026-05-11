<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="header-content">
        <div class="logo" @click="goHome">
          <el-icon size="32" color="#e6162d"><component :is="componentMap.Link" /></el-icon>
          <span class="logo-text">简易微博</span>
        </div>
        
        <el-input
          v-model="searchQuery"
          placeholder="搜索微博、用户..."
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><component :is="componentMap.Search" /></el-icon>
            </el-button>
          </template>
        </el-input>

        <div class="header-actions">
          <el-dropdown>
            <el-button type="text" class="action-btn">
              <el-icon :size="20" color="#666"><component :is="componentMap.Bell" /></el-icon>
              <span v-if="unreadCount > 0" class="notification-badge">{{ unreadCount }}</span>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <div v-if="notifications.length === 0" class="empty-notifications">
                  暂无通知
                </div>
                <el-dropdown-item v-for="notif in notifications" :key="notif.id" class="notification-item">
                  <div class="notification-content">
                    <span class="notification-text">{{ notif.content }}</span>
                    <span class="notification-time">{{ formatTime(notif.createdAt) }}</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item divided>
                  <span @click="markAllRead" class="mark-all-read">全部已读</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <el-dropdown>
            <el-button type="text" class="action-btn user-btn">
              <el-avatar :size="32" :icon="componentMap.User" />
              <span>{{ currentUser?.nickname || '登录' }}</span>
              <el-icon :size="16"><component :is="componentMap.ArrowDown" /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-item v-if="currentUser" @click="goProfile">
                <el-icon><component :is="componentMap.User" /></el-icon>
                <span>个人主页</span>
              </el-dropdown-item>
              <el-dropdown-item v-if="currentUser" @click="goPublish">
                <el-icon><component :is="componentMap.Plus" /></el-icon>
                <span>发布微博</span>
              </el-dropdown-item>
              <el-dropdown-item v-if="currentUser" divided @click="handleLogout">
                <el-icon><component :is="componentMap.Close" /></el-icon>
                <span>退出登录</span>
              </el-dropdown-item>
              <el-dropdown-item v-else @click="goLogin">
                <el-icon><component :is="componentMap.Right" /></el-icon>
                <span>登录/注册</span>
              </el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-container>
      <el-aside class="app-sidebar" width="200px">
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          mode="vertical"
        >
          <el-menu-item index="/" @click="goHome">
            <el-icon><component :is="componentMap.Home" /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/publish" v-if="currentUser" @click="goPublish">
            <el-icon><component :is="componentMap.EditPen" /></el-icon>
            <span>发布</span>
          </el-menu-item>
          <el-menu-item index="/profile" v-if="currentUser" @click="goProfile">
            <el-icon><component :is="componentMap.User" /></el-icon>
            <span>个人主页</span>
          </el-menu-item>
          <el-menu-item index="/explore" @click="goExplore">
            <el-icon><component :is="componentMap.TrendCharts" /></el-icon>
            <span>发现</span>
          </el-menu-item>
        </el-menu>

        <div class="sidebar-stats" v-if="currentUser">
          <el-card class="stats-card">
            <div class="stats-title">我的数据</div>
            <div class="stats-row">
              <div class="stat-item">
                <span class="stat-value">{{ userStats.posts }}</span>
                <span class="stat-label">微博</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ userStats.following }}</span>
                <span class="stat-label">关注</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ userStats.followers }}</span>
                <span class="stat-label">粉丝</span>
              </div>
            </div>
          </el-card>
        </div>
      </el-aside>

      <el-main class="app-main">
        <transition name="fade" mode="out-in">
          <router-view />
        </transition>
      </el-main>
    </el-container>

    <el-footer class="app-footer">
      <p>简易微博 © 2024 - 分享生活，记录美好</p>
    </el-footer>
  </el-container>
</template>

<script>
import { ref, reactive, computed, onMounted, defineComponent } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Link,
  Search,
  Bell,
  ArrowDown,
  User,
  Plus,
  Close,
  Right,
  House,
  EditPen,
  TrendCharts
} from '@element-plus/icons-vue'

export default defineComponent({
  name: 'App',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const searchQuery = ref('')
    const currentUser = ref(null)
    const notifications = ref([])
    const unreadCount = ref(0)

    const userStats = reactive({
      posts: 0,
      following: 0,
      followers: 0
    })

    const componentMap = {
      Link,
      Search,
      Bell,
      ArrowDown,
      User,
      Plus,
      Close,
      Right,
      House,
      EditPen,
      TrendCharts
    }

    const activeMenu = computed(() => {
      return route.path
    })

    const goHome = () => {
      router.push('/')
    }

    const goPublish = () => {
      router.push('/publish')
    }

    const goProfile = () => {
      router.push('/profile')
    }

    const goExplore = () => {
      router.push('/explore')
    }

    const goLogin = () => {
      router.push('/login')
    }

    const handleSearch = () => {
      if (searchQuery.value.trim()) {
        alert(`搜索: ${searchQuery.value}`)
        searchQuery.value = ''
      }
    }

    const handleLogout = () => {
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      currentUser.value = null
      router.push('/login')
    }

    const formatTime = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const now = new Date()
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)

      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 7) return `${days}天前`
      return date.toLocaleDateString('zh-CN')
    }

    const loadUser = () => {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        currentUser.value = JSON.parse(userStr)
        loadNotifications()
        loadUserStats()
      }
    }

    const loadNotifications = () => {
      notifications.value = [
        { id: 1, content: '用户A赞了你的微博', createdAt: Date.now() - 300000 },
        { id: 2, content: '用户B评论了你的微博', createdAt: Date.now() - 1800000 },
        { id: 3, content: '用户C关注了你', createdAt: Date.now() - 7200000 }
      ]
      unreadCount.value = notifications.value.length
    }

    const loadUserStats = () => {
      userStats.posts = Math.floor(Math.random() * 100) + 10
      userStats.following = Math.floor(Math.random() * 200) + 50
      userStats.followers = Math.floor(Math.random() * 500) + 20
    }

    const markAllRead = () => {
      unreadCount.value = 0
    }

    onMounted(() => {
      loadUser()
    })

    return {
      searchQuery,
      currentUser,
      notifications,
      unreadCount,
      userStats,
      activeMenu,
      componentMap,
      goHome,
      goPublish,
      goProfile,
      goExplore,
      goLogin,
      handleSearch,
      handleLogout,
      formatTime,
      markAllRead
    }
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

<style scoped>
.app-container {
  min-height: 100vh;
}

.app-header {
  background: linear-gradient(135deg, #e6162d 0%, #ff4d4f 100%);
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: white;
}

.search-input {
  flex: 1;
  max-width: 400px;
  margin: 0 40px;
  background-color: rgba(255, 255, 255, 0.2);
  border: none;
}

.search-input :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
}

.search-input :deep(.el-input__inner) {
  border-radius: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  color: white;
  font-size: 16px;
  position: relative;
}

.action-btn :deep(.el-icon) {
  color: white;
}

.notification-badge {
  position: absolute;
  top: -4px;
  right: -8px;
  background-color: #ff6b6b;
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.user-btn {
  gap: 10px;
}

.empty-notifications {
  padding: 20px 40px;
  color: #999;
}

.notification-item {
  padding: 12px 16px !important;
}

.notification-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.notification-text {
  font-size: 14px;
  color: #333;
}

.notification-time {
  font-size: 12px;
  color: #999;
}

.mark-all-read {
  color: #e6162d;
  cursor: pointer;
}

.app-sidebar {
  background-color: #fff;
  border-right: 1px solid #e8e8e8;
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu :deep(.el-menu-item) {
  margin: 4px 8px;
  border-radius: 8px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: #f5f5f5;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #fff5f5;
  color: #e6162d;
}

.sidebar-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #e6162d;
}

.sidebar-stats {
  padding: 16px;
}

.stats-card {
  border-radius: 12px;
}

.stats-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.stats-row {
  display: flex;
  justify-content: space-around;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.app-main {
  padding: 20px;
  background-color: #f5f5f5;
}

.app-footer {
  text-align: center;
  padding: 20px;
  background-color: #fff;
  border-top: 1px solid #e8e8e8;
}

.app-footer p {
  color: #999;
  font-size: 14px;
}
</style>
