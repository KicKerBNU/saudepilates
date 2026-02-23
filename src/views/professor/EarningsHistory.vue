<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">{{ $t('professor.earningsHistory') }}</h1>
      </div>
    </header>

    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="px-4 py-6 sm:px-0">
        <Breadcrumb :items="breadcrumbItems" />
      </div>

      <!-- Two big cards -->
      <div class="px-4 sm:px-0 mb-8">
        <div v-if="loading" class="flex justify-center py-16">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
          <p class="mt-4 ml-4 text-gray-500">{{ $t('professor.loadingData') }}</p>
        </div>
        <div v-else-if="error" class="rounded-lg border border-red-200 bg-red-50 p-6 text-center text-red-600">
          {{ error }}
        </div>
        <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <!-- Card 1: This month -->
          <div class="rounded-xl shadow-lg bg-white border-2 border-indigo-100 overflow-hidden">
            <div class="px-6 py-5 bg-indigo-50 border-b border-indigo-100">
              <h2 class="text-lg font-semibold text-indigo-900">
                {{ $t('professor.earningsThisMonth') }}
              </h2>
              <p class="mt-1 text-sm text-indigo-700">
                {{ $t('professor.earningsThisMonthDesc') }}
              </p>
            </div>
            <div class="px-6 py-8 sm:py-10">
              <p class="text-3xl sm:text-4xl font-bold text-gray-900">
                {{ currency }} {{ formatCurrency(thisMonthTotal) }}
              </p>
            </div>
          </div>

          <!-- Card 2: Last 12 months -->
          <div class="rounded-xl shadow-lg bg-white border-2 border-gray-200 overflow-hidden">
            <div class="px-6 py-5 bg-gray-50 border-b border-gray-200">
              <h2 class="text-lg font-semibold text-gray-900">
                {{ $t('professor.earningsLast12Months') }}
              </h2>
              <p class="mt-1 text-sm text-gray-600">
                {{ $t('professor.earningsLast12MonthsDesc') }}
              </p>
            </div>
            <div class="px-6 py-8 sm:py-10">
              <p class="text-3xl sm:text-4xl font-bold text-gray-900">
                {{ currency }} {{ formatCurrency(last12MonthsTotal) }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Monthly history graph (last 12 months) -->
      <div v-if="!loading && !error" class="px-4 sm:px-0 mb-8">
        <div class="bg-white shadow overflow-hidden sm:rounded-lg">
          <div class="px-4 py-5 sm:px-6">
            <h3 class="text-lg leading-6 font-medium text-gray-900">
              {{ $t('professor.monthlyHistory') }}
            </h3>
            <p class="mt-1 max-w-2xl text-sm text-gray-500">
              {{ $t('professor.earningsLast12MonthsGraph') }}
            </p>
          </div>
          <div class="border-t border-gray-200 px-4 py-5 sm:p-6">
            <div class="space-y-3 min-h-[16rem]">
              <div
                v-for="(item, idx) in monthlyHistory"
                :key="idx"
                class="flex items-center gap-3"
              >
                <div class="w-24 shrink-0 text-sm text-gray-600">
                  {{ item.monthLabel }} {{ item.year }}
                </div>
                <div class="flex-1 flex items-center gap-2">
                  <div
                    class="h-8 rounded bg-indigo-500 min-w-[2px] transition-all"
                    :style="{ width: maxMonthlyEarnings ? (item.earnings / maxMonthlyEarnings * 100) + '%' : '0%' }"
                  />
                  <span class="text-sm font-medium text-gray-900 whitespace-nowrap">
                    {{ currency }} {{ formatCurrency(item.earnings) }}
                  </span>
                </div>
              </div>
              <p v-if="monthlyHistory.length && !maxMonthlyEarnings" class="text-sm text-gray-500">
                {{ $t('professor.noEarningsLast12Months') }}
              </p>
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
          {{ $t('professor.backToDashboard') }}
        </router-link>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';
import { usePaymentsStore } from '../../stores/payments';
import Breadcrumb from '@/components/Breadcrumb.vue';
import { useCompanyCurrency } from '@/composables/useCompanyCurrency';

const { t } = useI18n();
const { currency, formatCurrency } = useCompanyCurrency();

const authStore = useAuthStore();
const paymentsStore = usePaymentsStore();
const router = useRouter();

const breadcrumbItems = computed(() => [
  { name: t('professor.dashboard'), path: '/professor' },
  { name: t('professor.earningsHistory'), path: '/professor/earnings' }
]);

const loading = ref(false);
const error = ref(null);
const thisMonthTotal = ref(0);
const last12MonthsTotal = ref(0);
const monthlyHistory = ref([]);
const commission = ref(0);

const months = computed(() => [
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

const maxMonthlyEarnings = computed(() => {
  if (!monthlyHistory.value.length) return 0;
  return Math.max(...monthlyHistory.value.map(m => m.earnings), 0);
});

onMounted(async () => {
  try {
    if (!authStore.isAuthenticated || !authStore.isProfessor) {
      router.push('/login');
      return;
    }
    if (!authStore.userProfile && authStore.userId) {
      await authStore.fetchUserProfile(authStore.userId);
    }
    commission.value = authStore.userProfile?.commission ?? 0;
    await fetchEarnings();
  } catch (err) {
    console.error('Error initializing earnings page:', err);
    error.value = t('professor.errorInitializingPage', { message: err.message });
  }
});

async function fetchEarnings() {
  loading.value = true;
  error.value = null;
  thisMonthTotal.value = 0;
  last12MonthsTotal.value = 0;
  monthlyHistory.value = [];

  const professorId = authStore.userId;
  if (!professorId) {
    error.value = t('professor.errorProfileNotLoaded');
    loading.value = false;
    return;
  }

  try {
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();

    // Build last 12 months from professorPayments (same source for card and graph so they match)
    await paymentsStore.fetchProfessorPayments(professorId);
    const byMonth = {};
    for (let i = 11; i >= 0; i--) {
      const monthIndex = (currentMonth - i + 120) % 12;
      const year = currentYear + Math.floor((currentMonth - i) / 12);
      const key = `${year}-${monthIndex}`;
      byMonth[key] = { monthIndex, year, monthLabel: months.value[monthIndex], earnings: 0 };
    }

    for (const payment of paymentsStore.professorPayments) {
      const d = new Date(payment.paymentDate);
      const key = `${d.getFullYear()}-${d.getMonth()}`;
      if (byMonth[key]) {
        byMonth[key].earnings += Number(payment.amount) || 0;
      }
    }

    // 1) This month = same value as current month in the graph (payments received this month)
    const currentMonthKey = `${currentYear}-${currentMonth}`;
    thisMonthTotal.value = byMonth[currentMonthKey]?.earnings ?? 0;

    // 2) Last 12 months total
    let total12 = 0;
    for (const m of Object.values(byMonth)) total12 += m.earnings;
    last12MonthsTotal.value = total12;
    // Sort by year then month descending (most recent first); string keys like "2024-9" vs "2024-10" sort wrong
    monthlyHistory.value = Object.values(byMonth).sort((a, b) => {
      if (a.year !== b.year) return b.year - a.year;
      return b.monthIndex - a.monthIndex;
    });
  } catch (err) {
    console.error('Error fetching earnings:', err);
    error.value = t('professor.errorLoadingData', { message: err.message });
  } finally {
    loading.value = false;
  }
}
</script>
