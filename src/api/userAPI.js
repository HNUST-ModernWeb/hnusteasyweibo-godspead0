import api from './api'

export const userAPI = {
  login: (username, password) => api.post('/user/login', { username, password }),
  register: (user) => api.post('/user/register', user),
  getUserById: (id) => api.get(`/user/${id}`),
  updateUser: (user) => api.put('/user', user)
}