import { useState } from 'react'

const suggestions = [
  'Best AI for coding and software development',
  'Top tools for content writing and copywriting',
  'AI tools for data analysis and spreadsheets',
  'Image generation and graphic design tools',
  'Video editing and creation AI tools',
  'AI presentation and slide builders',
  'Music and audio generation tools',
  'Research and general-purpose AI assistants'
]

export default function NlpQueryInput({ onSubmit, loading }) {
  const [query, setQuery] = useState('')
  const [showSuggestions, setShowSuggestions] = useState(false)

  const handleSubmit = (e) => {
    e.preventDefault()
    if (query.trim()) {
      onSubmit(query.trim())
    }
  }

  const handleSuggestionClick = (suggestion) => {
    setQuery(suggestion)
    setShowSuggestions(false)
    onSubmit(suggestion)
  }

  return (
    <div className="mb-6">
      <form onSubmit={handleSubmit} className="relative">
        <div className="relative">
          <i className="fas fa-brain absolute left-4 top-1/2 -translate-y-1/2 text-gold-primary text-lg" />
          <input
            id="nlp-query-input"
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onFocus={() => setShowSuggestions(true)}
            onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
            placeholder="Ask anything... e.g., 'What's the best AI for building a chatbot?'"
            className="w-full pl-12 pr-32 py-4 rounded-2xl bg-space-bg/60 border-2 border-space-border text-text-primary text-sm
                       placeholder-text-muted focus:border-gold-primary focus:outline-none focus:ring-2 focus:ring-gold-primary/20 transition-all"
          />
          <button
            type="submit"
            disabled={loading || !query.trim()}
            className="absolute right-2 top-1/2 -translate-y-1/2 btn-gold !py-2 !px-5 !text-sm !rounded-xl flex items-center gap-2"
          >
            {loading ? (
              <div className="pulse-loader"><span /><span /><span /></div>
            ) : (
              <><i className="fas fa-rocket" /> Ask AI</>
            )}
          </button>
        </div>

        {/* Suggestions Dropdown */}
        {showSuggestions && (
          <div className="absolute z-20 w-full mt-2 py-2 rounded-xl bg-space-card border border-space-border shadow-2xl backdrop-blur-xl">
            <p className="px-4 py-1.5 text-xs text-text-muted font-medium uppercase tracking-wider">Suggestions</p>
            {suggestions.map((s, i) => (
              <button
                key={i}
                type="button"
                onMouseDown={() => handleSuggestionClick(s)}
                className="w-full text-left px-4 py-2.5 text-sm text-text-secondary hover:bg-gold-primary/10 hover:text-gold-accent transition-colors cursor-pointer"
              >
                <i className="fas fa-lightbulb mr-2 text-gold-dim" />{s}
              </button>
            ))}
          </div>
        )}
      </form>

      <p className="mt-2 text-xs text-text-muted text-center">
        <i className="fas fa-magic mr-1" />
        Powered by NLP — type any question and our AI will find the best tools for you
      </p>
    </div>
  )
}
