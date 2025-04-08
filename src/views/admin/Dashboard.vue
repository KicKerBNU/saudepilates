<template>
  <div class="min-h-screen bg-gray-100" 
       @touchstart="handleTouchStart" 
       @touchmove="handleTouchMove" 
       @touchend="handleTouchEnd">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">Dashboard do Administrador</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="px-4 py-6 sm:px-0">
        <Breadcrumb :items="breadcrumbItems" />
      </div>
      
      <!-- Subscription Alert -->
      <div v-if="showSubscriptionAlert" class="mb-6 px-4 sm:px-0">
        <div class="rounded-md bg-yellow-50 p-4">
          <div class="flex">
            <div class="flex-shrink-0">
              <svg class="h-5 w-5 text-yellow-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="ml-3">
              <h3 class="text-sm font-medium text-yellow-800">
                Alerta de Assinatura
              </h3>
              <div class="mt-2 text-sm text-yellow-700">
                <p>
                  {{ subscriptionAlertMessage }}
                </p>
              </div>
              <div class="mt-4">
                <div class="-mx-2 -my-1.5 flex">
                  <router-link :to="{name: 'SubscriptionPayment'}" class="bg-yellow-50 px-2 py-1.5 rounded-md text-sm font-medium text-yellow-800 hover:bg-yellow-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-yellow-50 focus:ring-yellow-600">
                    Renovar Assinatura
                  </router-link>
                  <button @click="dismissSubscriptionAlert" type="button" class="ml-3 bg-yellow-50 px-2 py-1.5 rounded-md text-sm font-medium text-yellow-800 hover:bg-yellow-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-yellow-50 focus:ring-yellow-600">
                    Ignorar
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Dashboard Stats Overview -->
      <div class="px-4 py-6 sm:px-0">
        <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
          <!-- Students Stats -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-indigo-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">
                      Total de Alunos
                    </dt>
                    <dd class="flex items-baseline">
                      <div v-if="loadingStudents" class="text-2xl font-semibold text-gray-900">
                        <svg class="animate-spin h-6 w-6 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                      </div>
                      <div v-else class="text-2xl font-semibold text-gray-900">
                        {{ totalStudents }}
                      </div>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-4 py-4 sm:px-6">
              <div class="text-sm">
                <router-link :to="{name: 'StudentsManagement'}" class="font-medium text-indigo-600 hover:text-indigo-500">
                  Ver todos os alunos <span aria-hidden="true">&rarr;</span>
                </router-link>
              </div>
            </div>
          </div>

          <!-- Professors Stats -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-green-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">
                      Total de Professores
                    </dt>
                    <dd class="flex items-baseline">
                      <div v-if="loadingProfessors" class="text-2xl font-semibold text-gray-900">
                        <svg class="animate-spin h-6 w-6 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                      </div>
                      <div v-else class="text-2xl font-semibold text-gray-900">
                        {{ totalProfessors }}
                      </div>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-4 py-4 sm:px-6">
              <div class="text-sm">
                <router-link :to="{name: 'ProfessorsManagement'}" class="font-medium text-indigo-600 hover:text-indigo-500">
                  Ver todos os professores <span aria-hidden="true">&rarr;</span>
                </router-link>
              </div>
            </div>
          </div>

          <!-- Payments Stats -->
          <router-link :to="{name: 'MonthlyPayments'}" class="cursor-pointer">
            <div class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50 transition duration-150">
              <div class="px-4 py-5 sm:p-6">
                <div class="flex items-center">
                  <div class="flex-shrink-0 bg-yellow-500 rounded-md p-3">
                    <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                  <div class="ml-5 w-0 flex-1">
                    <dl>
                      <dt class="text-sm font-medium text-gray-500 truncate">
                        Receita Mensal
                      </dt>
                      <dd class="flex items-baseline">
                        <div v-if="loadingMonthlyRevenue" class="text-2xl font-semibold text-gray-900">
                          <svg class="animate-spin h-6 w-6 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                          </svg>
                        </div>
                        <div v-else class="text-2xl font-semibold text-gray-900">
                          R$ {{ monthlyRevenue ? monthlyRevenue.toFixed(2) : '0.00' }}
                        </div>
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
              <div class="bg-gray-50 px-4 py-4 sm:px-6">
                <div class="text-sm">
                  <span class="font-medium text-indigo-600 hover:text-indigo-500">
                    Ver detalhes dos pagamentos mensais <span aria-hidden="true">&rarr;</span>
                  </span>
                </div>
              </div>
            </div>
          </router-link>

          <!-- Plans Stats -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-blue-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">
                      Total de Planos
                    </dt>
                    <dd class="flex items-baseline">
                      <div v-if="loadingPlans" class="text-2xl font-semibold text-gray-900">
                        <svg class="animate-spin h-6 w-6 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                      </div>
                      <div v-else class="text-2xl font-semibold text-gray-900">
                        {{ totalPlans }}
                      </div>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-4 py-4 sm:px-6">
              <div class="text-sm">
                <router-link :to="{name: 'PlansManagement'}" class="font-medium text-indigo-600 hover:text-indigo-500">
                  Ver todos os planos <span aria-hidden="true">&rarr;</span>
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Payment Success Message -->
      <div v-if="showPaymentSuccess" class="mt-8 px-4 sm:px-0">
        <div class="rounded-md bg-green-50 p-4">
          <div class="flex">
            <div class="flex-shrink-0">
              <svg class="h-5 w-5 text-green-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="ml-3">
              <h3 class="text-sm font-medium text-green-800">Pagamento registrado com sucesso!</h3>
              <div class="mt-2 text-sm text-green-700">
                <p>O pagamento foi registrado no sistema. A comissão do professor foi calculada automaticamente com base no valor final pago pelo aluno.</p>
              </div>
              <div class="mt-4">
                <div class="-mx-2 -my-1.5 flex">
                  <button @click="dismissPaymentSuccess()" type="button" class="rounded-md bg-green-50 px-2 py-1.5 text-sm font-medium text-green-800 hover:bg-green-100 focus:outline-none focus:ring-2 focus:ring-green-600 focus:ring-offset-2 focus:ring-offset-green-50">
                    Entendido
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Activity Section -->
      <div class="mt-8 px-4 sm:px-0">
        <h2 class="text-lg font-medium text-gray-900 mb-4">Atividades Recentes</h2>
        <div class="bg-white shadow overflow-hidden sm:rounded-md">
          <div v-if="loadingRecentActivities" class="flex items-center justify-center py-12">
            <svg class="animate-spin h-12 w-12 text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>
          <ul v-else role="list" class="divide-y divide-gray-200">
            <li v-if="recentPayments.length === 0" class="px-6 py-4 text-gray-500">
              Nenhuma atividade recente.
            </li>
            <li v-for="payment in recentPayments" :key="payment.id" class="px-4 py-4 sm:px-6">
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <div class="flex-shrink-0">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                  </div>
                  <div class="min-w-0 flex-1 px-4 md:grid md:grid-cols-2 md:gap-4">
                    <div>
                      <p class="text-sm font-medium text-indigo-600 truncate">{{ payment.studentName }}</p>
                      <p class="mt-2 flex items-center text-sm text-gray-500">
                        <span class="truncate">{{ payment.description }}</span>
                      </p>
                    </div>
                    <div class="hidden md:block">
                      <div>
                        <p class="text-sm text-gray-900">
                          {{ formatDate(payment.paymentDate) }}
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
                    R$ {{ payment.finalAmount ? payment.finalAmount.toFixed(2) : '0.00' }}
                  </span>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="mt-8 px-4 sm:px-0">
        <h2 class="text-lg font-medium text-gray-900 mb-4">Ações Rápidas</h2>
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <router-link :to="{name: 'PaymentRegistration'}" class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-yellow-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <h3 class="text-lg font-medium text-gray-900">Registrar Pagamento</h3>
                  <p class="mt-1 text-sm text-gray-500">
                    Registre um novo pagamento de mensalidade para alunos, incluindo opções para desconto em pagamentos antecipados.
                  </p>
                </div>
              </div>
            </div>
          </router-link>

          <!-- Payment Visualization Card -->
          <router-link :to="{name: 'PaymentVisualization'}" class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-purple-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <h3 class="text-lg font-medium text-gray-900">Visualização de Pagamentos</h3>
                  <p class="mt-1 text-sm text-gray-500">
                    Visualize gráficos e estatísticas dos pagamentos recebidos.
                  </p>
                </div>
              </div>
            </div>
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { useStudentsStore } from '../../stores/students';
import { useProfessorsStore } from '../../stores/professors';
import { usePaymentsStore } from '../../stores/payments';
import { useSubscriptionStore } from '../../stores/subscription';
import Breadcrumb from '@/components/Breadcrumb.vue';

