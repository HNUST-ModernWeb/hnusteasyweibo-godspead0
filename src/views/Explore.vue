<template>
  <div class="explore-page">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="trending-card" shadow="hover">
          <template #header>
            <span class="card-title">
              <el-icon :size="20" color="#e6162d"><component :is="componentMap.Sunny" /></el-icon>
              热门微博
            </span>
          </template>

          <div class="hot-posts">
            <div
              v-for="(post, index) in hotPosts"
              :key="post.id"
              class="hot-post-item"
            >
              <span class="post-rank" :class="{ 'top3': index < 3 }">{{ index + 1 }}</span>
              <div class="post-content">
                <p class="post-text">{{ post.content }}</p>
                <div class="post-stats">
                  <span class="stat-item">
                    <el-icon :size="14" color="#999"><component :is="componentMap.Star" /></el-icon>
                    {{ post.likeCount }}
                  </span>
                  <span class="stat-item">
                    <el-icon :size="14" color="#999"><component :is="componentMap.ChatDotRound" /></el-icon>
                    {{ post.commentCount }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="recent-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <span class="card-title">
              <el-icon :size="20" color="#3498db"><component :is="componentMap.Clock" /></el-icon>
              最新动态
            </span>
          </template>

          <div class="recent-posts">
            <div
              v-for="post in recentPosts"
              :key="post.id"
              class="recent-post-item"
            >
              <el-avatar :size="40" :icon="componentMap.User" />
              <div class="post-body">
                <div class="post-header">
                  <span class="user-name">{{ post.user?.nickname || '用户' + post.userId }}</span>
                  <span class="post-time">{{ formatTime(post.createdAt) }}</span>
                </div>
                <p class="post-content">{{ post.content }}</p>
                <div v-if="post.imageUrls" class="post-images">
                  <el-image
                    v-for="(img, idx) in post.imageUrls.split(',').slice(0, 3)"
                    :key="idx"
                    :src="getImageUrl(img)"
                    class="mini-image"
                    fit="cover"
                  />
                </div>
                <div class="post-actions">
                  <el-button type="text" size="small" class="action-btn">
                    <el-icon :size="14"><component :is="componentMap.Star" /></el-icon>
                    {{ post.likeCount }}
                  </el-button>
                  <el-button type="text" size="small" class="action-btn">
                    <el-icon :size="14"><component :is="componentMap.ChatDotRound" /></el-icon>
                    {{ post.commentCount }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="side-card" shadow="hover">
          <template #header>
            <span class="card-title">
              <el-icon :size="20" color="#e6162d"><component :is="componentMap.Hash" /></el-icon>
              热门话题
            </span>
          </template>

          <div class="hot-topics">
            <div
              v-for="(topic, index) in hotTopics"
              :key="topic.id"
              class="hot-topic-item"
            >
              <span class="topic-rank" :class="{ 'top3': index < 3 }">{{ index + 1 }}</span>
              <div class="topic-info">
                <span class="topic-name">#{{ topic.name }}</span>
                <span class="topic-count">{{ topic.postCount }} 讨论</span>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="side-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <span class="card-title">
              <el-icon :size="20" color="#3498db"><component :is="componentMap.Users" /></el-icon>
              热门用户
            </span>
          </template>

          <div class="hot-users">
            <div
              v-for="user in hotUsers"
              :key="user.id"
              class="hot-user-item"
            >
              <el-avatar :size="48" :icon="componentMap.User" />
              <div class="user-info">
                <span class="user-name">{{ user.nickname }}</span>
                <span class="user-followers">{{ user.followers }} 粉丝</span>
              </div>
              <el-button type="primary" size="small" class="follow-btn">+ 关注</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted, defineComponent } from 'vue'
import { postAPI } from '../api/postAPI'
import { Sunny, Timer, Star, ChatDotRound, PriceTag, Avatar, User } from '@element-plus/icons-vue'

export default defineComponent({
  name: 'Explore',
  setup() {
    const hotPosts = ref([])
    const recentPosts = ref([])
    const hotTopics = ref([])
    const hotUsers = ref([])

    const componentMap = {
      Sunny,
      Timer,
      Star,
      ChatDotRound,
      PriceTag,
      Avatar,
      User
    }

    const getImageUrl = (img) => {
      if (!img) return ''
      if (img.startsWith('http')) return img
      return 'http://localhost:8081' + img
    }

    const formatTime = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const now = new Date()
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)

      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      return date.toLocaleDateString('zh-CN')
    }

    const loadHotPosts = async () => {
      try {
        const data = await postAPI.getAllPosts()
        hotPosts.value = data
          .sort((a, b) => (b.likeCount || 0) + (b.commentCount || 0) - ((a.likeCount || 0) + (a.commentCount || 0)))
          .slice(0, 5)
      } catch (error) {
        console.error('Failed to load hot posts:', error)
      }
    }

    const loadRecentPosts = async () => {
      try {
        const data = await postAPI.getAllPosts()
        recentPosts.value = data
          .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
          .slice(0, 10)
      } catch (error) {
        console.error('Failed to load recent posts:', error)
      }
    }

    const loadHotTopics = () => {
      hotTopics.value = [
        { id: 1, name: '热门话题1', postCount: 1234 },
        { id: 2, name: '热门话题2', postCount: 892 },
        { id: 3, name: '热门话题3', postCount: 756 },
        { id: 4, name: '热门话题4', postCount: 634 },
        { id: 5, name: '热门话题5', postCount: 521 }
      ]
    }

    const loadHotUsers = () => {
      hotUsers.value = [
        { id: 1001, nickname: '用户A', followers: 12580 },
        { id: 1002, nickname: '用户B', followers: 8920 },
        { id: 1003, nickname: '用户C', followers: 6750 },
        { id: 1004, nickname: '用户D', followers: 5320 },
        { id: 1005, nickname: '用户E', followers: 4180 }
      ]
    }

    onMounted(() => {
      loadHotPosts()
      loadRecentPosts()
      loadHotTopics()
      loadHotUsers()
    })

    return {
      hotPosts,
      recentPosts,
      hotTopics,
      hotUsers,
      componentMap,
      getImageUrl,
      formatTime
    }
  }
})
</script>

