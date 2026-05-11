const providerColors = {
  AWS: { bg: 'rgba(255,153,0,0.12)', border: 'rgba(255,153,0,0.3)', text: '#FF9900', icon: 'fab fa-aws' },
  GCP: { bg: 'rgba(66,133,244,0.12)', border: 'rgba(66,133,244,0.3)', text: '#4285F4', icon: 'fab fa-google' },
  Azure: { bg: 'rgba(0,120,212,0.12)', border: 'rgba(0,120,212,0.3)', text: '#0078D4', icon: 'fab fa-microsoft' },
  OCI: { bg: 'rgba(248,0,0,0.12)', border: 'rgba(248,0,0,0.3)', text: '#F80000', icon: 'fas fa-cloud' },
  Alibaba: { bg: 'rgba(255,106,0,0.12)', border: 'rgba(255,106,0,0.3)', text: '#FF6A00', icon: 'fas fa-server' }
}

export default function ProviderCard({ service, rank }) {
  const provider = service.provider || 'AWS'
  const colors = providerColors[provider] || providerColors.AWS
  const score = service.final_score || service.performance_score || 0
  const cost = service.estimated_monthly_cost || (service.price_per_hour * 730) || 0

  return (
    <div
      className="glass-card p-5 animate-fade-in-up"
      style={{ animationDelay: `${rank * 100}ms`, borderColor: colors.border }}
    >
      {/* Rank Badge */}
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center gap-3">
          <div
            className="w-10 h-10 rounded-xl flex items-center justify-center text-lg"
            style={{ background: colors.bg, color: colors.text }}
          >
            <i className={colors.icon} />
          </div>
          <div>
            <h3 className="font-bold text-text-primary text-sm">{service.service_name}</h3>
            <span className="text-xs font-medium" style={{ color: colors.text }}>{provider}</span>
          </div>
        </div>
        <div className="flex items-center gap-1 px-2.5 py-1 rounded-full bg-gold-primary/15 text-gold-accent text-xs font-bold">
          #{rank}
        </div>
      </div>

      {/* Scores */}
      <div className="grid grid-cols-2 gap-3 mb-4">
        <div className="p-2.5 rounded-lg bg-space-bg/50 text-center">
          <div className="text-xs text-text-muted mb-1">Performance</div>
          <div className="text-lg font-bold text-accent-green">{Number(score).toFixed(1)}</div>
        </div>
        <div className="p-2.5 rounded-lg bg-space-bg/50 text-center">
          <div className="text-xs text-text-muted mb-1">Monthly Cost</div>
          <div className="text-lg font-bold text-gold-accent">${Number(cost).toFixed(2)}</div>
        </div>
      </div>

      {/* Specs */}
      <div className="flex items-center gap-2 flex-wrap">
        {service.cpu && (
          <span className="text-xs px-2 py-1 rounded-md bg-space-bg/50 text-text-secondary">
            <i className="fas fa-microchip mr-1" />{service.cpu} vCPU
          </span>
        )}
        {service.ram && (
          <span className="text-xs px-2 py-1 rounded-md bg-space-bg/50 text-text-secondary">
            <i className="fas fa-memory mr-1" />{service.ram} GB
          </span>
        )}
        {service.region && (
          <span className="text-xs px-2 py-1 rounded-md bg-space-bg/50 text-text-secondary">
            <i className="fas fa-globe mr-1" />{service.region}
          </span>
        )}
      </div>

      {/* Description */}
      {service.description && (
        <p className="mt-3 text-xs text-text-secondary leading-relaxed line-clamp-2">
          {service.description}
        </p>
      )}
    </div>
  )
}
