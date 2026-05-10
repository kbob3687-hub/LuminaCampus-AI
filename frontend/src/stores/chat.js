import { defineStore } from 'pinia'
import { ref } from 'vue'
import { sendMessage } from '../api/chat'

export const useChatStore = defineStore('chat', () => {
  const messages = ref([])
  const loading = ref(false)
  const currentSubject = ref('')

  async function send(question) {
    if (!question.trim() || loading.value) return

    messages.value.push({ role: 'user', content: question })
    loading.value = true

    try {
      const res = await sendMessage(question, currentSubject.value, '')
      const answer = res.data
      messages.value.push({ role: 'assistant', content: answer })
    } catch (e) {
      messages.value.push({
        role: 'assistant',
        content: '抱歉，回答出了点问题，请稍后重试。',
      })
    } finally {
      loading.value = false
    }
  }

  function clearMessages() {
    messages.value = []
  }

  function setSubject(subject) {
    currentSubject.value = subject
    clearMessages()
  }

  return { messages, loading, currentSubject, send, clearMessages, setSubject }
})