// Initialize route and router
const route = useRoute();
const router = useRouter();

// Initialize stores
const showPaymentSuccess = ref(false);
const authStore = useAuthStore();
const studentsStore = useStudentsStore();
const professorsStore = useProfessorsStore();
const paymentsStore = usePaymentsStore();
const subscriptionStore = useSubscriptionStore();

// Subscription alert state
const showSubscriptionAlert = ref(false);
const subscriptionAlertMessage = ref('');

// Touch event variables
const touchStartX = ref(0);
const touchEndX = ref(0);
const minSwipeDistance = 50; // Minimum distance for a swipe to be registered

// Touch event handlers
const handleTouchStart = (e) => {
  touchStartX.value = e.touches[0].clientX;
};

const handleTouchMove = (e) => {
  touchEndX.value = e.touches[0].clientX;
};

const handleTouchEnd = () => {
  const swipeDistance = touchEndX.value - touchStartX.value;
  
  // Check if it's a left swipe (negative distance) and meets minimum distance
  if (swipeDistance < -minSwipeDistance) {
    router.go(-1); // Go back one step in history
  }
};

// Show success message if returning from successful payment registration
if (route.query.paymentSuccess === 'true') {
  showPaymentSuccess.value = true;
}

// Show success message if subscription was renewed
if (route.query.subscriptionRenewed === 'true') {
  // Display a success toast or notification here
}

