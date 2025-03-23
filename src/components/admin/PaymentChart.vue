<template>
  <div class="bg-white p-6 rounded-lg shadow">
    <div class="flex justify-between items-center mb-4">
      <h3 class="text-lg font-medium text-gray-900">Visão Geral de Receitas</h3>
      <div class="flex space-x-2">
        <button 
          @click="timeRange = 'current'"
          :class="[
            'px-3 py-1 text-sm rounded-md',
            timeRange === 'current' 
              ? 'bg-indigo-600 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
        >
          Ano Atual
        </button>
        <button 
          @click="timeRange = 'last'"
          :class="[
            'px-3 py-1 text-sm rounded-md',
            timeRange === 'last' 
              ? 'bg-indigo-600 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
        >
          Ano Anterior
        </button>
      </div>
    </div>
    
    <div class="h-80">
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
import { Line } from 'vue-chartjs';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const props = defineProps({
  payments: {
    type: Array,
    required: true
  }
});

const timeRange = ref('current');

const monthNames = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

const chartData = computed(() => {
  const currentYear = new Date().getFullYear();
  const targetYear = timeRange.value === 'current' ? currentYear : currentYear - 1;
  
  // Initialize monthly totals
  const monthlyTotals = new Array(12).fill(0);
  const monthlyProfessorPayments = new Array(12).fill(0);
  const monthlyNetProfit = new Array(12).fill(0);
  
  // Calculate totals for each month
  props.payments.forEach(payment => {
    const paymentDate = new Date(payment.paymentDate);
    if (paymentDate.getFullYear() === targetYear) {
      const month = paymentDate.getMonth();
      const amount = payment.finalAmount || payment.amount || 0;
      const professorCommission = payment.commissionAmount || 0;
      
      monthlyTotals[month] += amount;
      monthlyProfessorPayments[month] += professorCommission;
      monthlyNetProfit[month] += (amount - professorCommission);
    }
  });
  
  return {
    labels: monthNames,
    datasets: [
      {
        label: 'Receita Total',
        data: monthlyTotals,
        borderColor: 'rgb(79, 70, 229)',
        backgroundColor: 'rgba(79, 70, 229, 0.5)',
        tension: 0.3
      },
      {
        label: 'Pagamento Professores',
        data: monthlyProfessorPayments,
        borderColor: 'rgb(220, 38, 38)',
        backgroundColor: 'rgba(220, 38, 38, 0.5)',
        tension: 0.3
      },
      {
        label: 'Lucro Líquido',
        data: monthlyNetProfit,
        borderColor: 'rgb(16, 185, 129)',
        backgroundColor: 'rgba(16, 185, 129, 0.5)',
        tension: 0.3
      }
    ]
  };
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: (value) => `R$ ${value.toLocaleString('pt-BR')}`
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
        label: (context) => `${context.dataset.label}: R$ ${context.parsed.y.toLocaleString('pt-BR')}`
      }
    }
  }
};

// Watch for changes in payments prop to update chart
watch(() => props.payments, () => {
  chartData.value = timeRange.value === 'current' ? getCurrentYearData() : getLastYearData();
}, { deep: true });

// Watch for changes in timeRange to update chart
watch(timeRange, () => {
  chartData.value = timeRange.value === 'current' ? getCurrentYearData() : getLastYearData();
});
</script> 