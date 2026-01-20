<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-4 px-4 sm:py-6 sm:px-6 lg:px-8">
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-900">{{ $t('admin.paymentVisualization') }}</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-4 px-4 sm:py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="mb-4 sm:mb-6">
        <Breadcrumb :items="breadcrumbItems" />
      </div>
      
      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <svg class="animate-spin h-8 w-8 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </div>
      
      <div v-else class="space-y-4 sm:space-y-6">
        <!-- Summary Cards -->
        <div class="grid grid-cols-1 gap-4 sm:gap-6 sm:grid-cols-2 lg:grid-cols-3">
          <!-- Total Revenue Card -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="p-4 sm:p-5">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <svg class="h-6 w-6 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div class="ml-4 sm:ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">{{ $t('admin.totalRevenueCurrentMonth') }}</dt>
                    <dd class="text-base sm:text-lg font-medium text-gray-900">{{ $t('common.currency') }} {{ formatCurrency(totalCurrentMonth) }}</dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>

          <!-- Professor Payments Card -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="p-4 sm:p-5">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <svg class="h-6 w-6 text-red-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                </div>
                <div class="ml-4 sm:ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">{{ $t('admin.professorPaymentsCurrentMonth') }}</dt>
                    <dd class="text-base sm:text-lg font-medium text-gray-900">{{ $t('common.currency') }} {{ formatCurrency(totalProfessorPaymentsCurrentMonth) }}</dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>

          <!-- Net Profit Card -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="p-4 sm:p-5">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <svg class="h-6 w-6 text-green-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                  </svg>
                </div>
                <div class="ml-4 sm:ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">{{ $t('admin.netProfitCurrentMonth') }}</dt>
                    <dd class="text-base sm:text-lg font-medium text-gray-900">{{ $t('common.currency') }} {{ formatCurrency(netProfitCurrentMonth) }}</dd>
                  </dl>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Payment Chart -->
        <div class="mt-4 sm:mt-6">
          <PaymentChart :payments="payments" />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';
import { usePaymentsStore } from '../../stores/payments';
import PaymentChart from '../../components/admin/PaymentChart.vue';
import Breadcrumb from '@/components/Breadcrumb.vue';

const { t, locale } = useI18n();

const router = useRouter();
const authStore = useAuthStore();
const paymentsStore = usePaymentsStore();
const route = useRoute();

const loading = ref(true);
const payments = ref([]);

// Add breadcrumb items
const breadcrumbItems = computed(() => {
  const path = route.path;
  const segments = path.split('/').filter(Boolean);
  
  return segments.map((segment, index) => {
    let path = '/' + segments.slice(0, index + 1).join('/');
    let name = segment.charAt(0).toUpperCase() + segment.slice(1);
    
    // Special handling for specific segments
    if (segment === 'payments') {
      name = t('admin.payments');
      path = '/admin';
    } else if (segment === 'monthly') {
      name = t('admin.monthly');
    } else if (segment === 'visualization') {
      name = t('admin.visualization');
    }
    
    return { name, path };
  }).filter(item => item && item.name && item.path); // Filter out any invalid items
});

// Computed properties for summary cards
const totalCurrentMonth = computed(() => {
  const currentDate = new Date();
  const currentMonth = currentDate.getMonth();
  const currentYear = currentDate.getFullYear();
  
  return payments.value
    .filter(payment => {
      const paymentDate = new Date(payment.paymentDate);
      return paymentDate.getMonth() === currentMonth && 
             paymentDate.getFullYear() === currentYear;
    })
    .reduce((total, payment) => total + (payment.finalAmount || payment.amount || 0), 0);
});

const totalProfessorPaymentsCurrentMonth = computed(() => {
  const currentDate = new Date();
  const currentMonth = currentDate.getMonth();
  const currentYear = currentDate.getFullYear();
  
  return payments.value
    .filter(payment => {
      const paymentDate = new Date(payment.paymentDate);
      return paymentDate.getMonth() === currentMonth && 
             paymentDate.getFullYear() === currentYear;
    })
    .reduce((total, payment) => total + (payment.commissionAmount || 0), 0);
});

const netProfitCurrentMonth = computed(() => {
  return totalCurrentMonth.value - totalProfessorPaymentsCurrentMonth.value;
});

// Helper function to format currency
function formatCurrency(value) {
  const localeMap = {
    'pt': 'pt-BR',
    'en': 'en-US',
    'es': 'es-ES',
    'fr': 'fr-FR'
  };
  return value.toLocaleString(localeMap[locale.value] || 'pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// Fetch payments data
async function fetchPayments() {
  try {
    payments.value = await paymentsStore.fetchStudentPayments();
  } catch (error) {
    console.error('Error fetching payments:', error);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchPayments();
});
</script> 