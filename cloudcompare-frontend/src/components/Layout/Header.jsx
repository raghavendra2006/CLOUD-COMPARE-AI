import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'

export default function Header() {
  const { isAuthenticated, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <header className="relative z-10 flex items-center justify-between px-6 py-4 border-b border-space-border bg-space-surface/80 backdrop-blur-xl">
      <Link to={isAuthenticated ? '/dashboard' : '/'} className="flex items-center gap-3 no-underline">
        <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-gold-primary to-gold-accent flex items-center justify-center">
          <i className="fas fa-cloud text-black text-lg" />
        </div>
        <div>
          <h1 className="text-lg font-bold text-text-primary leading-tight">CloudCompare AI</h1>
          <span className="text-xs text-gold-dim">Multi-Cloud Service Optimization</span>
        </div>
      </Link>

      <div className="flex items-center gap-3">
        {isAuthenticated ? (
          <button
            onClick={handleLogout}
            className="flex items-center gap-2 px-4 py-2 rounded-lg border cursor-pointer transition-all duration-300
                       bg-accent-red/15 text-red-300 border-accent-red/20 hover:bg-accent-red/25"
          >
            <i className="fas fa-sign-out-alt" />
            <span className="text-sm">Logout</span>
          </button>
        ) : (
          <>
            <Link
              to="/login"
              className="flex items-center gap-2 px-4 py-2 rounded-lg border no-underline transition-all duration-300
                         bg-accent-green/15 text-accent-green border-accent-green/20 hover:bg-accent-green/25"
            >
              <i className="fas fa-sign-in-alt" />
              <span className="text-sm">Login</span>
            </Link>
            <Link
              to="/signup"
              className="flex items-center gap-2 px-4 py-2 rounded-lg border no-underline transition-all duration-300
                         bg-gold-primary/15 text-gold-primary border-gold-primary/20 hover:bg-gold-primary/25"
            >
              <i className="fas fa-user-plus" />
              <span className="text-sm">Sign Up</span>
            </Link>
          </>
        )}
      </div>
    </header>
  )
}
