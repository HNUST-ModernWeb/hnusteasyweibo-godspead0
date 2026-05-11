<template>
  <div class="notification-list">
    <div v-if="loading" class="loading">
      <el-loading-spinner size="medium" />
    </div>
    <div v-else-if="notifications.length === 0" class="empty">
      <el-icon size="48" color="#ccc"><component :is="componentMap.Bell" /></el-icon>
      <p>暂无通知</p>
    </div>
    <div v-else>
      <el-timeline mode="left">
        <el-timeline-item
          v-for="notification in notifications"
          :key="notification.id"
          :timestamp="formatTime(notification.createdAt)"
          :color="getNotificationColor(notification.type)"
        >
          <el-card
            class="notification-card"
            :class="{ 'unread': !notification.read }"
            @click="markAsRead(notification.id)"
          >
            <div class="notification-content">
              <el-icon :size="20" :color="getNotificationIconColor(notification.type)">
                <component :is="getNotificationIcon(notification.type)" />
              </el-icon>
              <span class="text">
                <span class="highlight">{{ notification.sourceUserNickname }}</span>
                {{ getNotificationText(notification.type) }}
                <span v-if="notification.postContent" class="post-preview">"{{ notification.postContent }}"</span>
              </span>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <div class="mark-all-btn">
        <el-button @click="markAllAsRead" type="text" size="small">
          全部标为已读
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, defineComponent } from 'vue'
import { notificationAPI } from '../api/notificationAPI'
import {
  Bell,
  Heart,
  Message,
  UserPlus,
  Repeat
} from '@element-plus/icons-vue'

export default defineComponent({
  name: 'NotificationList',
  emits: ['read'],
  setup(props, { emit }) {
    const notifications = ref([])
    const loading = ref(true)

    const componentMap = {
      Bell,
      Heart,
      Message,
      UserPlus,
      Repeat
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
          notification.read = true
        }
        emit('read')
      } catch (error) {
        console.error('Failed to mark notification as read:', error)
      }
    }

    const markAllAsRead = async () => {
      try {
        const currentUser = JSON.parse(localStorage.getItem('user'))
        if (currentUser) {
          await notificationAPI.markAllAsRead(currentUser.id)
          notifications.value.forEach(n => n.read = true)
          emit('read')
        }
      } catch (error) {
        console.error('Failed to mark all as read:', error)
      }
    }

    const formatTime = (dateStr) => {
      if (!dateStr) return '未知时间'
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN')
    }

    const getNotificationColor = (type) => {
      const colors = {
        LIKE: '#ff6b6b',
        COMMENT: '#4ecdc4',
        FOLLOW: '#45b7d1',
        REPOST: '#96ceb4'
      }
      return colors[type] || '#999'
    }

    const getNotificationIconColor = (type) => {
      return getNotificationColor(type)
    }

    const getNotificationIcon = (type) => {
      const icons = {
        LIKE: Heart,
        COMMENT: Message,
        FOLLOW: UserPlus,
        REPOST: Repeat
      }
      return icons[type] || Bell
    }

    const getNotificationText = (type) => {
      const texts = {
        LIKE: '点赞了你的微博',
        COMMENT: '评论了你的微博',
        FOLLOW: '关注了你',
        REPOST: '转发了你的微博'
      }
      return texts[type] || '有新的动态'
    }

    onMounted(() => {
      loadNotifications()
    })

    return {
      notifications,
      loading,
      componentMap,
      markAsRead,
      markAllAsRead,
      formatTime,
      getNotificationColor,
      getNotificationIconColor,
      getNotificationIcon,
      getNotificationText
    }
  }
})
</script>

<style scoped>
.notification-list {
  padding: 16px;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  color: #999;
}

.empty p {
  margin-top: 16px;
}

.notification-card {
  cursor: pointer;
  transition: all 0.3s;
}

.notification-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.notification-card.unread {
  background-color: #fffbf0;
  border-left: 3px solid #e6162d;
}

.notification-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.text {
  flex: 1;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.highlight {
  color: #e6162d;
  font-weight: 500;
}

.post-preview {
  display: block;
  color: #999;
  margin-top: 8px;
  font-style: italic;
}

.mark-all-btn {
  text-align: center;
  margin-top: 20px;
}
</style>
