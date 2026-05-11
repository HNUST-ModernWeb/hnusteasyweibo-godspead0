<template>
  <div class="home-page">
    <el-row :gutter="20">
      <el-col :span="16">
        <div class="posts-list">
          <el-card
            v-for="post in posts"
            :key="post.id"
            class="post-card"
            shadow="hover"
          >
            <div class="post-header">
              <el-avatar :size="48" :icon="componentMap.User" class="user-avatar" />
              <div class="user-info">
                <span class="user-name">{{ post.user?.nickname || '用户' + post.userId }}</span>
                <span class="post-time">{{ formatTime(post.createdAt) }}</span>
              </div>
            </div>

            <div class="post-content">
              <p>{{ post.content }}</p>
              <div v-if="post.topics && post.topics.length > 0" class="post-topics">
                <el-tag
                  v-for="topic in post.topics"
                  :key="topic.id"
                  type="info"
                  size="small"
                  class="topic-tag"
                  @click="searchTopic(topic.name)"
                >
                  #{{ topic.name }}
                </el-tag>
              </div>
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

            <div class="post-actions">
              <el-button
                @click="likePost(post.id)"
                :class="{ 'liked': post.isLiked }"
                type="text"
                class="action-btn"
              >
                <el-icon :size="18" :color="post.isLiked ? '#e6162d' : '#999'">
                  <component :is="post.isLiked ? componentMap.StarFilled : componentMap.Star" />
                </el-icon>
                <span :style="{ color: post.isLiked ? '#e6162d' : '#666' }">{{ post.likeCount || 0 }}</span>
              </el-button>
              <el-button
                @click="toggleComments(post.id)"
                type="text"
                class="action-btn"
              >
                <el-icon :size="18" color="#666"><component :is="componentMap.ChatDotRound" /></el-icon>
                <span>{{ post.commentCount || 0 }}</span>
              </el-button>
              <el-button
                type="text"
                class="action-btn"
              >
                <el-icon :size="18" color="#666"><component :is="componentMap.Share" /></el-icon>
                <span>分享</span>
              </el-button>
              <el-button
                  type="text"
                  class="action-btn"
                >
                  <el-icon :size="18" color="#666"><component :is="componentMap.Promotion" /></el-icon>
                  <span>评论</span>
                </el-button>
            </div>

            <el-collapse v-model="expandedPosts" :accordion="false">
              <el-collapse-item :name="String(post.id)" class="comments-collapse">
                <template #title>
                  <span class="comments-title">
                    <el-icon :size="16" color="#666"><component :is="componentMap.ChatDotRound" /></el-icon>
                    评论 ({{ post.commentCount || 0 }})
                  </span>
                </template>

                <div v-if="post.comments.length === 0" class="no-comments">
                  <el-empty description="暂无评论，快来发表第一条评论吧！" />
                </div>

                <div v-else class="comments-list">
                  <div
                    v-for="comment in post.comments"
                    :key="comment.id"
                    class="comment-item"
                  >
                    <div class="comment-header">
                      <el-avatar :size="36" :icon="componentMap.User" class="comment-avatar" />
                      <div class="comment-info">
                        <span class="comment-user">{{ comment.user?.nickname || '用户' + comment.userId }}</span>
                        <span class="comment-floor">#{{ getCommentFloor(post, comment) }}楼</span>
                        <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                      </div>
                    </div>
                    <div class="comment-content">{{ comment.content }}</div>
                    <div class="comment-actions">
                      <el-button
                        @click="startReply(post.id, comment)"
                        type="text"
                        size="small"
                        class="reply-btn"
                      >
                        <el-icon :size="14"><component :is="componentMap.Promotion" /></el-icon>
                        回复
                      </el-button>
                    </div>

                    <div v-if="replyingTo[post.id]?.id === comment.id" class="reply-input-box">
                      <el-input
                        v-model="replyContent[post.id]"
                        :placeholder="'回复 @' + (comment.user?.nickname || '用户')"
                        @keyup.enter="submitReply(post.id)"
                        size="small"
                      />
                      <el-button
                        @click="submitReply(post.id)"
                        type="primary"
                        size="small"
                        class="reply-submit-btn"
                      >
                        发送
                      </el-button>
                      <el-button
                        @click="cancelReply(post.id)"
                        size="small"
                      >
                        取消
                      </el-button>
                    </div>

                    <div v-if="comment.replies && comment.replies.length > 0" class="comment-replies">
                      <div
                        v-for="reply in comment.replies"
                        :key="reply.id"
                        class="reply-item"
                      >
                        <el-avatar :size="28" :icon="componentMap.User" class="reply-avatar" />
                        <div class="reply-body">
                          <div class="reply-header">
                            <span class="reply-user">{{ reply.user?.nickname || '用户' + reply.userId }}</span>
                            <span v-if="reply.replyToUser" class="reply-to">@{{ reply.replyToUser.nickname }}</span>
                            <span class="reply-time">{{ formatTime(reply.createdAt) }}</span>
                          </div>
                          <div class="reply-content">{{ reply.content }}</div>
                          <div class="reply-actions">
                            <el-button
                              @click="startReply(post.id, reply, comment)"
                              type="text"
                              size="small"
                              class="reply-btn"
                            >
                              <el-icon :size="12"><component :is="componentMap.Promotion" /></el-icon>
                              回复
                            </el-button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="comment-input-area">
                  <el-input
                    v-model="newComments[post.id]"
                    placeholder="写下你的评论..."
                    @keyup.enter="submitComment(post.id)"
                  />
                  <el-button
                    @click="submitComment(post.id)"
                    type="primary"
                    class="comment-submit-btn"
                  >
                    发送
                  </el-button>
                </div>
              </el-collapse-item>
            </el-collapse>
          </el-card>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="side-bar">
          <el-card class="side-card" shadow="hover">
            <template #header>
              <span class="side-title">热门话题</span>
            </template>
            <div class="hot-topics">
              <div
                v-for="(topic, index) in hotTopics"
                :key="topic.id"
                class="hot-topic-item"
                @click="searchTopic(topic.name)"
              >
                <span class="topic-rank" :class="{ 'top3': index < 3 }">{{ index + 1 }}</span>
                <div class="topic-info">
                  <span class="topic-name">#{{ topic.name }}</span>
                  <span class="topic-count">{{ topic.postCount }} 讨论</span>
                </div>
              </div>
            </div>
          </el-card>

          <el-card class="side-card" shadow="hover">
            <template #header>
              <span class="side-title">推荐关注</span>
            </template>
            <div class="recommend-users">
              <div v-for="user in recommendUsers" :key="user.id" class="recommend-user">
                <el-avatar :size="40" :icon="componentMap.User" />
                <div class="user-info">
                  <span class="user-name">{{ user.nickname }}</span>
                  <span class="user-desc">{{ user.bio || '暂无简介' }}</span>
                </div>
                <el-button type="primary" size="small" class="follow-btn">
                  + 关注
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onMounted, defineComponent } from 'vue'
import { useRouter } from 'vue-router'
import { postAPI } from '../api/postAPI'
import { commentAPI } from '../api/commentAPI'
import { topicAPI } from '../api/topicAPI'
import {
  User,
  Star,
  StarFilled,
  ChatDotRound,
  Share,
  Promotion
} from '@element-plus/icons-vue'

