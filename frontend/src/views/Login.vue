<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const isLogin = ref(true)
const loading = ref(false)

const form = reactive({ username: '', password: '', confirmPassword: '' })

async function handleSubmit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  if (!isLogin.value && form.password !== form.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  loading.value = true
  try {
    if (isLogin.value) {
      await userStore.login(form)
      ElMessage.success('登录成功')
    } else {
      await userStore.register(form)
      ElMessage.success('注册成功，请登录')
      isLogin.value = true
    }
  } catch (e) {
    // interceptor handles error
  } finally {
    loading.value = false
  }
}

function toggleMode() {
  isLogin.value = !isLogin.value
  form.username = ''
  form.password = ''
  form.confirmPassword = ''
}
</script>

<template>
  <div class="login-page">
    <div class="login-left">
      <div class="brand">
        <div class="logo">G</div>
        <h1>GraphRAG 学习助手</h1>
        <p class="subtitle">知识图谱 + AI 问答，让学习更高效</p>
        <div class="features">
          <div class="feature-item">
            <span class="dot"></span>
            <span>预置学科知识图谱，结构化学习路径</span>
          </div>
          <div class="feature-item">
            <span class="dot"></span>
            <span>上传个人资料，智能 RAG 问答</span>
          </div>
          <div class="feature-item">
            <span class="dot"></span>
            <span>多轮对话 + 流式输出，标注来源</span>
          </div>
        </div>
      </div>
    </div>

    <div class="login-right">
      <div class="form-card">
        <h2>{{ isLogin ? '欢迎回来' : '创建账号' }}</h2>
        <p class="form-tip">
          {{ isLogin ? '登录后开始你的学习之旅' : '注册即可体验全部功能' }}
        </p>

        <el-form @submit.prevent="handleSubmit" class="form">
          <el-form-item>
            <el-input
              v-model="form.username"
              placeholder="用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item v-if="!isLogin">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="确认密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleSubmit"
            class="submit-btn"
          >
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
        </el-form>

        <p class="switch-text">
          {{ isLogin ? '还没有账号？' : '已有账号？' }}
          <span class="switch-link" @click="toggleMode">
            {{ isLogin ? '立即注册' : '去登录' }}
          </span>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { User, Lock } from '@element-plus/icons-vue'
export default { components: { User, Lock } }
</script>

<style scoped>
.login-page {
  display: flex;
  height: 100vh;
  background: var(--bg);
}

.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4A90D9 0%, #6BA3E0 50%, #89B8E8 100%);
  color: white;
  padding: 60px;
}

.brand {
  max-width: 420px;
}

.logo {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 24px;
  backdrop-filter: blur(10px);
}

.brand h1 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 16px;
  opacity: 0.85;
  margin-bottom: 48px;
  line-height: 1.6;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  opacity: 0.9;
}

.dot {
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  flex-shrink: 0;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-card {
  width: 100%;
  max-width: 380px;
}

.form-card h2 {
  font-size: 26px;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 8px;
}

.form-tip {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 36px;
}

.form {
  margin-bottom: 24px;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  margin-top: 4px;
}

.switch-text {
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
}

.switch-link {
  color: var(--primary);
  cursor: pointer;
  font-weight: 500;
}

.switch-link:hover {
  text-decoration: underline;
}
</style>
