import api from './api'

export const topicAPI = {
  getHotTopics: (limit = 10) => api.get(`/topic/hot?limit=${limit}`),
  getTopicById: (id) => api.get(`/topic/${id}`),
  searchTopic: (name) => api.get(`/topic/search?name=${name}`),
  createTopic: (name, description = '') => api.post('/topic/create', { name, description }),
  getTopicsByPostId: (postId) => api.get(`/topic/post/${postId}`)
}