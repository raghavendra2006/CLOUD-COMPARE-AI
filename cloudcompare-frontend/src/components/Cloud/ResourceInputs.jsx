export default function ResourceInputs({ values, onChange }) {
  const fields = [
    { id: 'cpu', icon: 'fa-microchip', label: 'vCPU', placeholder: 'e.g., 2', min: 1, max: 128 },
    { id: 'ram', icon: 'fa-memory', label: 'RAM (GB)', placeholder: 'e.g., 4', min: 1, max: 512 },
    { id: 'storage', icon: 'fa-hdd', label: 'Storage (GB)', placeholder: 'e.g., 100', min: 1, max: 10000 },
    { id: 'hours', icon: 'fa-clock', label: 'Hours/Month', placeholder: 'e.g., 730', min: 1, max: 8760 }
  ]

  const handleChange = (id, val) => {
    onChange({ ...values, [id]: parseInt(val) || 0 })
  }

  return (
    <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-4">
      {fields.map((f) => (
        <div key={f.id}>
          <label className="block text-xs font-medium text-text-secondary mb-2">
            <i className={`fas ${f.icon} mr-1 text-gold-primary`} />{f.label}
          </label>
          <input
            id={`input-${f.id}`}
            type="number"
            value={values[f.id] || ''}
            onChange={(e) => handleChange(f.id, e.target.value)}
            placeholder={f.placeholder}
            min={f.min}
            max={f.max}
            className="w-full px-3 py-2.5 rounded-xl bg-space-bg/50 border border-space-border text-text-primary text-sm
                       placeholder-text-muted focus:border-gold-primary focus:outline-none focus:ring-2 focus:ring-gold-primary/20 transition-all"
          />
        </div>
      ))}
    </div>
  )
}
