<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8 flex justify-between items-center">
        <h1 class="text-3xl font-bold text-gray-900">Pagamentos Mensais</h1>
        <div class="flex items-center space-x-4">
          <span class="text-gray-600">
            {{ currentMonthYear }}
          </span>
          <div id="monthSelector" class="relative inline-block text-left">
            <button @click="toggleMonthSelector" type="button" class="inline-flex justify-center w-full rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
              {{ months[selectedMonth] }} {{ selectedYear }}
              <svg class="-mr-1 ml-2 h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </button>
            <div v-show="isMonthSelectorOpen" class="month-selector-dropdown origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 z-10">
              <div class="py-1" role="menu" aria-orientation="vertical" aria-labelledby="options-menu">
                <button 
                  v-for="(month, index) in months" 
                  :key="index" 
                  @click="selectMonth(index)"
                  class="block w-full text-left px-4 py-2 text-sm hover:bg-gray-100 hover:text-gray-900" 
                  :class="selectedMonth === index ? 'bg-indigo-100 text-indigo-900 font-medium' : 'text-gray-700'"
                  role="menuitem"
                >
                  {{ month }} {{ selectedYear }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Summary Card -->
      <div class="px-4 py-5 sm:p-6 bg-white shadow sm:rounded-lg mb-6">
        <h2 class="text-lg font-medium text-gray-900">Resumo do Mês</h2>
        <div class="mt-4 grid grid-cols-1 gap-5 sm:grid-cols-3">
          <div class="bg-indigo-50 overflow-hidden rounded-lg px-4 py-5">
            <dt class="text-sm font-medium text-indigo-800 truncate">Total Recebido</dt>
            <dd class="mt-1 text-3xl font-semibold text-indigo-900">R$ {{ calculateTotalPaid() }}</dd>
          </div>
          <div class="bg-yellow-50 overflow-hidden rounded-lg px-4 py-5">
            <dt class="text-sm font-medium text-yellow-800 truncate">Pagamentos Pendentes</dt>
            <dd class="mt-1 text-3xl font-semibold text-yellow-900">R$ {{ calculateTotalPending() }}</dd>
          </div>
          <div class="bg-green-50 overflow-hidden rounded-lg px-4 py-5">
            <dt class="text-sm font-medium text-green-800 truncate">Projeção Total</dt>
            <dd class="mt-1 text-3xl font-semibold text-green-900">R$ {{ calculateTotalProjection() }}</dd>
          </div>
        </div>
      </div>

      <!-- Filter Controls -->
      <div class="px-4 sm:px-0 mb-5">
        <div class="flex flex-wrap gap-2">
          <button 
            @click="activeFilter = 'all'" 
            :class="[
              'px-4 py-2 rounded-full text-sm font-medium',
              activeFilter === 'all' 
                ? 'bg-indigo-600 text-white' 
                : 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-300'
            ]"
          >
            Todos Pagamentos
          </button>
          <button 
            @click="activeFilter = 'withStudents'" 
            :class="[
              'px-4 py-2 rounded-full text-sm font-medium',
              activeFilter === 'withStudents' 
                ? 'bg-green-600 text-white' 
                : 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-300'
            ]"
          >
            Com Pagamento
          </button>
          <button 
            @click="activeFilter = 'withoutStudents'" 
            :class="[
              'px-4 py-2 rounded-full text-sm font-medium',
              activeFilter === 'withoutStudents' 
                ? 'bg-yellow-600 text-white' 
                : 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-300'
            ]"
          >
            Sem Pagamento
          </button>
        </div>
      </div>

      <!-- Payment List -->
      <div class="bg-white shadow overflow-hidden sm:rounded-md">
        <ul role="list" class="divide-y divide-gray-200">
          <li v-if="filteredPayments.length === 0" class="px-6 py-4 text-gray-500">
            Nenhum pagamento encontrado para este filtro.
          </li>
          <li v-for="payment in filteredPayments" :key="payment.id" class="px-4 py-4 sm:px-6 hover:bg-gray-50">
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                </div>
                <div class="min-w-0 flex-1 px-4">
                  <div>
                    <p class="text-sm font-medium text-indigo-600 truncate">{{ payment.studentName }}</p>
                    <p class="mt-1 flex items-center text-sm text-gray-500">
                      <span class="truncate">{{ payment.description }}</span>
                    </p>
                    <div class="mt-2 sm:flex sm:justify-between">
                      <div class="sm:flex">
                        <p class="flex items-center text-sm text-gray-700">
                          <span class="mr-2">Período:</span>
                          <span class="font-medium">{{ getPaymentPeriodText(payment) }}</span>
                        </p>
                      </div>
                      <div class="mt-2 flex items-center text-sm text-gray-700 sm:mt-0">
                        <p class="text-sm">
                          Data: {{ formatDate(payment.paymentDate) }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="ml-5 flex flex-col items-end space-y-2">
                <span class="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                  Pago
                </span>
                <span class="px-2 py-1 inline-flex text-sm leading-4 font-medium">
                  R$ {{ (payment.finalAmount || payment.amount).toFixed(2) }}
                </span>
              </div>
            </div>
          </li>
        </ul>
      </div>

      <!-- Students Without Payments Section -->
      <div v-if="studentsWithoutPayments.length > 0 && showStudentsWithoutPayments" class="mt-8">
        <h3 class="text-lg font-medium text-gray-900 mb-4">Alunos Sem Pagamento no Mês</h3>
        <div class="bg-white shadow overflow-hidden sm:rounded-md">
          <ul role="list" class="divide-y divide-gray-200">
            <li v-for="student in studentsWithoutPayments" :key="student.id" class="px-4 py-4 sm:px-6 hover:bg-gray-50">
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <div class="flex-shrink-0">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                  </div>
                  <div class="min-w-0 flex-1 px-4">
                    <div>
                      <p class="text-sm font-medium text-indigo-600 truncate">{{ student.name || `${student.firstName || ''} ${student.lastName || ''}`.trim() }}</p>
                      <p class="mt-1 flex items-center text-sm text-gray-500">
                        <span class="truncate">{{ student.planTitle || 'Sem plano associado' }}</span>
                      </p>
                    </div>
                  </div>
                </div>
                <div class="ml-5 flex flex-col items-end">
                  <router-link 
                    :to="{name: 'PaymentRegistration', query: { studentId: student.id }}" 
                    class="inline-flex items-center px-3 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    Registrar Pagamento
                  </router-link>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useAuthStore } from '../../stores/auth';
