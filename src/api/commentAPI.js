import api from './api'

export const commentAPI = {
  getCommentsByPostId: (postId) => api.get(`/comment/post/${postId}`),
  createComment: (comment) => api.post('/comment', comment),
  deleteComment: (id) => api.delete(`/comment/${id}`)
}