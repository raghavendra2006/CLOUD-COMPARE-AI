import { useState, useEffect, useCallback } from 'react'

export function useAuth() {
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(localStorage.getItem('token'))

  useEffect(() => {
    if (token) {
      try {
        // Decode JWT payload (base64)
        const payload = JSON.parse(atob(token.split('.')[1]))
        setUser({ email: payload.sub, exp: payload.exp })
      } catch {
        setUser(null)
        setToken(null)
        localStorage.removeItem('token')
      }
    } else {
      setUser(null)
    }
  }, [token])

  const login = useCallback((newToken) => {
    localStorage.setItem('token', newToken)
    setToken(newToken)
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('token')
    setToken(null)
    setUser(null)
  }, [])

  const isAuthenticated = !!token && !!user

  return { user, token, isAuthenticated, login, logout }
}