// Method to dismiss the payment success message
const dismissPaymentSuccess = () => {
  showPaymentSuccess.value = false;
  // Update route without the query parameter
  router.replace({ name: 'AdminDashboard' });
};

// Method to dismiss the subscription alert
const dismissSubscriptionAlert = () => {
  showSubscriptionAlert.value = false;
  localStorage.setItem('subscriptionAlertDismissed', new Date().toISOString());
};

// Stats
const totalStudents = ref(0);
const totalProfessors = ref(0);
const monthlyRevenue = ref(0);
const recentPayments = ref([]);
const totalPlans = ref(0);
const currentMonth = new Date().getMonth();
const currentYear = new Date().getFullYear();
const loadingStudents = ref(true);
const loadingProfessors = ref(true);
const loadingMonthlyRevenue = ref(true);
const loadingPlans = ref(true);
const loadingRecentActivities = ref(true);

// Check if user is admin, if not redirect
onMounted(async () => {
  if (!authStore.isAdmin) {
    router.push('/');
    return;
  }
  
  try {
    // Load data in parallel
    await Promise.all([
      fetchStudents(),
      fetchProfessors(),
      fetchPayments(),
      fetchPlans(),
      checkSubscription()
    ]);
  } catch (error) {
    console.error('Error loading dashboard data:', error);
  }
});

const fetchStudents = async () => {
  try {
    // Use authStore to get students for the current company
    const students = await authStore.getUsersByCompany('student');
    totalStudents.value = students.length;
    loadingStudents.value = false;
  } catch (error) {
    console.error('Error fetching students:', error);
    totalStudents.value = 0;
    loadingStudents.value = false;
  }
};

const fetchProfessors = async () => {
  try {
    // Use authStore to get professors for the current company
    const professors = await authStore.getUsersByCompany('professor');
    totalProfessors.value = professors.length;
    loadingProfessors.value = false;
  } catch (error) {
    console.error('Error fetching professors:', error);
    totalProfessors.value = 0;
    loadingProfessors.value = false;
  }
};

const fetchPayments = async () => {
  try {
    const payments = await paymentsStore.fetchStudentPayments();
    
    // Calculate monthly revenue (current month)
    const currentMonth = new Date().getMonth();
    const currentYear = new Date().getFullYear();
    
    const monthlyPayments = payments.filter(payment => {
      const paymentDate = new Date(payment.paymentDate);
      return paymentDate.getMonth() === currentMonth && 
             paymentDate.getFullYear() === currentYear;
      // Removed status filter since all payments are now considered 'paid'
    });
    
    monthlyRevenue.value = monthlyPayments.reduce((total, payment) => {
      const amount = payment.finalAmount || payment.amount || 0;
      return total + amount;
    }, 0);
    
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
    recentPayments.value = recentPaymentsList.map(payment => ({
      ...payment,
      studentName: studentsMap[payment.studentId] || `Aluno ID: ${payment.studentId.substring(0, 6)}...`
    }));
    
    loadingMonthlyRevenue.value = false;
    loadingRecentActivities.value = false;
  } catch (error) {
    console.error('Error fetching payments:', error);
    loadingMonthlyRevenue.value = false;
    loadingRecentActivities.value = false;
  }
};

const fetchPlans = async () => {
  try {
    // Use authStore to get plans for the current company
    const plans = await authStore.getPlans();
    totalPlans.value = plans.length;
    loadingPlans.value = false;
  } catch (error) {
    console.error('Error fetching plans:', error);
    totalPlans.value = 0;
    loadingPlans.value = false;
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(date);
};

// Fetch subscription data and show alert if needed
const checkSubscription = async () => {
  try {
    await subscriptionStore.fetchSubscription();
    
    // Check if the alert was dismissed recently (in the last 24 hours)
    const lastDismissed = localStorage.getItem('subscriptionAlertDismissed');
    const showAlert = !lastDismissed || 
                      (new Date() - new Date(lastDismissed)) > (24 * 60 * 60 * 1000);
    
    if (showAlert) {
      const daysLeft = subscriptionStore.daysUntilExpiration;
      
      if (!subscriptionStore.isValid) {
        // Subscription expired
        subscriptionAlertMessage.value = 'Sua assinatura expirou. Renove agora para continuar utilizando todos os recursos da plataforma.';
        showSubscriptionAlert.value = true;
      } else if (daysLeft <= 7) {
        // Subscription expiring soon
        subscriptionAlertMessage.value = `Sua assinatura expira em ${daysLeft} dias. Renove agora para evitar interrupções.`;
        showSubscriptionAlert.value = true;
      }
    }
  } catch (error) {
    console.error('Error checking subscription:', error);
  }
};

const breadcrumbItems = computed(() => {
  const path = route.path;
  const segments = path.split('/').filter(Boolean);
  
  return segments.map((segment, index) => {
    const path = '/' + segments.slice(0, index + 1).join('/');
    const name = segment.charAt(0).toUpperCase() + segment.slice(1);
    return { name, path };
  });
});

// Logout functionality removed - now handled in NavBar
</script>
