<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useChatStore } from '../stores/chat'
import { getConversations } from '../api/chat'
import ChatPanel from '../components/ChatPanel.vue'
import SubjectTags from '../components/SubjectTags.vue'
import UploadDialog from '../components/UploadDialog.vue'

const route = useRoute()
const userStore = useUserStore()
const chatStore = useChatStore()

const conversations = ref([])
const showUpload = ref(false)
const sidebarCollapsed = ref(false)

onMounted(async () => {
  try {
    const res = await getConversations()
    conversations.value = res.data || []
  } catch (e) {
    // 未登录或请求失败，忽略
  }
})

function handleSubjectChange(subject) {
  chatStore.setSubject(subject)
}

function handleNewChat() {
  chatStore.clearMessages()
  chatStore.currentSubject = ''
}

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}
</script>

<template>
  <div class="home">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="sidebar-brand" v-if="!sidebarCollapsed">
          <div class="logo-sm">G</div>
          <span>GraphRAG</span>
        </div>
        <el-button
          :icon="sidebarCollapsed ? 'Expand' : 'Fold'"
          text
          circle
          @click="toggleSidebar"
          class="toggle-btn"
        />
      </div>

      <el-button
        class="new-chat-btn"
        type="primary"
        plain
        @click="handleNewChat"
        v-if="!sidebarCollapsed"
      >
        <el-icon><Plus /></el-icon>
        新对话
      </el-button>

      <div class="conversation-list" v-if="!sidebarCollapsed">
        <div
          v-for="conv in conversations"
          :key="conv.id"
          class="conv-item"
        >
          <el-icon class="conv-icon"><ChatDotRound /></el-icon>
          <div class="conv-info">
            <span class="conv-title">{{ conv.question }}</span>
            <span class="conv-meta" v-if="conv.subject">{{ conv.subject }}</span>
          </div>
        </div>
        <div v-if="!conversations.length" class="empty-conv">
          <span>暂无对话记录</span>
        </div>
      </div>

      <div class="sidebar-footer" v-if="!sidebarCollapsed">
        <div class="user-info">
          <el-avatar :size="32" class="user-avatar">
            {{ userStore.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <span class="username">{{ userStore.username }}</span>
        </div>
        <el-button text @click="userStore.logout" class="logout-btn">
          <el-icon><SwitchButton /></el-icon>
        </el-button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="main">
      <header class="main-header">
        <SubjectTags
          :active="chatStore.currentSubject"
          @change="handleSubjectChange"
        />
        <el-button
          class="upload-btn"
          @click="showUpload = true"
          plain
        >
          <el-icon><Upload /></el-icon>
          上传资料
        </el-button>
      </header>

      <ChatPanel />

      <UploadDialog v-model="showUpload" />
    </main>
  </div>
</template>

<style scoped>
.home {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  width: 280px;
  background: var(--bg-white);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: 56px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid var(--border);
  min-height: 56px;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 15px;
  color: var(--text);
}

.logo-sm {
  width: 32px;
  height: 32px;
  background: var(--primary);
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
}

.toggle-btn {
  color: var(--text-secondary);
}

.new-chat-btn {
  margin: 12px 16px;
  border-style: dashed;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.conv-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: background 0.15s;
}

.conv-item:hover {
  background: var(--primary-bg);
}

.conv-icon {
  color: var(--text-light);
  margin-top: 2px;
  flex-shrink: 0;
}

.conv-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.conv-title {
  font-size: 13px;
  color: var(--text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conv-meta {
  font-size: 11px;
  color: var(--text-light);
}

.empty-conv {
  padding: 40px 16px;
  text-align: center;
  color: var(--text-light);
  font-size: 13px;
}

.sidebar-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-top: 1px solid var(--border);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  background: var(--primary);
  color: white;
  font-size: 14px;
  font-weight: 600;
}

.username {
  font-size: 13px;
  color: var(--text);
  font-weight: 500;
}

.logout-btn {
  color: var(--text-light);
}

.logout-btn:hover {
  color: #E74C3C;
}

/* 主内容区 */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--bg);
}

.main-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  background: var(--bg-white);
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
}

.upload-btn {
  flex-shrink: 0;
}
</style>
