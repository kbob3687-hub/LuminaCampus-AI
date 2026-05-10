import request from './request'

export function getConversations() {
  return request.get('/conversation/list')
}

export function sendMessage(question, subject, docId) {
  const params = new URLSearchParams()
  params.append('question', question)
  if (subject) params.append('subject', subject)
  if (docId) params.append('docId', docId)
  return request.post('/chat', params)
}
