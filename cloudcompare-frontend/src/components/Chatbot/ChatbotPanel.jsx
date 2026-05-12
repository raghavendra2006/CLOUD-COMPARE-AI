import { useMemo, useState } from 'react'
import { chatApi } from '../../api/client.js'

const starterPrompts = {
  cloud: [
    'Create a beginner deployment plan for this cloud setup',
    'Explain why the top cloud provider is best for my workload',
    'What should I check before deploying this architecture?'
  ],
  ai: [
    'Which AI tool should I choose for this use case?',
    'Explain the trade-offs between the top AI tools',
    'How should a team test these AI tools before adopting one?'
  ]
}

export default function ChatbotPanel({
  activeView,
  cloudContext,
  aiToolsContext
}) {
  const [mode, setMode] = useState(activeView === 'ai' ? 'ai' : 'cloud')
  const [question, setQuestion] = useState('')
  const [messages, setMessages] = useState([
    {
      role: 'assistant',
      text: 'Ask for deployment planning, provider trade-offs, AI tool selection, or next-step guidance based on your current results.'
    }
  ])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const prompts = useMemo(() => starterPrompts[mode], [mode])

  const sendQuestion = async (text = question) => {
    const cleanQuestion = text.trim()
    if (!cleanQuestion || loading) return

    setQuestion('')
    setError(null)
    setLoading(true)
    setMessages((current) => [...current, { role: 'user', text: cleanQuestion }])

    try {
      const res = mode === 'cloud'
        ? await chatApi.cloud(cleanQuestion, cloudContext)
        : await chatApi.aiTools(cleanQuestion, aiToolsContext)

      const reply = res.data?.data?.reply || res.data?.reply || 'I could not generate a response for that question.'
      setMessages((current) => [...current, { role: 'assistant', text: reply }])
    } catch (err) {
      const message = err.response?.status === 403
        ? 'Please log in again to use the chatbot assistant.'
        : err.response?.data?.message || 'Chatbot request failed. Please try again.'
      setError(message)
      setMessages((current) => [...current, { role: 'assistant', text: message }])
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="glass-card p-5 mb-6">
      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4 mb-4">
        <div>
          <h2 className="text-lg font-bold text-text-primary">
            <i className="fas fa-comments mr-2 text-gold-primary" />CloudCompare Assistant
          </h2>
          <p className="text-sm text-text-secondary mt-1">
            Get chatbot guidance using your current cloud or AI comparison context.
          </p>
        </div>

        <div className="inline-flex rounded-xl border border-space-border overflow-hidden self-start lg:self-auto">
          <button
            type="button"
            onClick={() => setMode('cloud')}
            className={`px-4 py-2 text-xs sm:text-sm font-medium transition-all cursor-pointer border-r border-space-border
              ${mode === 'cloud' ? 'bg-gold-primary/20 text-gold-accent' : 'bg-space-bg/50 text-text-secondary hover:text-text-primary'}`}
          >
            <i className="fas fa-cloud mr-2" />Cloud Architect
          </button>
          <button
            type="button"
            onClick={() => setMode('ai')}
            className={`px-4 py-2 text-xs sm:text-sm font-medium transition-all cursor-pointer
              ${mode === 'ai' ? 'bg-gold-primary/20 text-gold-accent' : 'bg-space-bg/50 text-text-secondary hover:text-text-primary'}`}
          >
            <i className="fas fa-robot mr-2" />AI Tools
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-[1fr_280px] gap-4">
        <div className="rounded-xl border border-space-border bg-space-bg/40 overflow-hidden">
          <div className="h-64 overflow-y-auto p-4 space-y-3">
            {messages.map((message, index) => (
              <div
                key={`${message.role}-${index}`}
                className={`flex ${message.role === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                <div
                  className={`max-w-[85%] whitespace-pre-line rounded-xl px-4 py-3 text-sm leading-relaxed border
                    ${message.role === 'user'
                      ? 'bg-gold-primary/20 text-gold-accent border-gold-primary/30'
                      : 'bg-space-bg/70 text-text-secondary border-space-border'}`}
                >
                  {message.text}
                </div>
              </div>
            ))}
            {loading && (
              <div className="flex justify-start">
                <div className="rounded-xl px-4 py-3 bg-space-bg/70 border border-space-border">
                  <div className="pulse-loader"><span /><span /><span /></div>
                </div>
              </div>
            )}
          </div>

          <form
            onSubmit={(event) => {
              event.preventDefault()
              sendQuestion()
            }}
            className="border-t border-space-border p-3 flex flex-col sm:flex-row gap-3"
          >
            <input
              type="text"
              value={question}
              onChange={(event) => setQuestion(event.target.value)}
              placeholder={mode === 'cloud' ? 'Ask for a deployment plan or provider recommendation...' : 'Ask which AI tool fits your workflow...'}
              className="flex-1 px-4 py-3 rounded-xl bg-space-bg/70 border border-space-border text-text-primary text-sm outline-none focus:border-gold-primary focus:ring-2 focus:ring-gold-primary/20"
            />
            <button
              type="submit"
              disabled={loading || !question.trim()}
              className="btn-gold flex items-center justify-center gap-2 !py-3 sm:w-36 disabled:opacity-60 disabled:cursor-not-allowed"
            >
              <i className="fas fa-paper-plane" />Ask
            </button>
          </form>
        </div>

        <div className="space-y-2">
          <p className="text-xs font-semibold uppercase tracking-wider text-text-muted">Suggested questions</p>
          {prompts.map((prompt) => (
            <button
              type="button"
              key={prompt}
              onClick={() => sendQuestion(prompt)}
              disabled={loading}
              className="w-full text-left p-3 rounded-xl border border-space-border bg-space-bg/40 text-sm text-text-secondary hover:text-gold-accent hover:border-gold-primary/40 transition-all disabled:opacity-60"
            >
              <i className="fas fa-lightbulb mr-2 text-gold-primary" />{prompt}
            </button>
          ))}
          {error && (
            <div className="p-3 rounded-xl bg-accent-red/10 border border-accent-red/30 text-red-300 text-sm">
              <i className="fas fa-exclamation-triangle mr-2" />{error}
            </div>
          )}
        </div>
      </div>
    </section>
  )
}
