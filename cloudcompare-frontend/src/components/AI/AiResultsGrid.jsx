import AiToolCard from './AiToolCard.jsx'

export default function AiResultsGrid({ results, query }) {
  if (!results || !results.tools || results.tools.length === 0) return null

  return (
    <div className="animate-fade-in-up">
      {/* Summary Header */}
      <div className="glass-card p-5 mb-6">
        <div className="flex items-center gap-3 mb-2">
          <i className="fas fa-robot text-gold-primary text-lg" />
          <h3 className="font-bold text-text-primary">AI Analysis Results</h3>
        </div>
        <p className="text-sm text-text-secondary">
          Found <span className="text-gold-accent font-bold">{results.totalResults || results.tools.length}</span> tools
          for: <span className="text-accent-green italic">"{results.purpose || results.originalQuery || query}"</span>
        </p>
        {results.classifiedIntent && (
          <p className="text-xs text-text-muted mt-1">
            <i className="fas fa-tag mr-1" />Classified as: <span className="text-gold-dim">{results.classifiedIntent}</span>
          </p>
        )}
      </div>

      {/* Results Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {results.tools.map((tool, i) => (
          <AiToolCard key={tool.tool_name || tool.toolName || i} tool={tool} index={i} />
        ))}
      </div>
    </div>
  )
}
