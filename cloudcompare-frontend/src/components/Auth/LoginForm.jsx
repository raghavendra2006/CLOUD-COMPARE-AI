import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authApi } from '../../api/client'
import { useAuth } from '../../hooks/useAuth'

export default function LoginForm() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const res = await authApi.login(email, password)
      if (res.data?.data?.token) {
        login(res.data.data.token)
        navigate('/dashboard')
      } else {
        setError('Invalid response from server')
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Please check your credentials.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="relative z-10 min-h-screen flex items-center justify-center px-4">
      <div className="glass-card p-8 w-full max-w-md animate-fade-in-up">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="w-16 h-16 mx-auto mb-4 rounded-2xl bg-gradient-to-br from-gold-primary to-gold-accent flex items-center justify-center">
            <i className="fas fa-cloud text-black text-2xl" />
          </div>
          <h2 className="text-2xl font-bold text-text-primary">Welcome Back</h2>
          <p className="text-sm text-text-secondary mt-1">Sign in to CloudCompare AI</p>
        </div>

        {/* Error */}
        {error && (
          <div className="mb-4 p-3 rounded-lg bg-accent-red/15 border border-accent-red/30 text-red-300 text-sm">
            <i className="fas fa-exclamation-circle mr-2" />{error}
          </div>
        )}

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-text-secondary mb-2">
              <i className="fas fa-envelope mr-2 text-gold-primary" />Email
            </label>
            <input
              id="login-email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="you@example.com"
              className="w-full px-4 py-3 rounded-xl bg-space-bg/50 border border-space-border text-text-primary
                         placeholder-text-muted focus:border-gold-primary focus:outline-none focus:ring-2 focus:ring-gold-primary/20 transition-all"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-text-secondary mb-2">
              <i className="fas fa-lock mr-2 text-gold-primary" />Password
            </label>
            <input
              id="login-password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="••••••••"
              className="w-full px-4 py-3 rounded-xl bg-space-bg/50 border border-space-border text-text-primary
                         placeholder-text-muted focus:border-gold-primary focus:outline-none focus:ring-2 focus:ring-gold-primary/20 transition-all"
            />
          </div>

          <button
            id="login-submit"
            type="submit"
            disabled={loading}
            className="btn-gold w-full flex items-center justify-center gap-2"
          >
            {loading ? (
              <div className="pulse-loader"><span /><span /><span /></div>
            ) : (
              <><i className="fas fa-sign-in-alt" /> Sign In</>
            )}
          </button>
        </form>

        <p className="text-center text-sm text-text-secondary mt-6">
          Don't have an account?{' '}
          <Link to="/signup" className="text-gold-primary hover:text-gold-accent transition-colors">Sign up</Link>
        </p>
      </div>
    </div>
  )
}
