<template>
  <div class="publish-page">
    <el-card class="publish-card" shadow="hover">
      <template #header>
        <span class="publish-title">发布新内容</span>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="分享你的想法..."
            class="content-input"
          />
        </el-form-item>

        <div class="toolbar">
          <el-button
            type="text"
            class="toolbar-btn"
            @click="showEmojiPicker = !showEmojiPicker"
          >
            <el-icon :size="20" color="#666"><component :is="componentMap.ChatDotRound" /></el-icon>
            <span>表情</span>
          </el-button>
          <el-button
            type="text"
            class="toolbar-btn"
            @click="showTopicPicker = !showTopicPicker"
          >
            <el-icon :size="20" color="#666"><component :is="componentMap.PriceTag" /></el-icon>
            <span>话题</span>
          </el-button>
        </div>

        <div v-if="showEmojiPicker" class="emoji-picker">
          <div class="emoji-grid">
            <span
              v-for="emoji in emojis"
              :key="emoji"
              class="emoji-item"
              @click="insertEmoji(emoji)"
            >{{ emoji }}</span>
          </div>
        </div>

        <div v-if="showTopicPicker" class="topic-picker">
          <el-input
            v-model="topicSearch"
            placeholder="搜索话题..."
            class="topic-search"
          />
          <div class="topic-list">
            <div
              v-for="topic in filteredTopics"
              :key="topic.id"
              class="topic-item"
              @click="insertTopic(topic.name)"
            >
              <span class="topic-name">#{{ topic.name }}</span>
              <span class="topic-count">{{ topic.postCount }} 讨论</span>
            </div>
          </div>
        </div>

        <div class="image-upload-section">
          <el-upload
            :action="uploadUrl"
            :file-list="fileList"
            :before-upload="beforeUpload"
            :on-success="onUploadSuccess"
            :on-error="onUploadError"
            :on-progress="onUploadProgress"
            :multiple="true"
            :limit="9"
            :auto-upload="false"
            class="image-upload"
            ref="uploadRef"
          >
            <el-button type="text" class="upload-btn">
              <el-icon :size="24" color="#666"><component :is="componentMap.Picture" /></el-icon>
              <span>上传图片</span>
            </el-button>
            <template #tip>
              <span class="upload-tip">支持 jpg、png、gif 格式，单张不超过 10MB，最多上传9张</span>
            </template>
          </el-upload>
        </div>

        <div v-if="uploadProgress > 0" class="upload-progress-bar">
          <el-progress
            :percentage="uploadProgress"
            :status="uploadProgress === 100 ? 'success' : 'active'"
            :show-text="true"
          />
        </div>

        <div class="form-actions">
          <el-button @click="handleCancel" :disabled="loading">取消</el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            :loading="loading || uploading"
            class="submit-btn"
          >
            <template #loading>
              <span v-if="uploading">上传中...</span>
              <span v-else>发布中...</span>
            </template>
            <span v-if="!loading && !uploading">发布</span>
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, defineComponent, watch } from 'vue'
import { useRouter } from 'vue-router'
import { postAPI } from '../api/postAPI'
import { ChatDotRound, PriceTag, Picture } from '@element-plus/icons-vue'

