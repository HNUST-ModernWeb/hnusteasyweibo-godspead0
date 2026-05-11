import api from './api'

export const postAPI = {
  getAllPosts: () => api.get('/post'),
  createPost: (post) => api.post('/post', post),
  getPostById: (id) => api.get(`/post/${id}`),
  updatePost: (post) => api.put('/post', post),
  deletePost: (id) => api.delete(`/post/${id}`),
  getPostsByUserId: (userId) => api.get(`/post/user/${userId}`),
  toggleLike: (postId, userId) => api.post(`/like/toggle?postId=${postId}&userId=${userId}`),
  getLikeStatus: (postId, userId) => api.get(`/like/status?postId=${postId}&userId=${userId}`)
}