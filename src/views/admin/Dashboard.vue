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
      <SubscriptionAlert 
        :show="showSubscriptionAlert"
        :message="subscriptionAlertMessage"
        @dismiss="dismissSubscriptionAlert"
      />
      
      <!-- Dashboard Stats Overview -->
      <div class="px-4 py-6 sm:px-0">
        <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
          <!-- Students Stats -->
          <StatsCard
            title="Total de Alunos"
            :value="totalStudents"
            :loading="loadingStudents"
            iconPath="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"
            iconBgColor="bg-indigo-500"
            :linkTo="{name: 'StudentsManagement'}"
            linkText="Ver todos os alunos"
          />

          <!-- Professors Stats -->
          <StatsCard
            title="Total de Professores"
            :value="totalProfessors"
            :loading="loadingProfessors"
            iconPath="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"
            iconBgColor="bg-green-500"
            :linkTo="{name: 'ProfessorsManagement'}"
            linkText="Ver todos os professores"
          />

          <!-- Payments Stats -->
          <router-link :to="{name: 'MonthlyPayments'}" class="cursor-pointer">
            <StatsCard
              title="Receita Mensal"
              :value="monthlyRevenue ? `R$ ${monthlyRevenue.toFixed(2)}` : 'R$ 0.00'"
              :loading="loadingMonthlyRevenue"
              iconPath="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              iconBgColor="bg-yellow-500"
              :linkTo="{name: 'MonthlyPayments'}"
              linkText="Ver detalhes dos pagamentos mensais"
            />
          </router-link>

          <!-- Plans Stats -->
          <StatsCard
            title="Total de Planos"
            :value="totalPlans"
            :loading="loadingPlans"
            iconPath="M12 4v16m8-8H4"
            iconBgColor="bg-blue-500"
            :linkTo="{name: 'PlansManagement'}"
            linkText="Ver todos os planos"
          />
        </div>
      </div>

      <!-- Payment Success Message -->
      <SuccessMessage 
        :show="showPaymentSuccess"
        title="Pagamento registrado com sucesso!"
        message="O pagamento foi registrado no sistema. A comissão do professor foi calculada automaticamente com base no valor final pago pelo aluno."
        @dismiss="dismissPaymentSuccess"
      />

      <!-- Recent Activity Section -->
      <RecentActivities />

      <!-- Quick Actions -->
      <QuickActions />
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
import RecentActivities from '@/components/admin/RecentActivities.vue';
import QuickActions from '@/components/admin/QuickActions.vue';
import SuccessMessage from '@/components/admin/SuccessMessage.vue';
import SubscriptionAlert from '@/components/admin/SubscriptionAlert.vue';
import StatsCard from '@/components/admin/StatsCard.vue';

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
const totalPlans = ref(0);
const currentMonth = new Date().getMonth();
const currentYear = new Date().getFullYear();
const loadingStudents = ref(true);
const loadingProfessors = ref(true);
const loadingMonthlyRevenue = ref(true);
const loadingPlans = ref(true);

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
    
    loadingMonthlyRevenue.value = false;
  } catch (error) {
    console.error('Error fetching payments:', error);
    loadingMonthlyRevenue.value = false;
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
