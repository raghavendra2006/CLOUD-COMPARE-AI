import { Link } from 'react-router-dom'
import Header from '../components/Layout/Header.jsx'

const features = [
  { icon: 'fa-robot', title: 'AI-Powered Analysis', desc: 'Groq LLM compares services across 5 cloud providers with intelligent scoring.' },
  { icon: 'fa-bolt', title: 'Real-Time Pricing', desc: 'Live cost estimates for compute, storage, database, and AI services.' },
  { icon: 'fa-shield-alt', title: 'Enterprise Grade', desc: 'Circuit breakers, caching, and virtual threads for production reliability.' },
  { icon: 'fa-chart-bar', title: 'Visual Analytics', desc: 'Interactive charts for cost, performance, and popularity comparison.' },
  { icon: 'fa-brain', title: 'NLP Queries', desc: 'Ask questions in plain English — no dropdowns needed.' },
  { icon: 'fa-cloud', title: '5 Cloud Providers', desc: 'AWS, GCP, Azure, OCI, and Alibaba Cloud side-by-side.' }
]

const providers = [
  { name: 'AWS', color: '#FF9900', icon: 'fab fa-aws' },
  { name: 'Google Cloud', color: '#4285F4', icon: 'fab fa-google' },
  { name: 'Azure', color: '#0078D4', icon: 'fab fa-microsoft' },
  { name: 'Oracle Cloud', color: '#F80000', icon: 'fas fa-database' },
  { name: 'Alibaba Cloud', color: '#FF6A00', icon: 'fas fa-cloud' }
]

export default function LandingPage() {
  return (
    <div className="relative z-10 min-h-screen">
      <Header />

      {/* Hero Section */}
      <section className="flex flex-col items-center justify-center text-center px-6 pt-20 pb-16">
        <div className="animate-fade-in-up">
          <div className="inline-flex items-center gap-2 px-4 py-1.5 mb-6 rounded-full bg-gold-primary/10 border border-gold-primary/20">
            <i className="fas fa-sparkles text-gold-accent text-xs" />
            <span className="text-xs font-medium text-gold-accent">Powered by Java 21 + Groq AI</span>
          </div>

          <h1 className="text-5xl md:text-6xl font-extrabold mb-4 leading-tight">
            <span className="bg-gradient-to-r from-gold-primary via-gold-accent to-gold-primary bg-clip-text text-transparent">
              CloudCompare AI
            </span>
          </h1>
          <p className="text-lg text-text-secondary max-w-2xl mx-auto mb-8 leading-relaxed">
            The intelligent multi-cloud service recommendation engine. Compare AWS, GCP, Azure, OCI, and Alibaba Cloud with AI-powered analysis in seconds.
          </p>

          <div className="flex flex-col sm:flex-row items-center gap-4 justify-center">
            <Link to="/dashboard" className="btn-gold flex items-center gap-2 no-underline text-sm">
              <i className="fas fa-rocket" /> Launch Dashboard
            </Link>
            <Link
              to="/signup"
              className="flex items-center gap-2 px-6 py-3 rounded-xl border border-space-border text-text-secondary
                         hover:border-gold-primary/40 hover:text-text-primary transition-all no-underline text-sm"
            >
              <i className="fas fa-user-plus" /> Create Account
            </Link>
          </div>
        </div>
      </section>

      {/* Provider Logos */}
      <section className="flex items-center justify-center gap-8 flex-wrap px-6 py-8 mb-12">
        {providers.map((p) => (
          <div key={p.name} className="flex items-center gap-2 opacity-60 hover:opacity-100 transition-opacity">
            <i className={`${p.icon} text-xl`} style={{ color: p.color }} />
            <span className="text-sm font-medium text-text-secondary">{p.name}</span>
          </div>
        ))}
      </section>

      {/* Features Grid */}
      <section className="max-w-6xl mx-auto px-6 pb-20">
        <h2 className="text-2xl font-bold text-center text-text-primary mb-10">
          <i className="fas fa-gem mr-2 text-gold-primary" />Platform Capabilities
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          {features.map((f, i) => (
            <div
              key={f.title}
              className="glass-card p-6 animate-fade-in-up"
              style={{ animationDelay: `${i * 100}ms` }}
            >
              <div className="w-12 h-12 rounded-xl bg-gold-primary/15 flex items-center justify-center mb-4">
                <i className={`fas ${f.icon} text-gold-accent text-lg`} />
              </div>
              <h3 className="font-bold text-text-primary mb-2">{f.title}</h3>
              <p className="text-sm text-text-secondary leading-relaxed">{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Footer */}
      <footer className="text-center py-8 border-t border-space-border">
        <p className="text-xs text-text-muted">
          © 2024 CloudCompare AI — Built with Spring Boot 3, Java 21, React & Tailwind CSS
        </p>
      </footer>
    </div>
  )
}
