import { useState, useEffect, useRef } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authApi } from '../../api/client'

/* ─── Exact replica of signup.html ─── */

export default function SignupForm() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const cardRef = useRef(null)
  const lightRef = useRef(null)

  // Particle system
  useEffect(() => {
    const bg = document.createElement('div')
    bg.className = 'particle-bg'
    document.body.appendChild(bg)
    for (let i = 0; i < 20; i++) {
      const p = document.createElement('div')
      p.className = 'particle'
      const size = Math.random() * 4 + 2
      p.style.width = `${size}px`
      p.style.height = `${size}px`
      p.style.left = `${Math.random() * 100}%`
      p.style.animationDuration = `${Math.random() * 10 + 10}s`
      p.style.animationDelay = `${Math.random() * 10}s`
      p.style.opacity = String(Math.random() * 0.5)
      bg.appendChild(p)
    }
    return () => bg.remove()
  }, [])

  // Light follow + card tilt
  useEffect(() => {
    const handleMove = (e) => {
      if (lightRef.current) {
        lightRef.current.style.left = `${e.clientX}px`
        lightRef.current.style.top = `${e.clientY}px`
      }
      if (cardRef.current) {
        const xAxis = (window.innerWidth / 2 - e.pageX) / 25
        const yAxis = (window.innerHeight / 2 - e.pageY) / 25
        cardRef.current.style.transform = `perspective(1200px) rotateY(${xAxis}deg) rotateX(${yAxis}deg)`
      }
    }
    document.addEventListener('mousemove', handleMove)
    return () => document.removeEventListener('mousemove', handleMove)
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (password !== confirmPassword) { setError('Passwords do not match'); return }
    if (password.length < 6) { setError('Password must be at least 6 characters'); return }

    setLoading(true)
    try {
      await authApi.signup(username, email, password)
      navigate('/login')
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.error || 'Signup failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <div className="stars" />
      <div className="light-follow" ref={lightRef} />

      <nav style={{
        display: 'flex', justifyContent: 'space-between', alignItems: 'center',
        padding: '1.25rem 2.5rem', position: 'relative', zIndex: 20
      }}>
        <Link to="/" style={{
          display: 'flex', alignItems: 'center', gap: 10,
          fontWeight: 700, fontSize: '1.2rem', color: '#fff', textDecoration: 'none'
        }}>
          <i className="fas fa-cloud" style={{
            fontSize: '1.3rem',
            background: 'linear-gradient(135deg, #00d4aa, #7c3aed)',
            WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent'
          }} />
          CloudCompare AI
        </Link>
      </nav>

      <div style={{
        minHeight: 'calc(100vh - 70px)', display: 'flex',
        alignItems: 'center', justifyContent: 'center',
        padding: '2rem', position: 'relative', zIndex: 10, perspective: 1500
      }}>
        <div className="auth-card" ref={cardRef}>
          <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
            <div style={{
              fontSize: '3rem', marginBottom: '1rem', display: 'inline-block',
              background: 'linear-gradient(135deg, #00d4aa, #7c3aed)',
              WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
              filter: 'drop-shadow(0 0 15px rgba(0,212,170,0.4))',
              animation: 'float 4s ease-in-out infinite'
            }}>
              <i className="fas fa-user-astronaut" />
            </div>
            <h2 style={{ fontSize: '2rem', fontWeight: 800, letterSpacing: '-0.5px', marginBottom: '0.5rem' }}>Create Account</h2>
            <p style={{ color: '#94a3b8', fontSize: '1rem' }}>Join the CloudCompare AI platform</p>
          </div>

          {error && (
            <div style={{
              padding: '1rem', borderRadius: 12, marginBottom: '1.5rem', fontSize: '0.95rem',
              background: 'rgba(239,68,68,0.15)', border: '1px solid rgba(239,68,68,0.3)', color: '#fca5a5'
            }}>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {[
              { label: 'Username', type: 'text', value: username, set: setUsername, placeholder: 'your-username' },
              { label: 'Email Address', type: 'email', value: email, set: setEmail, placeholder: 'you@example.com' }
            ].map((f) => (
              <div key={f.label} style={{ marginBottom: '1.2rem' }}>
                <label style={{
                  display: 'block', marginBottom: '0.6rem', color: '#94a3b8',
                  fontSize: '0.85rem', fontWeight: 600, textTransform: 'uppercase', letterSpacing: 1
                }}>{f.label}</label>
                <input
                  type={f.type} className="form-control" value={f.value}
                  onChange={(e) => f.set(e.target.value)} placeholder={f.placeholder} required
                />
              </div>
            ))}

            <div style={{ marginBottom: '1.2rem' }}>
              <label style={{
                display: 'block', marginBottom: '0.6rem', color: '#94a3b8',
                fontSize: '0.85rem', fontWeight: 600, textTransform: 'uppercase', letterSpacing: 1
              }}>Password</label>
              <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
                <input type={showPassword ? 'text' : 'password'} className="form-control" style={{ paddingRight: '3.2rem' }}
                  value={password} onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" required />
                <i className={`fas ${showPassword ? 'fa-eye-slash' : 'fa-eye'}`} onClick={() => setShowPassword(!showPassword)}
                  style={{ position: 'absolute', right: '1rem', top: '50%', transform: 'translateY(-50%)', cursor: 'pointer', color: '#64748b', fontSize: '1.1rem', zIndex: 2 }} />
              </div>
            </div>

            <div style={{ marginBottom: '1.5rem' }}>
              <label style={{
                display: 'block', marginBottom: '0.6rem', color: '#94a3b8',
                fontSize: '0.85rem', fontWeight: 600, textTransform: 'uppercase', letterSpacing: 1
              }}>Confirm Password</label>
              <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
                <input type={showConfirm ? 'text' : 'password'} className="form-control" style={{ paddingRight: '3.2rem' }}
                  value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} placeholder="••••••••" required />
                <i className={`fas ${showConfirm ? 'fa-eye-slash' : 'fa-eye'}`} onClick={() => setShowConfirm(!showConfirm)}
                  style={{ position: 'absolute', right: '1rem', top: '50%', transform: 'translateY(-50%)', cursor: 'pointer', color: '#64748b', fontSize: '1.1rem', zIndex: 2 }} />
              </div>
            </div>

            <button type="submit" className="btn-auth" disabled={loading}>
              {loading ? <><i className="fas fa-spinner fa-spin" /> Creating...</> : <><i className="fas fa-rocket" /> Create Account</>}
            </button>
          </form>

          <div style={{ marginTop: '2rem', textAlign: 'center', color: '#64748b' }}>
            Already have an account?{' '}
            <Link to="/login" style={{ color: '#00d4aa', textDecoration: 'none', fontWeight: 800 }}>Sign in</Link>
          </div>
        </div>
      </div>
    </>
  )
}