import { usePaymentsStore } from '../../stores/payments';

const authStore = useAuthStore();
const paymentsStore = usePaymentsStore();

// Month selection
const months = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];
const isMonthSelectorOpen = ref(false);
const selectedMonth = ref(new Date().getMonth());
const selectedYear = ref(new Date().getFullYear());

const currentMonthYear = computed(() => {
  return `${months[selectedMonth.value]} ${selectedYear.value}`;
});

// Filter state
const activeFilter = ref('all');

// Data
const allPayments = ref([]);
const allStudents = ref([]);
const expectedPayments = ref([]); // Used to track which students should have paid this month

// Fetch data
const fetchData = async () => {
  try {
    // Get students
    const students = await authStore.getUsersByCompany('student');
    allStudents.value = students.filter(student => student.isActive !== false);
    
    // Get all plans
    const plans = await authStore.getPlans();
    const plansMap = {};
    plans.forEach(plan => {
      plansMap[plan.id] = plan;
    });
    
    // Attach plan information to students
    const studentsWithPlans = allStudents.value.map(student => {
      if (student.planId && plansMap[student.planId]) {
        const plan = plansMap[student.planId];
        return {
          ...student,
          planTitle: plan.title || 'Plano não encontrado',
          planPrice: plan.price || 0
        };
      }
      return {
        ...student,
        planTitle: 'Sem plano',
        planPrice: 0
      };
    });
    
    allStudents.value = studentsWithPlans;
    
    // Get payments
    const payments = await paymentsStore.fetchStudentPayments();
    
    // Filter payments for the selected month/year
    const filteredPayments = payments.filter(payment => {
      // Skip payments without a date
      if (!payment.paymentDate) {
        return false;
      }
      
      const paymentDate = new Date(payment.paymentDate);
      
      // For regular monthly payments
      if (!payment.period || payment.period === 'month') {
        return paymentDate.getMonth() === selectedMonth.value && 
               paymentDate.getFullYear() === selectedYear.value;
      }
      
      // For quarterly (3 months) payments
      if (payment.period === 'quarter') {
        // Check if the selected month falls within the 3-month period
        const paymentMonth = paymentDate.getMonth();
        const paymentYear = paymentDate.getFullYear();
        
        for (let i = 0; i < 3; i++) {
          const month = (paymentMonth + i) % 12;
          const year = paymentYear + Math.floor((paymentMonth + i) / 12);
          
          if (month === selectedMonth.value && year === selectedYear.value) {
            return true;
          }
        }
      }
      
      // For semi-annual (6 months) payments
      if (payment.period === 'semiannual') {
        // Check if the selected month falls within the 6-month period
        const paymentMonth = paymentDate.getMonth();
        const paymentYear = paymentDate.getFullYear();
        
        for (let i = 0; i < 6; i++) {
          const month = (paymentMonth + i) % 12;
          const year = paymentYear + Math.floor((paymentMonth + i) / 12);
          
          if (month === selectedMonth.value && year === selectedYear.value) {
            return true;
          }
        }
      }
      
      // For annual (12 months) payments
      if (payment.period === 'annual') {
        // Check if the selected month falls within the 12-month period
        const paymentMonth = paymentDate.getMonth();
        const paymentYear = paymentDate.getFullYear();
        
        for (let i = 0; i < 12; i++) {
          const month = (paymentMonth + i) % 12;
          const year = paymentYear + Math.floor((paymentMonth + i) / 12);
          
          if (month === selectedMonth.value && year === selectedYear.value) {
            return true;
          }
        }
      }
      
      return false;
    });
    
    // Add student names to payments
    const studentsMap = {};
    allStudents.value.forEach(student => {
      studentsMap[student.id] = student.name || 
        `${student.firstName || ''} ${student.lastName || ''}`.trim() || 'Aluno sem nome';
    });
    
    allPayments.value = filteredPayments.map(payment => {
      // Ensure all payments are marked as paid
      return {
        ...payment,
        status: 'paid', // Force status to be paid
        studentName: studentsMap[payment.studentId] || `Aluno ID: ${payment.studentId?.substring(0, 6)}...`
      };
    });
    
    // Set up expected payments for the current month
    calculateExpectedPayments();
    
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};

// Calculate expected payments for all students
const calculateExpectedPayments = () => {
  expectedPayments.value = allStudents.value.map(student => ({
    studentId: student.id,
    studentName: student.name || `${student.firstName || ''} ${student.lastName || ''}`.trim(),
    planTitle: student.planTitle,
    expectedAmount: student.planPrice || 0,
    status: 'pending'
  }));
};

// Calculate total paid function (more reliable than computed)
const calculateTotalPaid = () => {
  let total = 0;
  
  if (!allPayments.value || allPayments.value.length === 0) {
    return '0.00';
  }
  
  // Manually sum all payment amounts
  for (const payment of allPayments.value) {
    // Make sure we have actual numbers
    const amount = parseFloat(payment.finalAmount || payment.amount || 0);
    if (!isNaN(amount)) {
      total += amount;
    }
  }
  
  return total.toFixed(2);
};

// Calculate pending payments
const calculateTotalPending = () => {
  // We only calculate expected amounts for students who don't have any payment record
  let total = 0;
  
  for (const student of studentsWithoutPayments.value) {
    const amount = parseFloat(student.planPrice || 0);
    if (!isNaN(amount)) {
      total += amount;
    }
  }
  
  return total.toFixed(2);
};

// Calculate total projected revenue for the month
const calculateTotalProjection = () => {
  const paid = parseFloat(calculateTotalPaid());
  const pending = parseFloat(calculateTotalPending());
  return (paid + pending).toFixed(2);
};

// Filter payments based on active filter
const filteredPayments = computed(() => {
  if (activeFilter.value === 'all' || activeFilter.value === 'withStudents') {
    return allPayments.value;
  }
  return [];
});

// Compute students who haven't made a payment this month
const studentsWithoutPayments = computed(() => {
  const studentsWithPayments = new Set(allPayments.value.map(payment => payment.studentId));
  
  return allStudents.value.filter(student => 
    !studentsWithPayments.has(student.id) && 
    student.planPrice > 0 // Only include students who have a plan with a price
  );
});

// Determine if we should show the students without payments section
const showStudentsWithoutPayments = computed(() => {
  return activeFilter.value === 'all' || activeFilter.value === 'withoutStudents';
});

// Toggle month selector dropdown
const toggleMonthSelector = () => {
  isMonthSelectorOpen.value = !isMonthSelectorOpen.value;
};

// Handle clicks outside of month selector dropdown
const handleClickOutside = (event) => {
  const monthSelectorEl = document.getElementById('monthSelector');
  // Don't close if we're clicking on the selector itself
  if (event.target.closest('#monthSelector')) {
    return;
  }
  // Close if dropdown is open and clicking outside
  if (isMonthSelectorOpen.value) {
    isMonthSelectorOpen.value = false;
  }
};

// Change month handler
const selectMonth = (monthIndex) => {
  selectedMonth.value = monthIndex;
  isMonthSelectorOpen.value = false;
  fetchData();
};

// Utility function to get a human-readable payment period text
const getPaymentPeriodText = (payment) => {
  if (!payment.period || payment.period === 'month') {
    return 'Mensal';
  } else if (payment.period === 'quarter') {
    return 'Trimestral';
  } else if (payment.period === 'semiannual') {
    return 'Semestral';
  } else if (payment.period === 'annual') {
    return 'Anual';
  }
  return payment.period;
};

// Format date
const formatDate = (dateString) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(date);
};

// Document click handler is already defined above

// Initialize
onMounted(async () => {
  if (!authStore.isAdmin) {
    router.push('/login');
    return;
  }
  
  await fetchData();
  
  // Add click outside handler
  document.addEventListener('click', handleClickOutside);
});

// Clean up event listener
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>