export default defineComponent({
  name: 'Home',
  setup() {
    const router = useRouter()
    const posts = ref([])
    const expandedPosts = ref([])
    const newComments = reactive({})
    const replyContent = reactive({})
    const replyingTo = reactive({})
    const hotTopics = ref([])
    const recommendUsers = ref([])

    const componentMap = {
      User,
      Star,
      StarFilled,
      ChatDotRound,
      Share,
      Promotion
    }

    const getImageUrl = (img) => {
      if (!img) return ''
      if (img.startsWith('http')) return img
      return 'http://localhost:8081' + img
    }

    const formatTime = (dateStr) => {
      if (!dateStr) return '未知时间'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const loadPosts = async () => {
      try {
        const data = await postAPI.getAllPosts()
        posts.value = data.map(post => ({
          ...post,
          comments: [],
          isLiked: false
        }))
        await Promise.all(posts.value.map(post => loadComments(post.id)))
      } catch (error) {
        console.error('Failed to load posts:', error)
      }
    }

    const loadComments = async (postId) => {
      try {
        const comments = await commentAPI.getCommentsByPostId(postId)
        const post = posts.value.find(p => p.id === postId)
        if (post) {
          post.comments = comments
        }
      } catch (error) {
        console.error('Failed to load comments:', error)
      }
    }

    const loadHotTopics = async () => {
      try {
        hotTopics.value = [
          { id: 1, name: '热门话题1', postCount: 1234 },
          { id: 2, name: '热门话题2', postCount: 892 },
          { id: 3, name: '热门话题3', postCount: 756 },
          { id: 4, name: '热门话题4', postCount: 634 },
          { id: 5, name: '热门话题5', postCount: 521 }
        ]
      } catch (error) {
        console.error('Failed to load hot topics:', error)
      }
    }

    const loadRecommendUsers = async () => {
      try {
        recommendUsers.value = [
          { id: 1001, nickname: '用户A', bio: '热爱生活' },
          { id: 1002, nickname: '用户B', bio: '技术博主' },
          { id: 1003, nickname: '用户C', bio: '美食爱好者' }
        ]
      } catch (error) {
        console.error('Failed to load recommend users:', error)
      }
    }

    const searchTopic = (topicName) => {
      alert(`搜索话题: #${topicName}`)
    }

    const likePost = async (id) => {
      const post = posts.value.find(p => p.id === id)
      if (!post) return

      const currentUser = JSON.parse(localStorage.getItem('user'))
      if (!currentUser) {
        router.push('/login')
        return
      }

      try {
        const response = await postAPI.toggleLike(id, currentUser.id)
        post.isLiked = response.liked
        post.likeCount = response.count
      } catch (error) {
        console.error('Failed to like post:', error)
      }
    }

    const toggleComments = (postId) => {
      const index = expandedPosts.value.indexOf(String(postId))
      if (index > -1) {
        expandedPosts.value.splice(index, 1)
      } else {
        expandedPosts.value.push(String(postId))
      }
    }

    const startReply = (postId, comment, parentComment = null) => {
      replyingTo[postId] = {
        id: comment.id,
        userId: comment.userId,
        parentId: parentComment ? parentComment.id : comment.id,
        rootId: comment.rootId || comment.id
      }
      replyContent[postId] = ''
    }

    const cancelReply = (postId) => {
      delete replyingTo[postId]
      replyContent[postId] = ''
    }

    const submitComment = async (postId) => {
      const content = newComments[postId]
      if (!content || content.trim() === '') return

      const post = posts.value.find(p => p.id === postId)
      if (!post) return

      try {
        const currentUser = JSON.parse(localStorage.getItem('user'))
        if (!currentUser) {
          router.push('/login')
          return
        }
        const comment = {
          postId,
          userId: currentUser.id,
          parentId: 0,
          rootId: 0,
          content: content.trim()
        }
        await commentAPI.createComment(comment)
        await loadComments(postId)
        post.commentCount = (post.commentCount || 0) + 1
        newComments[postId] = ''
      } catch (error) {
        console.error('Failed to create comment:', error)
      }
    }

    const submitReply = async (postId) => {
      const content = replyContent[postId]
      if (!content || content.trim() === '') return

      const post = posts.value.find(p => p.id === postId)
      if (!post) return

      const replyTo = replyingTo[postId]
      if (!replyTo) return

      try {
        const currentUser = JSON.parse(localStorage.getItem('user'))
        if (!currentUser) {
          router.push('/login')
          return
        }
        const comment = {
          postId,
          userId: currentUser.id,
          parentId: replyTo.id,
          rootId: replyTo.rootId || replyTo.id,
          content: content.trim()
        }
        await commentAPI.createComment(comment)
        await loadComments(postId)
        post.commentCount = (post.commentCount || 0) + 1
        cancelReply(postId)
      } catch (error) {
        console.error('Failed to create reply:', error)
      }
    }

    const getCommentFloor = (post, comment) => {
      const index = post.comments.findIndex(c => c.id === comment.id)
      return index + 1
    }

    onMounted(() => {
      loadPosts()
      loadHotTopics()
      loadRecommendUsers()
    })

    return {
      posts,
      expandedPosts,
      newComments,
      replyContent,
      replyingTo,
      hotTopics,
      recommendUsers,
      componentMap,
      getImageUrl,
      formatTime,
      searchTopic,
      likePost,
      toggleComments,
      startReply,
      cancelReply,
      submitComment,
      submitReply,
      getCommentFloor
    }
  }
})
</script>

<style scoped>
.home-page {
  padding: 20px;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-card {
  border-radius: 12px;
  overflow: hidden;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.user-avatar {
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 600;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-content {
  padding: 12px 0;
}

.post-content p {
  margin: 0;
  line-height: 1.6;
}

.post-topics {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.topic-tag {
  cursor: pointer;
}

.post-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 12px 0;
}

.post-image {
  width: calc(33.33% - 6px);
  height: 120px;
  border-radius: 8px;
  cursor: pointer;
}

.post-actions {
  display: flex;
  gap: 20px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
}

.action-btn.liked {
  color: #e6162d;
}

.comments-collapse {
  border-top: 1px solid #f0f0f0;
}

.comments-title {
  display: flex;
  align-items: center;
  gap: 6px;
}

.no-comments {
  padding: 20px;
}

.comments-list {
  padding: 12px 0;
}

.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.comment-user {
  font-weight: 500;
  color: #333;
}

.comment-floor {
  font-size: 12px;
  color: #ff6b6b;
  background-color: #fff5f5;
  padding: 2px 6px;
  border-radius: 2px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  padding: 8px 0 8px 46px;
  font-size: 14px;
  line-height: 1.6;
}

.comment-actions {
  padding-left: 46px;
}

.reply-btn {
  color: #666;
}

.reply-input-box {
  display: flex;
  gap: 8px;
  padding: 12px 0 12px 46px;
}

.reply-submit-btn {
  flex-shrink: 0;
}

.comment-replies {
  margin-top: 12px;
  padding-left: 46px;
  border-left: 2px solid #e8e8e8;
}

.reply-item {
  display: flex;
  gap: 8px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-avatar {
  flex-shrink: 0;
}

.reply-body {
  flex: 1;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.reply-user {
  font-weight: 500;
  font-size: 13px;
}

.reply-to {
  font-size: 12px;
  color: #666;
}

.reply-time {
  font-size: 11px;
  color: #999;
}

.reply-content {
  font-size: 13px;
  line-height: 1.5;
  margin-top: 4px;
}

.comment-input-area {
  display: flex;
  gap: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.comment-submit-btn {
  flex-shrink: 0;
}

.side-bar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.side-card {
  border-radius: 12px;
}

.side-title {
  font-weight: 600;
  color: #333;
}

.hot-topics {
  padding: 8px 0;
}

.hot-topic-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.hot-topic-item:hover {
  background-color: #f5f5f5;
  padding: 8px;
  margin: 0 -8px;
  border-radius: 4px;
}

.topic-rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
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

.recommend-users {
  padding: 8px 0;
}

.recommend-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.recommend-user:last-child {
  border-bottom: none;
}

.follow-btn {
  margin-left: auto;
}
</style>
