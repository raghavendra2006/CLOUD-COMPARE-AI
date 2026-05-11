import axios from 'axios'

const runtimeConfig = window.__CLOUDCOMPARE_CONFIG__ || {}
const API_BASE = runtimeConfig.API_BASE || import.meta.env.VITE_API_BASE || ''

const api = axios.create({
  baseURL: `${API_BASE}/api`,
  headers: { 'Content-Type': 'application/json' },
  timeout: 30000
})

// Attach JWT token to every request if available
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const authApi = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  signup: (name, email, password) => api.post('/auth/signup', { name, email, password })
}

// ─── Cloud Compare ─────────────────────────────────
export const cloudApi = {
  compare: (params) => api.post('/compare', params),
  getServiceTypes: (category) => api.get(`/service-types/${category}`),
  getRegions: () => api.get('/regions')
}

// ─── AI Tools ──────────────────────────────────────
export const aiApi = {
  compareTools: (purpose) => api.post('/ai-compare', { purpose }),
  nlpCompare: (query) => api.post('/nlp-compare', { query })
}

// ─── Health ────────────────────────────────────────
export const healthApi = {
  check: () => api.get('/test')
}

export default api
