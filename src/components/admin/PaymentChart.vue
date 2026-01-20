<template>
  <div class="bg-white p-4 sm:p-6 rounded-lg shadow">
    <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4 mb-4">
      <h3 class="text-base sm:text-lg font-medium text-gray-900">{{ $t('admin.revenueOverview') }}</h3>
      <div class="flex space-x-2">
        <button 
          @click="timeRange = 'current'"
          :class="[
            'px-3 py-1.5 text-sm rounded-md',
            timeRange === 'current' 
              ? 'bg-indigo-600 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
        >
          {{ $t('admin.currentYear') }}
        </button>
        <button 
          @click="timeRange = 'last'"
          :class="[
            'px-3 py-1.5 text-sm rounded-md',
            timeRange === 'last' 
              ? 'bg-indigo-600 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
        >
          {{ $t('admin.previousYear') }}
        </button>
      </div>
    </div>
    
    <div class="h-64 sm:h-80">
      <Line
        v-if="chartData"
        :data="chartData"
        :options="chartOptions"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Line } from 'vue-chartjs';
import { useCompanyCurrency } from '@/composables/useCompanyCurrency';

const { t } = useI18n();
const { currency, currencyLocale, formatCurrency } = useCompanyCurrency();
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

const props = defineProps({
  payments: {
    type: Array,
    required: true
  }
});

const timeRange = ref('current');

const monthNames = computed(() => [
  t('common.months.january'),
  t('common.months.february'),
  t('common.months.march'),
  t('common.months.april'),
  t('common.months.may'),
  t('common.months.june'),
  t('common.months.july'),
  t('common.months.august'),
  t('common.months.september'),
  t('common.months.october'),
  t('common.months.november'),
  t('common.months.december')
]);

const monthlyData = computed(() => {
  const months = Array.from({ length: 12 }, (_, i) => ({
    month: i + 1,
    currentYear: 0,
    lastYear: 0
  }));

  const currentYear = new Date().getFullYear();
  const lastYear = currentYear - 1;

  if (!props.payments || !Array.isArray(props.payments)) {
    return months;
  }

  props.payments.forEach(payment => {
    if (!payment || !payment.paymentDate) return;
    
    const paymentDate = new Date(payment.paymentDate);
    const month = paymentDate.getMonth() + 1;
    const year = paymentDate.getFullYear();
    const amount = payment.finalAmount || payment.amount || 0;

    if (year === currentYear) {
      const monthData = months.find(m => m.month === month);
      if (monthData) {
        monthData.currentYear += amount;
      }
    } else if (year === lastYear) {
      const monthData = months.find(m => m.month === month);
      if (monthData) {
        monthData.lastYear += amount;
      }
    }
  });

  return months;
});

const chartData = computed(() => {
  if (!monthlyData.value) return null;

  const currentYear = new Date().getFullYear();
  const lastYear = currentYear - 1;
  
  return {
    labels: monthlyData.value.map(data => {
      if (!data || !data.month) return '';
      return monthNames.value[data.month - 1];
    }).filter(Boolean),
    datasets: [
      {
        label: t('admin.currentYearLabel', { year: currentYear }),
        data: monthlyData.value.map(data => data?.currentYear || 0),
        borderColor: '#4F46E5',
        backgroundColor: 'rgba(79, 70, 229, 0.1)',
        tension: 0.4,
        fill: true
      },
      {
        label: t('admin.previousYearLabel', { year: lastYear }),
        data: monthlyData.value.map(data => data?.lastYear || 0),
        borderColor: '#6B7280',
        backgroundColor: 'rgba(107, 114, 128, 0.1)',
        tension: 0.4,
        fill: true
      }
    ]
  };
});

const chartOptions = computed(() => {
  return {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value) => `${currency.value} ${formatCurrency(value)}`
        }
      }
    },
    plugins: {
      legend: {
        display: true,
        position: 'top'
      },
      tooltip: {
        callbacks: {
          label: (context) => `${context.dataset.label}: ${currency.value} ${formatCurrency(context.parsed.y)}`
        }
      }
    }
  };
});

// Watch for changes in payments prop to update chart
watch(() => props.payments, () => {
  // The chart will automatically update when payments change
  // because chartData is a computed property
}, { deep: true });

// Watch for changes in timeRange to update chart
watch(timeRange, () => {
  // The chart will automatically update when timeRange changes
  // because chartData is a computed property
});
</script> 