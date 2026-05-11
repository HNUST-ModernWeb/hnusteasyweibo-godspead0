<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <div class="logo-section">
        <el-icon size="48" color="#e6162d"><component :is="componentMap.Link" /></el-icon>
        <h1>简易微博</h1>
        <p>分享生活，记录美好</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" @submit.prevent="handleLogin">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                :prefix-icon="componentMap.User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="componentMap.Lock"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" native-type="submit" class="submit-btn" :loading="loading">
                登 录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" @submit.prevent="handleRegister">
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                :prefix-icon="componentMap.User"
              />
            </el-form-item>
            <el-form-item prop="nickname">
              <el-input
                v-model="registerForm.nickname"
                placeholder="请输入昵称"
                :prefix-icon="componentMap.UserFilled"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="componentMap.Lock"
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                :prefix-icon="componentMap.Lock"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" native-type="submit" class="submit-btn" :loading="loading">
                注 册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, defineComponent } from 'vue'
import { useRouter } from 'vue-router'
import { userAPI } from '../api/userAPI'
import { Link, User, UserFilled, Lock } from '@element-plus/icons-vue'

export default defineComponent({
  name: 'Login',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const loginFormRef = ref(null)
    const registerFormRef = ref(null)
    const activeTab = ref('login')

    const componentMap = {
      Link,
      User,
      UserFilled,
      Lock
    }

    const loginForm = reactive({
      username: '',
      password: ''
    })

    const registerForm = reactive({
      username: '',
      nickname: '',
      password: '',
      confirmPassword: ''
    })

    const loginRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在3-20位', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码至少6位', trigger: 'blur' }
      ]
    }

    const registerRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在3-20位', trigger: 'blur' }
      ],
      nickname: [
        { required: true, message: '请输入昵称', trigger: 'blur' },
        { min: 2, max: 20, message: '昵称长度在2-20位', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码至少6位', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== registerForm.password) {
              callback(new Error('两次密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    }

    const handleLogin = async () => {
      if (!loginFormRef.value) return
      try {
        await loginFormRef.value.validate()
        loading.value = true

        const user = await userAPI.login(loginForm.username, loginForm.password)
        localStorage.setItem('user', JSON.stringify(user))
        localStorage.setItem('token', user.token)

        router.push('/')
      } catch (error) {
        console.error('Login failed:', error)
        if (error.response?.data?.message) {
          alert(error.response.data.message)
        }
      } finally {
        loading.value = false
      }
    }

    const handleRegister = async () => {
      if (!registerFormRef.value) return
      try {
        await registerFormRef.value.validate()
        loading.value = true

        const user = await userAPI.register({
          username: registerForm.username,
          password: registerForm.password,
          nickname: registerForm.nickname
        })
        localStorage.setItem('user', JSON.stringify(user))
        localStorage.setItem('token', user.token)

        router.push('/')
      } catch (error) {
        console.error('Registration failed:', error)
        if (error.response?.data?.message) {
          alert(error.response.data.message)
        }
      } finally {
        loading.value = false
      }
    }

    return {
      componentMap,
      loading,
      activeTab,
      loginForm,
      registerForm,
      loginRules,
      registerRules,
      loginFormRef,
      registerFormRef,
      handleLogin,
      handleRegister
    }
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 12px;
  overflow: hidden;
}

.logo-section {
  text-align: center;
  padding: 30px 20px;
  background: linear-gradient(135deg, #e6162d 0%, #ff4d4f 100%);
  color: white;
}

.logo-section h1 {
  margin: 12px 0 8px;
  font-size: 24px;
  font-weight: bold;
}

.logo-section p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.login-tabs :deep(.el-tabs__header) {
  margin: 0;
}

.login-tabs :deep(.el-tabs__content) {
  padding: 24px;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: bold;
  background: linear-gradient(135deg, #e6162d 0%, #ff4d4f 100%);
  border: none;
}

.submit-btn:hover {
  opacity: 0.9;
}
</style>
