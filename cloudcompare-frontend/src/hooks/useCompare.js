import { useState, useCallback } from 'react'
import { cloudApi } from '../api/client'

export function useCompare() {
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const compare = useCallback(async (params) => {
    setLoading(true)
    setError(null)
    try {
      const res = await cloudApi.compare(params)
      setResults(res.data.data)
      return res.data.data
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Comparison failed'
      setError(msg)
      return null
    } finally {
      setLoading(false)
    }
  }, [])

  const clearResults = useCallback(() => {
    setResults(null)
    setError(null)
  }, [])

  return { results, loading, error, compare, clearResults }
}
