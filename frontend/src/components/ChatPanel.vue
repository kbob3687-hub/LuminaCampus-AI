<script setup>
import { ref, nextTick, watch } from 'vue'
import { useChatStore } from '../stores/chat'
import ChatMessage from './ChatMessage.vue'

const chatStore = useChatStore()
const inputText = ref('')
const chatArea = ref(null)

watch(
  () => chatStore.messages.length,
  () => {
    nextTick(() => {
      if (chatArea.value) {
        chatArea.value.scrollTop = chatArea.value.scrollHeight
      }
    })
  }
)

async function handleSend() {
  const text = inputText.value.trim()
  if (!text) return
  inputText.value = ''
  await chatStore.send(text)
}

function handleKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}
</script>

<template>
  <div class="chat-panel">
    <!-- 欢迎区域 -->
    <div v-if="!chatStore.messages.length" class="welcome">
      <div class="welcome-icon">G</div>
      <h2>GraphRAG 学习助手</h2>
      <p>选择一个学科标签，或上传你的资料，然后开始提问吧</p>
      <div class="quick-questions">
        <div
          class="quick-item"
          v-for="q in [
            '二叉树有哪些常见的遍历方式？',
            'TCP 和 UDP 的区别是什么？',
            '什么是导数的几何意义？',
          ]"
          :key="q"
          @click="inputText = q"
        >
          <el-icon><ChatDotRound /></el-icon>
          <span>{{ q }}</span>
        </div>
      </div>
    </div>

    <!-- 消息列表 -->
    <div v-else class="messages" ref="chatArea">
      <ChatMessage
        v-for="(msg, i) in chatStore.messages"
        :key="i"
        :message="msg"
      />
      <div v-if="chatStore.loading" class="typing-indicator">
        <span></span><span></span><span></span>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-wrapper">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="1"
          :autosize="{ minRows: 1, maxRows: 4 }"
          placeholder="输入你的问题... (Enter 发送，Shift+Enter 换行)"
          @keydown="handleKeydown"
          resize="none"
          class="chat-input"
        />
        <el-button
          type="primary"
          circle
          :icon="Promotion"
          :loading="chatStore.loading"
          @click="handleSend"
          class="send-btn"
          :disabled="!inputText.trim()"
        />
      </div>
      <div class="input-hint">
        基于 LangGraph + Neo4j 知识图谱 · 支持多轮对话
      </div>
    </div>
  </div>
</template>

<script>
import { Promotion } from '@element-plus/icons-vue'
export default { components: { Promotion } }
</script>

<style scoped>
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 欢迎页 */
.welcome {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.welcome-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  color: white;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 20px;
  box-shadow: 0 8px 24px rgba(74, 144, 217, 0.2);
}

.welcome h2 {
  font-size: 22px;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 8px;
}

.welcome p {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 32px;
}

.quick-questions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  max-width: 400px;
}

.quick-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: var(--bg-white);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 13px;
  color: var(--text-secondary);
  transition: all 0.15s;
}

.quick-item:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

/* 消息列表 */
.messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

/* 打字动画 */
.typing-indicator {
  display: flex;
  gap: 6px;
  padding: 12px 18px;
  margin-left: 48px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: var(--text-light);
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

/* 输入区域 */
.input-area {
  padding: 16px 24px 20px;
  background: var(--bg-white);
  border-top: 1px solid var(--border);
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 8px 8px 8px 16px;
  transition: border-color 0.15s;
}

.input-wrapper:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(74, 144, 217, 0.1);
}

.chat-input {
  flex: 1;
}

.chat-input :deep(.el-textarea__inner) {
  background: transparent;
  border: none;
  box-shadow: none;
  padding: 6px 0;
  line-height: 1.6;
}

.send-btn {
  flex-shrink: 0;
  width: 38px;
  height: 38px;
}

.input-hint {
  text-align: center;
  font-size: 11px;
  color: var(--text-light);
  margin-top: 8px;
}
</style>
