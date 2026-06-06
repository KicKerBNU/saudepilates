<template>
  <div class="min-h-screen bg-gray-100" 
       @touchstart="handleTouchStart" 
       @touchmove="handleTouchMove" 
       @touchend="handleTouchEnd">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">{{ $t('admin.dashboard') }}</h1>
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
      
      <div class="px-4 pb-6 sm:px-0">
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <DashboardFeatureCard
            :title="$t('admin.totalStudents')"
            :value="totalStudents"
            :loading="loadingStudents"
            icon-path="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"
            icon-bg-color="bg-indigo-500"
            :link-to="{ name: 'StudentsManagement' }"
            :link-text="$t('admin.viewAllStudents')"
          />

          <DashboardFeatureCard
            :title="$t('admin.totalProfessors')"
            :value="totalProfessors"
            :loading="loadingProfessors"
            icon-path="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"
            icon-bg-color="bg-green-500"
            :link-to="{ name: 'ProfessorsManagement' }"
            :link-text="$t('admin.viewAllProfessors')"
          />

          <DashboardFeatureCard
            :title="$t('admin.totalPlans')"
            :value="totalPlans"
            :loading="loadingPlans"
            icon-path="M12 4v16m8-8H4"
            icon-bg-color="bg-blue-500"
            :link-to="{ name: 'PlansManagement' }"
            :link-text="$t('admin.viewAllPlans')"
          />

          <DashboardFeatureCard
            :title="$t('admin.monthlyRevenue')"
            :value="formattedMonthlyRevenue"
            :loading="loadingMonthlyRevenue"
            icon-path="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            icon-bg-color="bg-yellow-500"
            :link-to="{ name: 'MonthlyPayments' }"
            :link-text="$t('admin.viewMonthlyPayments')"
          />

          <DashboardFeatureCard
            :title="$t('admin.registerPayment')"
            :description="$t('admin.registerPaymentDesc')"
            icon-path="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"
            icon-bg-color="bg-amber-500"
            :link-to="{ name: 'PaymentRegistration' }"
            :link-text="$t('admin.registerPayment')"
          />

          <DashboardFeatureCard
            :title="$t('admin.paymentVisualization')"
            :description="$t('admin.paymentVisualizationDesc')"
            icon-path="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
            icon-bg-color="bg-purple-500"
            :link-to="{ name: 'PaymentVisualization' }"
            :link-text="$t('admin.paymentVisualization')"
          />

          <DashboardFeatureCard
            :title="$t('admin.professorPaymentsTitle')"
            :description="$t('admin.professorPaymentsDesc')"
            icon-path="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
            icon-bg-color="bg-emerald-500"
            :link-to="{ name: 'ProfessorPayments' }"
            :link-text="$t('admin.professorPaymentsTitle')"
          />

          <DashboardFeatureCard
            :title="$t('admin.classSchedule')"
            :description="$t('admin.classScheduleDesc')"
            icon-path="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
            icon-bg-color="bg-sky-500"
            :link-to="{ name: 'AdminSchedule' }"
            :link-text="$t('admin.classSchedule')"
          />

          <DashboardFeatureCard
            :title="$t('anamnesis.title')"
            :description="$t('anamnesis.adminCardDesc')"
            icon-path="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
            icon-bg-color="bg-indigo-500"
            :link-to="{ name: 'AdminAnamnesis' }"
            :link-text="$t('anamnesis.viewAllAnamnesis')"
          />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';
import { useCompanyCurrency } from '@/composables/useCompanyCurrency';

const { t } = useI18n();
const { currency, formatCurrency } = useCompanyCurrency();

// Computed property to format monthly revenue with currency
const formattedMonthlyRevenue = computed(() => {
  if (monthlyRevenue.value) {
    return `${currency.value} ${formatCurrency(monthlyRevenue.value)}`;
  }
  return `${currency.value} ${formatCurrency(0)}`;
});
import { useStudentsStore } from '../../stores/students';
import { useProfessorsStore } from '../../stores/professors';
import { usePaymentsStore } from '../../stores/payments';
import { useSubscriptionStore } from '../../stores/subscription';
import Breadcrumb from '@/components/Breadcrumb.vue';
import DashboardFeatureCard from '@/components/admin/DashboardFeatureCard.vue';
import SubscriptionAlert from '@/components/admin/SubscriptionAlert.vue';

// Initialize route and router
const route = useRoute();
const router = useRouter();

// Initialize stores
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


// Show success message if subscription was renewed
if (route.query.subscriptionRenewed === 'true') {
  // Display a success toast or notification here
}


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
    totalStudents.value = students.filter(student => student.isActive !== false).length;
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
