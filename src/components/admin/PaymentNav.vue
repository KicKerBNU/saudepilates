<template>
  <div class="border-b border-gray-200 mb-6">
    <nav class="-mb-px flex space-x-6 sm:space-x-8 overflow-x-auto" aria-label="Payment navigation">
      <router-link
        v-for="tab in tabs"
        :key="tab.route"
        :to="{ name: tab.route }"
        :class="[
          currentRoute === tab.route
            ? 'border-indigo-500 text-indigo-600'
            : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
          'whitespace-nowrap pb-3 px-1 border-b-2 font-medium text-sm flex items-center gap-2'
        ]"
      >
        <svg class="h-4 w-4 flex-shrink-0" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="tab.icon" />
        </svg>
        {{ tab.label }}
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';

const route = useRoute();
const { t } = useI18n();

const currentRoute = computed(() => route.name);

const tabs = computed(() => [
  {
    route: 'MonthlyPayments',
    label: t('admin.monthlyPayments'),
    icon: 'M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z'
  },
  {
    route: 'PaymentVisualization',
    label: t('admin.paymentVisualization'),
    icon: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z'
  },
  {
    route: 'ProfessorPayments',
    label: t('admin.professorPaymentsTitle'),
    icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z'
  },
  {
    route: 'PaymentRegistration',
    label: t('admin.registerPayment'),
    icon: 'M12 6v6m0 0v6m0-6h6m-6 0H6'
  }
]);
</script>
