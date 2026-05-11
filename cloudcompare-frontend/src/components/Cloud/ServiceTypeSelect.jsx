import { useState, useEffect } from 'react'
import { cloudApi } from '../../api/client'

export default function ServiceTypeSelect({ category, value, onChange }) {
  const [types, setTypes] = useState([])

  useEffect(() => {
    if (!category) return
    cloudApi.getServiceTypes(category)
      .then((res) => setTypes(res.data?.data || []))
      .catch(() => setTypes([]))
  }, [category])

  return (
    <div>
      <label className="block text-xs font-medium text-text-secondary mb-2">
        <i className="fas fa-filter mr-1 text-gold-primary" />Service Type
      </label>
      <div className="relative">
        <select
          id="service-type-select"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="w-full px-3 py-2.5 rounded-xl bg-space-bg/50 border border-space-border text-text-primary text-sm
                     appearance-none cursor-pointer focus:border-gold-primary focus:outline-none focus:ring-2 focus:ring-gold-primary/20 transition-all"
        >
          <option value="all">All Types</option>
          {types.map((t) => (
            <option key={t.value} value={t.value}>{t.label}</option>
          ))}
        </select>
        <i className="fas fa-chevron-down absolute right-3 top-1/2 -translate-y-1/2 text-gold-dim text-xs pointer-events-none" />
      </div>
    </div>
  )
}
