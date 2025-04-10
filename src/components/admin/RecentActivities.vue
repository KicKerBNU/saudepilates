<template>
  <div class="mt-8 px-4 sm:px-0">
    <h2 class="text-lg font-medium text-gray-900 mb-4">Atividades Recentes</h2>
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <div v-if="loading" class="flex items-center justify-center py-12">
        <svg class="animate-spin h-12 w-12 text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </div>
      <ul v-else role="list" class="divide-y divide-gray-200">
        <li v-if="activities.length === 0" class="px-6 py-4 text-gray-500">
          Nenhuma atividade recente.
        </li>
        <li v-for="activity in activities" :key="activity.id" class="px-4 py-4 sm:px-6">
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <div class="min-w-0 flex-1 px-4 md:grid md:grid-cols-2 md:gap-4">
                <div>
                  <p class="text-sm font-medium text-indigo-600 truncate">{{ activity.studentName }}</p>
                  <p class="mt-2 flex items-center text-sm text-gray-500">
                    <span class="truncate">{{ activity.description }}</span>
                  </p>
                </div>
                <div class="hidden md:block">
                  <div>
                    <p class="text-sm text-gray-900">
                      {{ formatDate(activity.paymentDate) }}
                    </p>
                    <p class="mt-2 flex items-center text-sm text-green-600">
                      <svg class="h-4 w-4 mr-1" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                      </svg>
                      Pago
                    </p>
                  </div>
                </div>
              </div>
            </div>
            <div class="ml-5 flex-shrink-0">
              <span class="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                R$ {{ activity.finalAmount ? activity.finalAmount.toFixed(2) : '0.00' }}
              </span>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { usePaymentsStore } from '../../stores/payments';
import { useAuthStore } from '../../stores/auth';

const paymentsStore = usePaymentsStore();
const authStore = useAuthStore();

const loading = ref(true);
const activities = ref([]);

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(date);
};

const fetchActivities = async () => {
  try {
    const payments = await paymentsStore.fetchStudentPayments();
    
    // Get recent payments (last 5)
    let recentPaymentsList = payments.slice(0, 5);
    
    // Fetch student names for each payment
    const studentsData = await authStore.getUsersByCompany('student');
    const studentsMap = {};
    
    // Create a map of student IDs to names for quick lookup
    studentsData.forEach(student => {
      studentsMap[student.id] = student.name || 
        `${student.firstName || ''} ${student.lastName || ''}`.trim() || 'Aluno sem nome';
    });
    
    // Add student names to payments
    activities.value = recentPaymentsList.map(payment => ({
      ...payment,
      studentName: studentsMap[payment.studentId] || `Aluno ID: ${payment.studentId.substring(0, 6)}...`
    }));
  } catch (error) {
    console.error('Error fetching activities:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchActivities();
});
</script> 