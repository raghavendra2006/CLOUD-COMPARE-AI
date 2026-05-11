import Header from '../components/Layout/Header.jsx'
import LoginForm from '../components/Auth/LoginForm.jsx'

export default function LoginPage() {
  return (
    <div className="relative z-10 min-h-screen">
      <Header />
      <LoginForm />
    </div>
  )
}
