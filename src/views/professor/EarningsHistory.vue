<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">Histórico de Ganhos</h1>
      </div>
    </header>

    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="px-4 py-6 sm:px-0">
        <Breadcrumb :items="breadcrumbItems" />
      </div>

      <!-- Filters Section -->
      <div class="px-4 sm:px-0 mb-8">
        <div class="bg-white shadow overflow-hidden sm:rounded-lg p-6">
          <h2 class="text-lg font-medium text-gray-900 mb-4">Filtros</h2>
          <div class="grid grid-cols-1 gap-6 sm:grid-cols-3">
            <div>
              <label for="year" class="block text-sm font-medium text-gray-700">Ano</label>
              <select id="year" v-model="selectedYear" class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
                <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
              </select>
            </div>
            <div>
              <label for="month" class="block text-sm font-medium text-gray-700">Mês</label>
              <select id="month" v-model="selectedMonth" class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
                <option v-for="(month, index) in months" :key="index" :value="index">{{ month }}</option>
              </select>
            </div>
            <div class="flex items-end">
              <button @click="applyFilters" class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                Aplicar Filtros
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Earnings Summary -->
      <div class="px-4 sm:px-0 mb-8">
        <div class="bg-white shadow overflow-hidden sm:rounded-lg">
          <div class="px-4 py-5 sm:px-6 bg-green-50">
            <h3 class="text-lg leading-6 font-medium text-gray-900">
              Resumo de Ganhos - {{ months[selectedMonth] }} {{ selectedYear }}
            </h3>
            <p class="mt-1 max-w-2xl text-sm text-gray-500">
              Comissão: {{ commission }}%
            </p>
          </div>
          <div class="border-t border-gray-200 px-4 py-5 sm:p-6">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div class="bg-white shadow-sm rounded-lg p-4 border border-gray-200">
                <h4 class="text-sm font-medium text-gray-500">Total de Ganhos</h4>
                <p class="mt-1 text-2xl font-semibold text-gray-900">R$ {{ totalEarnings.toFixed(2) }}</p>
              </div>
              <div class="bg-white shadow-sm rounded-lg p-4 border border-gray-200">
                <h4 class="text-sm font-medium text-gray-500">Média por Aula</h4>
                <p class="mt-1 text-2xl font-semibold text-gray-900">R$ {{ averagePerClass.toFixed(2) }}</p>
              </div>
              <div class="bg-white shadow-sm rounded-lg p-4 border border-gray-200">
                <h4 class="text-sm font-medium text-gray-500">Total de Aulas</h4>
                <p class="mt-1 text-2xl font-semibold text-gray-900">{{ totalClasses }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Earnings by Student -->
      <div class="px-4 sm:px-0 mb-8">
        <div class="bg-white shadow overflow-hidden sm:rounded-lg">
          <div class="px-4 py-5 sm:px-6">
            <h3 class="text-lg leading-6 font-medium text-gray-900">
              Ganhos por Aluno
            </h3>
            <p class="mt-1 max-w-2xl text-sm text-gray-500">
              Detalhamento por aluno no período selecionado
            </p>
          </div>
          <div v-if="loading" class="px-4 py-8 text-center">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600 mx-auto"></div>
            <p class="mt-2 text-sm text-gray-500">Carregando dados...</p>
          </div>
          <div v-else-if="error" class="px-4 py-5 sm:p-6 text-center text-red-500">
            {{ error }}
          </div>
          <div v-else-if="studentEarnings.length === 0" class="px-4 py-5 sm:p-6 text-center">
            <p class="text-gray-500">Nenhum registro de ganhos para o período selecionado.</p>
          </div>
          <div v-else class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Aluno
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Plano
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Aulas
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Valor por Aula
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Total
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="(earning, index) in studentEarnings" :key="index">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm font-medium text-gray-900">{{ earning.studentName }}</div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm text-gray-900">{{ earning.planTitle || 'Sem plano' }}</div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ earning.classes }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    R$ {{ earning.valuePerClass.toFixed(2) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-medium">
                    R$ {{ earning.total.toFixed(2) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Monthly History Chart -->
      <div class="px-4 sm:px-0 mb-8">
        <div class="bg-white shadow overflow-hidden sm:rounded-lg">
          <div class="px-4 py-5 sm:px-6">
            <h3 class="text-lg leading-6 font-medium text-gray-900">
              Histórico Mensal
            </h3>
            <p class="mt-1 max-w-2xl text-sm text-gray-500">
              Ganhos nos últimos 12 meses
            </p>
          </div>
          <div v-if="loadingHistory" class="px-4 py-8 text-center">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600 mx-auto"></div>
            <p class="mt-2 text-sm text-gray-500">Carregando histórico...</p>
          </div>
          <div v-else-if="errorHistory" class="px-4 py-5 sm:p-6 text-center text-red-500">
            {{ errorHistory }}
          </div>
          <div v-else class="border-t border-gray-200 px-4 py-5 sm:p-6">
            <div class="h-64 bg-gray-50 rounded-lg p-4 flex flex-col justify-center items-center">
              <p class="text-center text-gray-500">O histórico de ganhos será exibido aqui em um gráfico.</p>
              <p class="text-center text-sm text-gray-400 mt-2">Implementação futura com biblioteca de gráficos</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Return to Dashboard -->
      <div class="px-4 sm:px-0 mb-8">
        <router-link to="/professor" class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-indigo-700 bg-indigo-100 hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
          <svg class="mr-2 -ml-1 h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M9.707 14.707a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 1.414L7.414 9H15a1 1 0 110 2H7.414l2.293 2.293a1 1 0 010 1.414z" clip-rule="evenodd" />
          </svg>
          Voltar para o Dashboard
        </router-link>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAttendanceStore } from '../../stores/attendance';
import { useAuthStore } from '../../stores/auth';
import { useStudentsStore } from '../../stores/students';
import Breadcrumb from '@/components/Breadcrumb.vue';

// Stores
const attendanceStore = useAttendanceStore();
const authStore = useAuthStore();
const studentsStore = useStudentsStore();
const router = useRouter();
const route = useRoute();

// Breadcrumb items
const breadcrumbItems = computed(() => {
  return [
    { name: 'Professor', path: '/professor' },
    { name: 'Histórico de Ganhos', path: '/professor/earnings' }
  ];
});

// State
const loading = ref(false);
const loadingHistory = ref(false);
const error = ref(null);
const errorHistory = ref(null);
const studentEarnings = ref([]);
const monthlyHistory = ref([]);
const commission = ref(0); // Default commission

// Filter state
const currentDate = new Date();
const selectedYear = ref(currentDate.getFullYear());
const selectedMonth = ref(currentDate.getMonth());
const availableYears = ref([]);
const months = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

// Calculated values
const totalEarnings = computed(() => {
  return studentEarnings.value.reduce((total, student) => total + student.total, 0);
});

const totalClasses = computed(() => {
  return studentEarnings.value.reduce((total, student) => total + student.classes, 0);
});

const averagePerClass = computed(() => {
  if (totalClasses.value === 0) return 0;
  return totalEarnings.value / totalClasses.value;
});

// Initialize
onMounted(async () => {
  try {
    // Get professor data
    const user = authStore.user;
    if (!user) {
      router.push('/login');
      return;
    }
    
    // Set commission rate from professor data
    commission.value = user.commission || 0;
    
    // Setup available years (current year and 2 years back)
    const currentYear = new Date().getFullYear();
    availableYears.value = [currentYear - 2, currentYear - 1, currentYear];
    
    // Fetch data for the current month
    await fetchEarningsData();
    
    // Fetch monthly history data
    await fetchMonthlyHistory();
  } catch (err) {
    console.error('Error initializing earnings page:', err);
    error.value = 'Erro ao inicializar página: ' + err.message;
  }
});

// Methods
const fetchEarningsData = async () => {
  loading.value = true;
  error.value = null;
  studentEarnings.value = [];
  
  try {
    const user = authStore.user;
    
    // Calculate start and end dates for the selected month
    const startDate = new Date(selectedYear.value, selectedMonth.value, 1);
    const endDate = new Date(selectedYear.value, selectedMonth.value + 1, 0, 23, 59, 59);
    
    // Fetch attendance records for the selected month
    await attendanceStore.fetchAttendanceRecords(null, user.id, startDate, endDate);
    
    const records = attendanceStore.attendanceRecords.filter(record => record.present);
    
    if (records.length === 0) {
      loading.value = false;
      return;
    }
    
    // Get all students with attendance
    const studentIds = [...new Set(records.map(record => record.studentId))];
    
    // Count attendance per student
    const studentAttendance = records.reduce((acc, record) => {
      acc[record.studentId] = (acc[record.studentId] || 0) + 1;
      return acc;
    }, {});
    
    // Fetch all students data
    await Promise.all(studentIds.map(async (studentId) => {
      try {
        // Get student data
        const studentDoc = await studentsStore.fetchStudentById(studentId);
        if (!studentDoc) return;
        
        // Get plan data if available
        let planData = null;
        if (studentDoc.planId) {
          planData = await studentsStore.fetchPlanById(studentDoc.planId);
        }
        
        const attendanceCount = studentAttendance[studentId] || 0;
        let valuePerClass = 0;
        let total = 0;
        
        if (planData) {
          // Calculate price per class according to the formula:
          // Plan price / sessions per week / 4
          valuePerClass = (planData.price / planData.sessionsPerWeek / 4) * (commission.value / 100);
          total = valuePerClass * attendanceCount;
        }
        
        studentEarnings.value.push({
          studentId,
          studentName: studentDoc.name,
          planId: studentDoc.planId,
          planTitle: planData?.title || 'Sem plano',
          classes: attendanceCount,
          valuePerClass,
          total
        });
      } catch (err) {
        console.error(`Error processing student ${studentId}:`, err);
      }
    }));
    
    // Sort by total earnings (highest first)
    studentEarnings.value.sort((a, b) => b.total - a.total);
    
  } catch (err) {
    console.error('Error fetching earnings data:', err);
    error.value = 'Erro ao carregar dados: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const fetchMonthlyHistory = async () => {
  loadingHistory.value = true;
  errorHistory.value = null;
  monthlyHistory.value = [];
  
  try {
    const user = authStore.user;
    
    // Generate data for the last 12 months
    const currentMonth = new Date().getMonth();
    const currentYear = new Date().getFullYear();
    
    // For now just create placeholder data
    // In a real implementation, this would query attendance records for each month
    for (let i = 11; i >= 0; i--) {
      const monthIndex = (currentMonth - i + 12) % 12;
      const year = currentYear - Math.floor((i - currentMonth) / 12);
      
      monthlyHistory.value.push({
        month: months[monthIndex],
        year,
        earnings: 0 // Placeholder - would calculate real earnings
      });
    }
    
  } catch (err) {
    console.error('Error fetching monthly history:', err);
    errorHistory.value = 'Erro ao carregar histórico: ' + err.message;
  } finally {
    loadingHistory.value = false;
  }
};

const applyFilters = async () => {
  await fetchEarningsData();
};
</script>
