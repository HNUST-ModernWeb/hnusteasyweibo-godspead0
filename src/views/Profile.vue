<template>
  <div class="profile-page">
    <el-card class="profile-card" shadow="hover">
      <div class="profile-header">
        <div class="avatar-section">
          <el-avatar :size="120" :icon="componentMap.User" class="profile-avatar" />
        </div>
        <div class="user-info">
          <h2 class="user-name">{{ profile.nickname }}</h2>
          <p class="user-bio">{{ profile.bio || '暂无简介' }}</p>
          <div class="user-stats">
            <div class="stat">
              <span class="stat-value">{{ profile.postCount }}</span>
              <span class="stat-label">微博</span>
            </div>
            <div class="stat">
              <span class="stat-value">{{ profile.following }}</span>
              <span class="stat-label">关注</span>
            </div>
            <div class="stat">
              <span class="stat-value">{{ profile.followers }}</span>
              <span class="stat-label">粉丝</span>
            </div>
          </div>
          <div class="user-actions">
            <el-button
              v-if="isCurrentUser"
              type="primary"
              @click="goPublish"
            >
              <el-icon><component :is="componentMap.EditPen" /></el-icon>
              编辑资料
            </el-button>
            <el-button
              v-else-if="!isFollowing"
              type="primary"
              @click="toggleFollow"
            >
              + 关注
            </el-button>
            <el-button
              v-else
              @click="toggleFollow"
            >
              已关注
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="posts-card" shadow="hover" style="margin-top: 20px;">
      <template #header>
        <span class="posts-title">我的微博</span>
      </template>

      <div v-if="posts.length === 0" class="empty-posts">
        <el-empty description="暂无微博，快来发布第一条吧！" />
      </div>

      <div v-else class="user-posts">
        <div
          v-for="post in posts"
          :key="post.id"
          class="user-post-item"
        >
          <div class="post-content">
            <p>{{ post.content }}</p>
          </div>
          <div v-if="post.imageUrls" class="post-images">
            <el-image
              v-for="(img, idx) in post.imageUrls.split(',')"
              :key="idx"
              :src="getImageUrl(img)"
              :preview-src-list="post.imageUrls.split(',').map(getImageUrl)"
              class="post-image"
              fit="cover"
            />
          </div>
          <div class="post-meta">
            <span class="post-time">{{ formatTime(post.createdAt) }}</span>
            <span class="post-stats">
              <el-icon :size="14" color="#999"><component :is="componentMap.Star" /></el-icon>
              {{ post.likeCount }}
              <el-icon :size="14" color="#999" style="margin-left: 12px;"><component :is="componentMap.ChatDotRound" /></el-icon>
              {{ post.commentCount }}
            </span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted, defineComponent, computed } from 'vue'
import { useRouter } from 'vue-router'
import { postAPI } from '../api/postAPI'
import { User, EditPen, Star, ChatDotRound } from '@element-plus/icons-vue'

export default defineComponent({
  name: 'Profile',
  setup() {
    const router = useRouter()
    const profile = ref({
      nickname: '',
      bio: '',
      postCount: 0,
      following: 0,
      followers: 0
    })
    const posts = ref([])
    const isFollowing = ref(false)

    const componentMap = {
      User,
      EditPen,
      Star,
      ChatDotRound
    }

    const currentUser = computed(() => {
      const userStr = localStorage.getItem('user')
      return userStr ? JSON.parse(userStr) : null
    })

    const isCurrentUser = computed(() => {
      return currentUser.value && currentUser.value.id === 1
    })

    const getImageUrl = (img) => {
      if (!img) return ''
      if (img.startsWith('http')) return img
      return 'http://localhost:8081' + img
    }

    const formatTime = (dateStr) => {
      if (!dateStr) return '未知时间'
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const loadProfile = async () => {
      try {
        profile.value = {
          nickname: currentUser.value?.nickname || '用户' + currentUser.value?.id,
          bio: '热爱生活，分享美好',
          postCount: 28,
          following: 128,
          followers: 256
        }
      } catch (error) {
        console.error('Failed to load profile:', error)
      }
    }

    const loadPosts = async () => {
      try {
        const data = await postAPI.getAllPosts()
        posts.value = data.filter(post => post.userId === currentUser.value?.id).slice(0, 10)
      } catch (error) {
        console.error('Failed to load posts:', error)
      }
    }

    const goPublish = () => {
      router.push('/publish')
    }

    const toggleFollow = () => {
      isFollowing.value = !isFollowing.value
    }

    onMounted(() => {
      loadProfile()
      loadPosts()
    })

    return {
      profile,
      posts,
      isFollowing,
      isCurrentUser,
      componentMap,
      getImageUrl,
      formatTime,
      goPublish,
      toggleFollow
    }
  }
})
</script>

<style scoped>
.profile-page {
  padding: 20px;
}

.profile-card {
  border-radius: 12px;
}

.profile-header {
  display: flex;
  gap: 30px;
}

.avatar-section {
  flex-shrink: 0;
}

.profile-avatar {
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-info {
  flex: 1;
}

.user-name {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.user-bio {
  margin: 0 0 16px;
  color: #666;
  font-size: 14px;
}

.user-stats {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
}

.stat {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.user-actions {
  display: flex;
  gap: 12px;
}

.posts-card {
  border-radius: 12px;
}

.posts-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.empty-posts {
  padding: 40px;
}

.user-posts {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-post-item {
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
}

.user-post-item:last-child {
  border-bottom: none;
}

.post-content p {
  margin: 0;
  line-height: 1.6;
  color: #333;
}

.post-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.post-image {
  width: calc(33.33% - 6px);
  height: 100px;
  border-radius: 8px;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  color: #999;
  font-size: 12px;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
