import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as apiLogin, register as apiRegister } from '../api/user'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  const username = ref(localStorage.getItem('username') || '')
  const token = ref(localStorage.getItem('token') || '')

  async function login(form) {
    const res = await apiLogin(form.username, form.password)
    const loginVO = res.data
    token.value = loginVO.token
    username.value = loginVO.username
    localStorage.setItem('token', loginVO.token)
    localStorage.setItem('username', loginVO.username)
    router.push('/')
  }

  async function register(form) {
    await apiRegister(form.username, form.password)
  }

  function logout() {
    token.value = ''
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    router.push('/login')
  }

  return { username, token, login, register, logout }
})