<style scoped>
.explore-page {
  padding: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.trending-card,
.recent-card {
  border-radius: 12px;
}

.hot-posts {
  padding: 8px 0;
}

.hot-post-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.hot-post-item:last-child {
  border-bottom: none;
}

.post-rank {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  color: #999;
  background-color: #f0f0f0;
  border-radius: 4px;
  flex-shrink: 0;
}

.post-rank.top3 {
  color: #e6162d;
  background-color: #fff5f5;
}

.post-content p {
  margin: 0 0 8px;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.post-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.recent-posts {
  padding: 8px 0;
}

.recent-post-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.recent-post-item:last-child {
  border-bottom: none;
}

.post-body {
  flex: 1;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-images {
  display: flex;
  gap: 6px;
  margin: 8px 0;
}

.mini-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}

.post-actions {
  display: flex;
  gap: 20px;
}

.action-btn {
  color: #666;
}

.side-card {
  border-radius: 12px;
}

.hot-topics {
  padding: 8px 0;
}

.hot-topic-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.hot-topic-item:hover {
  background-color: #f5f5f5;
  padding: 10px;
  margin: 0 -10px;
  border-radius: 4px;
}

.topic-rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: #999;
  background-color: #f0f0f0;
  border-radius: 4px;
}

.topic-rank.top3 {
  color: #e6162d;
  background-color: #fff5f5;
}

.topic-info {
  flex: 1;
  overflow: hidden;
}

.topic-name {
  display: block;
  font-size: 13px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.topic-count {
  font-size: 11px;
  color: #999;
}

.hot-users {
  padding: 8px 0;
}

.hot-user-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.hot-user-item:last-child {
  border-bottom: none;
}

.hot-user-item .user-info {
  flex: 1;
}

.hot-user-item .user-name {
  display: block;
  font-size: 14px;
  font-weight: 500;
}

.user-followers {
  font-size: 12px;
  color: #999;
}

.follow-btn {
  margin-left: auto;
}
</style>
