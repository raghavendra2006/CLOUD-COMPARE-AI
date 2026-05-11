const categories = [
  { key: 'compute', icon: 'fa-microchip', label: 'Compute' },
  { key: 'storage', icon: 'fa-database', label: 'Storage' },
  { key: 'database', icon: 'fa-server', label: 'Database' },
  { key: 'ai', icon: 'fa-brain', label: 'AI Services' }
]

export default function CategorySelector({ selected, onChange }) {
  return (
    <div className="mb-6">
      <label className="block text-sm font-medium text-text-secondary mb-3">
        <i className="fas fa-tags mr-2 text-gold-primary" />Service Category
      </label>
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        {categories.map((cat) => (
          <button
            key={cat.key}
            onClick={() => onChange(cat.key)}
            className={`flex flex-col items-center gap-2 px-4 py-3 rounded-xl border cursor-pointer transition-all duration-300
              ${selected === cat.key
                ? 'bg-gold-primary/20 border-gold-primary text-gold-accent shadow-lg shadow-gold-primary/10'
                : 'bg-space-bg/50 border-space-border text-text-secondary hover:border-gold-primary/40 hover:text-text-primary'
              }`}
          >
            <i className={`fas ${cat.icon} text-lg`} />
            <span className="text-xs font-medium">{cat.label}</span>
          </button>
        ))}
      </div>
    </div>
  )
}
