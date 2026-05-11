import { Bar } from 'react-chartjs-2'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

const providerColors = ['#FF9900', '#4285F4', '#0078D4', '#F80000', '#FF6A00']

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
  },
  scales: {
    y: {
      beginAtZero: true,
      grid: { color: 'rgba(212,160,23,0.08)' },
      ticks: { color: '#a89968', font: { size: 11 } }
    },
    x: {
      grid: { display: false },
      ticks: { color: '#a89968', font: { size: 10 } }
    }
  }
}

function buildChart(services, labelKey, valueKey, label) {
  return {
    labels: services.map((s) => s.provider || s[labelKey] || 'Unknown'),
    datasets: [{
      label,
      data: services.map((s) => Number(s[valueKey]) || 0),
      backgroundColor: services.map((_, i) => providerColors[i % providerColors.length] + '88'),
      borderColor: services.map((_, i) => providerColors[i % providerColors.length]),
      borderWidth: 2,
      borderRadius: 8
    }]
  }
}

export default function ComparisonCharts({ services }) {
  if (!services || services.length === 0) return null

  const costData = buildChart(services, 'provider', 'estimated_monthly_cost', 'Monthly Cost ($)')
  // Fallback: if no estimated_monthly_cost, use price_per_hour * 730
  if (costData.datasets[0].data.every((v) => v === 0)) {
    costData.datasets[0].data = services.map((s) => Number(s.price_per_hour || 0) * 730)
  }

  const perfData = buildChart(services, 'provider', 'performance_score', 'Performance')
  const popData = buildChart(services, 'provider', 'popularity_score', 'Popularity')

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
      <div className="chart-box">
        <h3 className="text-sm font-semibold text-text-secondary mb-3">
          <i className="fas fa-dollar-sign mr-2 text-gold-primary" />Cost Comparison
        </h3>
        <div className="h-52">
          <Bar data={costData} options={chartOptions} />
        </div>
      </div>

      <div className="chart-box">
        <h3 className="text-sm font-semibold text-text-secondary mb-3">
          <i className="fas fa-tachometer-alt mr-2 text-accent-green" />Performance
        </h3>
        <div className="h-52">
          <Bar data={perfData} options={chartOptions} />
        </div>
      </div>

      <div className="chart-box">
        <h3 className="text-sm font-semibold text-text-secondary mb-3">
          <i className="fas fa-fire mr-2 text-accent-blue" />Popularity
        </h3>
        <div className="h-52">
          <Bar data={popData} options={chartOptions} />
        </div>
      </div>
    </div>
  )
}