export default defineComponent({
  name: 'Publish',
  setup() {
    const router = useRouter()
    const form = reactive({
      content: ''
    })
    const loading = ref(false)
    const uploading = ref(false)
    const uploadProgress = ref(0)
    const showEmojiPicker = ref(false)
    const showTopicPicker = ref(false)
    const topicSearch = ref('')
    const fileList = ref([])
    const uploadedUrls = ref([])
    const formRef = ref(null)
    const uploadRef = ref(null)

    const componentMap = {
      ChatDotRound,
      PriceTag,
      Picture
    }

    const emojis = [
      '😀', '😃', '😄', '😁', '😆', '😅', '🤣', '😂',
      '🙂', '😊', '😇', '🥰', '😍', '🤩', '😘', '😗',
      '😚', '😋', '😛', '😝', '🤑', '🤗', '🤭', '🤫',
      '🤔', '🤐', '🤨', '😐', '😑', '😶', '😏', '😒',
      '🙄', '😬', '🤥', '😌', '😔', '😪', '🤤', '😴',
      '😷', '🤒', '🤕', '🤢', '🤮', '🥵', '🥶', '🥴',
      '😵', '🤯', '🤠', '🥳', '🥸', '😎', '🤓', '🧐',
      '😕', '😟', '🙁', '☹️', '😮‍💨', '😥', '😢', '😭',
      '😱', '😨', '😰', '😥', '🤯', '😤', '😡', '😠',
      '🤬', '😈', '👿', '💀', '☠️', '💩', '🤡', '👹',
      '👺', '👻', '👽', '👾', '🤖', '🎃', '😺', '😸',
      '😹', '😻', '😼', '😽', '🙀', '😿', '😾', '🐱'
    ]

    const topics = [
      { id: 1, name: '热门话题', postCount: 1234 },
      { id: 2, name: '技术分享', postCount: 892 },
      { id: 3, name: '生活日常', postCount: 756 },
      { id: 4, name: '美食探店', postCount: 634 },
      { id: 5, name: '旅行打卡', postCount: 521 },
      { id: 6, name: '学习笔记', postCount: 445 },
      { id: 7, name: '影视推荐', postCount: 389 },
      { id: 8, name: '音乐分享', postCount: 321 }
    ]

    const filteredTopics = ref(topics)

    watch(topicSearch, (val) => {
      if (val) {
        filteredTopics.value = topics.filter(t =>
          t.name.toLowerCase().includes(val.toLowerCase())
        )
      } else {
        filteredTopics.value = topics
      }
    })

    const rules = {
      content: [
        { required: true, message: '请输入内容', trigger: 'blur' },
        { min: 1, max: 2000, message: '内容长度在1-2000字之间', trigger: 'blur' }
      ]
    }

    const uploadUrl = 'http://localhost:8081/api/file/upload'

    const beforeUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      if (!isImage) {
        alert('只支持图片文件')
        return false
      }
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        alert('文件大小不能超过10MB')
        return false
      }
      return true
    }

    const onUploadSuccess = (response, file, fileList) => {
      if (response.success) {
        uploadedUrls.value.push(response.url)
      }
      if (fileList.length === fileList.filter(f => f.status === 'success').length) {
        uploading.value = false
        uploadProgress.value = 100
        submitPost()
      }
    }

    const onUploadError = (error, file, fileList) => {
      console.error('Upload error:', error)
      alert('图片上传失败')
      uploading.value = false
    }

    const onUploadProgress = (event, file, fileList) => {
      const progress = Math.round((event.loaded / event.total) * 100)
      uploadProgress.value = progress
    }

    const insertEmoji = (emoji) => {
      form.content += emoji
      showEmojiPicker.value = false
    }

    const insertTopic = (topicName) => {
      form.content += `#${topicName}# `
      showTopicPicker.value = false
      topicSearch.value = ''
    }

    const submitPost = async () => {
      try {
        const currentUser = JSON.parse(localStorage.getItem('user'))
        if (!currentUser) {
          alert('请先登录')
          return
        }

        const post = {
          content: form.content,
          imageUrls: uploadedUrls.value.length > 0 ? uploadedUrls.value.join(',') : null,
          userId: currentUser.id
        }

        await postAPI.createPost(post)
        alert('发布成功！')
        router.push('/')
      } catch (error) {
        console.error('Failed to publish post:', error)
        alert(error.response?.data?.message || '发布失败，请重试')
      } finally {
        loading.value = false
      }
    }

    const handleSubmit = async () => {
      if (!formRef.value) return

      try {
        await formRef.value.validate()
        loading.value = true

        const pendingFiles = fileList.value.filter(f => f.status !== 'success')
        if (pendingFiles.length > 0) {
          uploading.value = true
          uploadProgress.value = 0
          uploadRef.value.submit()
        } else {
          await submitPost()
        }
      } catch (error) {
        console.error('Validation failed:', error)
        loading.value = false
      }
    }

    const handleCancel = () => {
      router.push('/')
    }

    return {
      form,
      loading,
      uploading,
      uploadProgress,
      showEmojiPicker,
      showTopicPicker,
      topicSearch,
      fileList,
      filteredTopics,
      emojis,
      formRef,
      uploadRef,
      componentMap,
      uploadUrl,
      beforeUpload,
      onUploadSuccess,
      onUploadError,
      onUploadProgress,
      insertEmoji,
      insertTopic,
      handleSubmit,
      handleCancel
    }
  }
})
</script>

<style scoped>
.publish-page {
  padding: 20px;
}

.publish-card {
  max-width: 600px;
  margin: 0 auto;
  border-radius: 12px;
}

.publish-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.content-input {
  font-size: 15px;
  line-height: 1.6;
}

.content-input :deep(.el-textarea__inner) {
  min-height: 120px;
}

.toolbar {
  display: flex;
  gap: 20px;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.toolbar-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 0;
  color: #666;
}

.toolbar-btn:hover {
  color: #e6162d;
}

.emoji-picker {
  padding: 12px;
  background-color: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 12px;
}

.emoji-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.emoji-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.emoji-item:hover {
  background-color: #f5f5f5;
}

.topic-picker {
  padding: 12px;
  background-color: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 12px;
}

.topic-search {
  margin-bottom: 12px;
}

.topic-list {
  max-height: 200px;
  overflow-y: auto;
}

.topic-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.topic-item:hover {
  background-color: #f5f5f5;
}

.topic-name {
  font-size: 14px;
  color: #333;
}

.topic-count {
  font-size: 12px;
  color: #999;
}

.image-upload-section {
  padding: 12px 0;
}

.upload-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 0;
  color: #666;
}

.upload-btn:hover {
  color: #e6162d;
}

.upload-tip {
  font-size: 12px;
  color: #999;
}

.upload-progress-bar {
  margin-bottom: 16px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
}

.submit-btn {
  background: linear-gradient(135deg, #e6162d 0%, #ff4d4f 100%);
  border: none;
  padding: 8px 24px;
}

.submit-btn:hover {
  opacity: 0.9;
}
</style>
