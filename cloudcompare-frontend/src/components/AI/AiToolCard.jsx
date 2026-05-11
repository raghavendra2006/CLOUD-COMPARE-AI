export default function AiToolCard({ tool, index }) {
  const scoreColor = tool.score >= 9 ? 'text-accent-green' : tool.score >= 8 ? 'text-gold-accent' : 'text-accent-blue'

  return (
    <div
      className="glass-card p-5 animate-fade-in-up"
      style={{ animationDelay: `${index * 120}ms` }}
    >
      {/* Header */}
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-gold-primary/20 to-gold-accent/10 flex items-center justify-center">
            <i className="fas fa-robot text-gold-primary" />
          </div>
          <div>
            <h3 className="font-bold text-text-primary text-sm">{tool.tool_name || tool.toolName}</h3>
            <span className="text-xs text-text-muted">{tool.provider}</span>
          </div>
        </div>
        <div className="flex items-center gap-1 px-3 py-1 rounded-full bg-gold-primary/15 text-gold-accent text-xs font-bold">
          #{tool.rank || index + 1}
        </div>
      </div>

      {/* Score + Model */}
      <div className="grid grid-cols-2 gap-3 mb-4">
        <div className="p-2.5 rounded-lg bg-space-bg/50 text-center">
          <div className="text-xs text-text-muted mb-1">AI Score</div>
          <div className={`text-xl font-bold ${scoreColor}`}>{Number(tool.score).toFixed(1)}</div>
        </div>
        <div className="p-2.5 rounded-lg bg-space-bg/50 text-center">
          <div className="text-xs text-text-muted mb-1">Model</div>
          <div className="text-xs font-mono text-text-secondary truncate" title={tool.model_number || tool.modelNumber}>
            {tool.model_number || tool.modelNumber || '—'}
          </div>
        </div>
      </div>

      {/* Pricing */}
      <div className="flex items-center gap-2 mb-3">
        <span className="text-xs px-2.5 py-1 rounded-md bg-accent-green/10 text-accent-green border border-accent-green/20">
          <i className="fas fa-tag mr-1" />{tool.pricing || 'Contact'}
        </span>
      </div>

      {/* Description */}
      {tool.description && (
        <p className="text-xs text-text-secondary leading-relaxed line-clamp-2">
          {tool.description}
        </p>
      )}
    </div>
  )
}
