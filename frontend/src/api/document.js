import request from './request'

export function uploadDocument(fileName, subject) {
  const params = new URLSearchParams()
  params.append('fileName', fileName)
  if (subject) params.append('subject', subject)
  return request.post('/document/upload', params)
}

export function getDocuments() {
  return request.get('/document/list')
}
