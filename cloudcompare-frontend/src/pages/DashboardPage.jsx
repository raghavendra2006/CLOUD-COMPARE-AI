import { useState } from 'react'
import Header from '../components/Layout/Header.jsx'
import CategorySelector from '../components/Cloud/CategorySelector.jsx'
import ResourceInputs from '../components/Cloud/ResourceInputs.jsx'
import ServiceTypeSelect from '../components/Cloud/ServiceTypeSelect.jsx'
import ProviderCard from '../components/Cloud/ProviderCard.jsx'
import ComparisonCharts from '../components/Cloud/ComparisonCharts.jsx'
import NlpQueryInput from '../components/AI/NlpQueryInput.jsx'
import AiResultsGrid from '../components/AI/AiResultsGrid.jsx'
import ChatbotPanel from '../components/Chatbot/ChatbotPanel.jsx'
import { useCompare } from '../hooks/useCompare.js'
import { aiApi } from '../api/client.js'

export default function DashboardPage() {
  // View toggle
  const [activeView, setActiveView] = useState('cloud')

  // Cloud state
  const [category, setCategory] = useState('compute')
  const [serviceType, setServiceType] = useState('all')
  const [priority, setPriority] = useState('balanced')
  const [resources, setResources] = useState({ cpu: 2, ram: 4, storage: 100, hours: 730 })
  const { results: cloudResults, loading: cloudLoading, error: cloudError, compare } = useCompare()

  // AI state
  const [aiResults, setAiResults] = useState(null)
  const [aiLoading, setAiLoading] = useState(false)
  const [aiError, setAiError] = useState(null)
  const [lastQuery, setLastQuery] = useState('')

  // ─── Cloud Compare ─────────────────────
  const handleCompare = () => {
    compare({
      category,
      serviceType,
      priority,
      cpu: resources.cpu,
      ram: resources.ram,
      storage: resources.storage,
      hours: resources.hours,
      region: 'all'
    })
  }

  // ─── AI NLP Query ──────────────────────
  const handleNlpQuery = async (query) => {
    setAiLoading(true)
    setAiError(null)
    setLastQuery(query)
    try {
      // Try NLP endpoint first, fallback to standard ai-compare
      let res
      try {
        res = await aiApi.nlpCompare(query)
      } catch {
        res = await aiApi.compareTools(query)
      }
      setAiResults(res.data?.data || null)
    } catch (err) {
      setAiError(err.response?.data?.message || 'AI analysis failed')
    } finally {
      setAiLoading(false)
    }
  }

  return (
    <div className="relative z-10 min-h-screen">
      <Header />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 py-6">
        {/* View Toggle */}
        <div className="flex justify-center mb-8">
          <div className="inline-flex rounded-xl border border-space-border overflow-hidden">
            <button
              onClick={() => setActiveView('cloud')}
              className={`px-6 py-2.5 text-sm font-medium transition-all cursor-pointer
                ${activeView === 'cloud'
                  ? 'bg-gold-primary/20 text-gold-accent border-r border-gold-primary/30'
                  : 'bg-space-bg/50 text-text-secondary hover:text-text-primary border-r border-space-border'
                }`}
            >
              <i className="fas fa-server mr-2" />Cloud Services
            </button>
            <button
              onClick={() => setActiveView('ai')}
              className={`px-6 py-2.5 text-sm font-medium transition-all cursor-pointer
                ${activeView === 'ai'
                  ? 'bg-gold-primary/20 text-gold-accent'
                  : 'bg-space-bg/50 text-text-secondary hover:text-text-primary'
                }`}
            >
              <i className="fas fa-robot mr-2" />AI Tools
            </button>
          </div>
        </div>

        <ChatbotPanel
          activeView={activeView}
          cloudContext={{
            category,
            serviceType,
            priority,
            resources,
            services: cloudResults?.services || cloudResults || []
          }}
          aiToolsContext={{
            query: lastQuery,
            tools: aiResults?.tools || []
          }}
        />

        {/* ═══════════ CLOUD VIEW ═══════════ */}
        {activeView === 'cloud' && (
          <div>
            <div className="glass-card p-6 mb-6">
              <div className="mb-4">
                <h2 className="text-lg font-bold text-text-primary">
                  <i className="fas fa-sliders-h mr-2 text-gold-primary" />Configure Your Requirements
                </h2>
                <p className="text-sm text-text-secondary mt-1">Select your cloud service needs and we'll find the best option</p>
              </div>

              <CategorySelector selected={category} onChange={(c) => { setCategory(c); setServiceType('all') }} />
              <ResourceInputs values={resources} onChange={setResources} />

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mt-4">
                <ServiceTypeSelect category={category} value={serviceType} onChange={setServiceType} />

                <div>
                  <label className="block text-xs font-medium text-text-secondary mb-2">
                    <i className="fas fa-sort-amount-up mr-1 text-gold-primary" />Priority
                  </label>
                  <select
                    id="priority-select"
                    value={priority}
                    onChange={(e) => setPriority(e.target.value)}
                    className="w-full px-3 py-2.5 rounded-xl bg-space-bg/50 border border-space-border text-text-primary text-sm
                               appearance-none cursor-pointer focus:border-gold-primary focus:outline-none focus:ring-2 focus:ring-gold-primary/20"
                  >
                    <option value="balanced">Balanced (Cost + Performance)</option>
                    <option value="cost">Cost Optimization</option>
                    <option value="performance">Maximum Performance</option>
                  </select>
                </div>

                <div className="flex items-end">
                  <button
                    id="compare-btn"
                    onClick={handleCompare}
                    disabled={cloudLoading}
                    className="btn-gold w-full flex items-center justify-center gap-2 !py-2.5"
                  >
                    {cloudLoading ? (
                      <div className="pulse-loader"><span /><span /><span /></div>
                    ) : (
                      <><i className="fas fa-rocket" /> Compare Services</>
                    )}
                  </button>
                </div>
              </div>
            </div>

            {/* Cloud Error */}
            {cloudError && (
              <div className="mb-6 p-4 rounded-xl bg-accent-red/10 border border-accent-red/30 text-red-300 text-sm">
                <i className="fas fa-exclamation-triangle mr-2" />{cloudError}
              </div>
            )}

            {/* Cloud Results */}
            {cloudResults && (
              <div className="animate-fade-in-up">
                {/* Provider Cards */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-4 mb-6">
                  {(cloudResults.services || cloudResults).map((svc, i) => (
                    <ProviderCard key={svc.provider || i} service={svc} rank={i + 1} />
                  ))}
                </div>

                {/* Charts */}
                <ComparisonCharts services={cloudResults.services || cloudResults} />
              </div>
            )}
          </div>
        )}

        {/* ═══════════ AI VIEW ═══════════ */}
        {activeView === 'ai' && (
          <div>
            <div className="glass-card p-6 mb-6">
              <div className="mb-4">
                <h2 className="text-lg font-bold text-text-primary">
                  <i className="fas fa-brain mr-2 text-gold-primary" />AI Tool Finder
                </h2>
                <p className="text-sm text-text-secondary mt-1">
                  Ask in plain English — our NLP engine will find the best AI tools for your needs
                </p>
              </div>
              <NlpQueryInput onSubmit={handleNlpQuery} loading={aiLoading} />
            </div>

            {/* AI Error */}
            {aiError && (
              <div className="mb-6 p-4 rounded-xl bg-accent-red/10 border border-accent-red/30 text-red-300 text-sm">
                <i className="fas fa-exclamation-triangle mr-2" />{aiError}
              </div>
            )}

            {/* AI Results */}
            {aiResults && <AiResultsGrid results={aiResults} query={lastQuery} />}
          </div>
        )}
      </main>
    </div>
  )
}
