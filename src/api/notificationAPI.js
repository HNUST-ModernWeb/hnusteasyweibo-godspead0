import api from './api'

export const notificationAPI = {
  getNotifications: (userId, limit = 20) => api.get(`/notification/user/${userId}?limit=${limit}`),
  getUnreadCount: (userId) => api.get(`/notification/unread/${userId}`),
  markAllAsRead: (userId) => api.post(`/notification/read/all/${userId}`),
  markAsRead: (id) => api.post(`/notification/read/${id}`)
}