<template>
  <div class="notifications">
    <h2>通知</h2>
    <div v-if="loading" class="loading">
      加载中...
    </div>
    <div v-else-if="notifications.length === 0" class="empty">
      暂无通知
    </div>
    <div v-else>
      <div 
        v-for="notification in notifications" 
        :key="notification.id" 
        class="notification-item"
        :class="{ unread: !notification.isRead }"
        @click="markAsRead(notification.id)"
      >
        <div class="notification-avatar"></div>
        <div class="notification-content">
          <div class="notification-header">
            <span class="notification-user">{{ notification.fromUser?.nickname || '用户' + notification.fromUserId }}</span>
            <span class="notification-action">{{ notification.content }}</span>
          </div>
          <div class="notification-time">{{ formatTime(notification.createdAt) }}</div>
        </div>
        <div v-if="!notification.isRead" class="unread-dot"></div>
      </div>
    </div>
    <button v-if="notifications.length > 0" class="mark-all-btn" @click="markAllAsRead">
      全部已读
    </button>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { notificationAPI } from '../api/notificationAPI'

export default {
  name: 'Notifications',
  setup() {
    const notifications = ref([])
    const loading = ref(true)

    const formatTime = (dateStr) => {
      if (!dateStr) return '未知时间'
      const date = new Date(dateStr)
      const now = new Date()
      const diff = now.getTime() - date.getTime()
      
      if (diff < 60000) {
        return '刚刚'
      } else if (diff < 3600000) {
        return `${Math.floor(diff / 60000)}分钟前`
      } else if (diff < 86400000) {
        return `${Math.floor(diff / 3600000)}小时前`
      } else {
        return date.toLocaleDateString('zh-CN')
      }
    }

    const loadNotifications = async () => {
      try {
        loading.value = true
        const currentUser = JSON.parse(localStorage.getItem('user'))
        if (currentUser) {
          const data = await notificationAPI.getNotifications(currentUser.id)
          notifications.value = data
        }
      } catch (error) {
        console.error('Failed to load notifications:', error)
      } finally {
        loading.value = false
      }
    }

    const markAsRead = async (id) => {
      try {
        await notificationAPI.markAsRead(id)
        const notification = notifications.value.find(n => n.id === id)
        if (notification) {
          notification.isRead = true
        }
      } catch (error) {
        console.error('Failed to mark notification as read:', error)
      }
    }

    const markAllAsRead = async () => {
      try {
        const currentUser = JSON.parse(localStorage.getItem('user'))
        if (currentUser) {
          await notificationAPI.markAllAsRead(currentUser.id)
          notifications.value.forEach(n => n.isRead = true)
        }
      } catch (error) {
        console.error('Failed to mark all notifications as read:', error)
      }
    }

    onMounted(() => {
      loadNotifications()
    })

    return {
      notifications,
      loading,
      formatTime,
      markAsRead,
      markAllAsRead
    }
  }
}
</script>

<style scoped>
.notifications {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.notifications h2 {
  margin-bottom: 1.5rem;
}
.loading {
  text-align: center;
  padding: 2rem;
  color: #3498db;
}
.empty {
  text-align: center;
  padding: 3rem;
  color: #999;
}
.notification-item {
  display: flex;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.notification-item:hover {
  background-color: #fafafa;
}
.notification-item.unread {
  background-color: #fdf6f6;
}
.notification-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #3498db;
  margin-right: 1rem;
}
.notification-content {
  flex: 1;
}
.notification-header {
  font-size: 0.9rem;
  margin-bottom: 0.25rem;
}
.notification-user {
  font-weight: bold;
  margin-right: 0.5rem;
}
.notification-action {
  color: #666;
}
.notification-time {
  font-size: 0.75rem;
  color: #999;
}
.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #e74c3c;
}
.mark-all-btn {
  width: 100%;
  padding: 0.75rem;
  margin-top: 1rem;
  border: none;
  border-radius: 4px;
  background-color: #3498db;
  color: white;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s ease;
}
.mark-all-btn:hover {
  background-color: #2980b9;
}
</style>