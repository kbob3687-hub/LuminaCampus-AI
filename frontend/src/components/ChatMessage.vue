<script setup>
import { computed } from 'vue'
import { marked } from 'marked'

const props = defineProps({
  message: { type: Object, required: true },
})

const isUser = computed(() => props.message.role === 'user')
const renderedContent = computed(() => {
  if (isUser.value) return props.message.content
  return marked.parse(props.message.content || '', { breaks: true })
})
</script>

<template>
  <div class="message" :class="{ user: isUser, assistant: !isUser }">
    <div class="avatar">
      <span v-if="isUser">你</span>
      <span v-else class="ai-avatar">AI</span>
    </div>
    <div class="bubble">
      <div
        v-if="!isUser"
        class="content markdown-body"
        v-html="renderedContent"
      />
      <div v-else class="content">{{ message.content }}</div>
    </div>
  </div>
</template>

<style scoped>
.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
  margin-left: auto;
}

.message.assistant {
  margin-right: auto;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.message.user .avatar {
  background: var(--primary);
  color: white;
}

.message.assistant .avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.ai-avatar {
  font-size: 12px;
  letter-spacing: 0.5px;
}

.bubble {
  padding: 14px 18px;
  border-radius: 16px;
  line-height: 1.7;
  font-size: 14px;
}

.message.user .bubble {
  background: var(--primary);
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant .bubble {
  background: var(--bg-white);
  color: var(--text);
  border: 1px solid var(--border);
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

/* Markdown 样式 */
.markdown-body :deep(h2) {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 10px;
  color: var(--text);
}

.markdown-body :deep(h3) {
  font-size: 15px;
  font-weight: 600;
  margin: 12px 0 6px;
}

.markdown-body :deep(p) {
  margin: 6px 0;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  padding-left: 20px;
  margin: 6px 0;
}

.markdown-body :deep(li) {
  margin: 4px 0;
}

.markdown-body :deep(blockquote) {
  border-left: 3px solid var(--primary);
  padding: 8px 14px;
  margin: 10px 0;
  background: var(--primary-bg);
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
  color: var(--text-secondary);
  font-size: 13px;
}

.markdown-body :deep(code) {
  background: #f0f2f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
  color: #e74c3c;
}

.markdown-body :deep(pre) {
  background: #2d2d2d;
  color: #f8f8f2;
  padding: 14px;
  border-radius: var(--radius-sm);
  overflow-x: auto;
  margin: 10px 0;
}

.markdown-body :deep(pre code) {
  background: none;
  color: inherit;
  padding: 0;
}

.markdown-body :deep(strong) {
  font-weight: 600;
  color: var(--text);
}
</style>
