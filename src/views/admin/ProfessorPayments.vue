<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-4 px-4 sm:py-6 sm:px-6 lg:px-8 flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3">
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-900">{{ $t('admin.professorPaymentsTitle') }}</h1>
        <div class="flex items-center space-x-3">
          <span class="text-gray-600 text-sm">{{ currentMonthYear }}</span>
          <div id="monthSelector" class="relative inline-block text-left">
            <button @click="toggleMonthSelector" type="button" class="inline-flex justify-center w-full rounded-md border border-gray-300 shadow-sm px-3 py-2 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
              {{ months[selectedMonth] }} {{ selectedYear }}
              <svg class="-mr-1 ml-2 h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </button>
            <div v-show="isMonthSelectorOpen" class="origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 z-10">
              <div class="py-1">
                <button
                  v-for="(month, index) in months"
                  :key="index"
                  @click="selectMonth(index)"
                  class="block w-full text-left px-4 py-2 text-sm hover:bg-gray-100"
                  :class="selectedMonth === index ? 'bg-indigo-100 text-indigo-900 font-medium' : 'text-gray-700'"
                >
                  {{ month }} {{ selectedYear }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto py-4 sm:py-6 px-4 sm:px-6 lg:px-8">
      <div class="mb-4">
        <Breadcrumb :items="breadcrumbItems" />
      </div>

      <!-- Payment Sub-Navigation -->
      <PaymentNav />

      <!-- Professor Selector -->
      <div class="bg-white shadow rounded-lg p-4 sm:p-6 mb-6">
        <label for="professorSelect" class="block text-sm font-medium text-gray-700 mb-2">{{ $t('admin.selectProfessor') }}</label>
        <select
          id="professorSelect"
          v-model="selectedProfessorId"
          class="block w-full sm:w-80 rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
          :disabled="loadingProfessors"
        >
          <option value="">{{ $t('admin.chooseProfessor') }}</option>
          <option v-for="prof in professors" :key="prof.id" :value="prof.id">
            {{ prof.name || `${prof.firstName || ''} ${prof.lastName || ''}`.trim() }}
          </option>
        </select>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-12">
        <svg class="animate-spin h-8 w-8 mx-auto text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p class="mt-2 text-sm text-gray-500">{{ $t('admin.loadingPayments') }}</p>
      </div>

      <!-- No professor selected -->
      <div v-else-if="!selectedProfessorId" class="bg-white shadow rounded-lg p-8 text-center">
        <svg class="mx-auto h-12 w-12 text-gray-300" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
        <p class="mt-3 text-sm text-gray-500">{{ $t('admin.selectProfessorPrompt') }}</p>
      </div>

      <!-- Professor selected: show data -->
      <template v-else>
        <!-- Summary Cards -->
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-3 mb-6">
          <div class="bg-white shadow rounded-lg p-4 sm:p-5">
            <dt class="text-sm font-medium text-gray-500 truncate">{{ $t('admin.totalCommissions') }}</dt>
            <dd class="mt-1 text-2xl sm:text-3xl font-semibold text-indigo-900">{{ currency }} {{ formatCurrency(totalCommission) }}</dd>
          </div>
          <div class="bg-white shadow rounded-lg p-4 sm:p-5">
            <dt class="text-sm font-medium text-gray-500 truncate">{{ $t('admin.studentsWithCommission') }}</dt>
            <dd class="mt-1 text-2xl sm:text-3xl font-semibold text-gray-900">{{ professorPaymentsForMonth.length }}</dd>
          </div>
          <div class="bg-white shadow rounded-lg p-4 sm:p-5">
            <dt class="text-sm font-medium text-gray-500 truncate">{{ $t('common.status') }}</dt>
            <dd class="mt-2">
              <span v-if="isPaid" class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                <svg class="h-4 w-4 mr-1.5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
                {{ $t('admin.paid') }}
              </span>
              <span v-else class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
                <svg class="h-4 w-4 mr-1.5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                {{ $t('admin.pending') }}
              </span>
            </dd>
          </div>
        </div>

        <!-- Actions Bar -->
        <div class="flex flex-col sm:flex-row gap-3 mb-6">
          <button
            v-if="!isPaid"
            @click="handleMarkPaid"
            :disabled="processingPay || professorPaymentsForMonth.length === 0"
            class="inline-flex items-center justify-center px-5 py-2.5 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg class="h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
            {{ processingPay ? $t('common.saving') : $t('admin.markAsPaid') }}
          </button>
          <button
            v-if="isPaid"
            @click="handleUnmarkPaid"
            :disabled="processingPay"
            class="inline-flex items-center justify-center px-5 py-2.5 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            <svg class="h-5 w-5 mr-2 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
            {{ $t('admin.undoPayment') }}
          </button>
          <button
            @click="downloadPDF"
            :disabled="professorPaymentsForMonth.length === 0 || generatingPdf"
            class="inline-flex items-center justify-center px-5 py-2.5 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg class="h-5 w-5 mr-2 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            {{ generatingPdf ? $t('admin.generatingPdf') : $t('admin.downloadPdf') }}
          </button>
        </div>

        <!-- Payments Table -->
        <div class="bg-white shadow rounded-lg overflow-hidden">
          <div class="px-4 py-4 sm:px-6 sm:py-5 border-b border-gray-200">
            <h3 class="text-base sm:text-lg font-medium text-gray-900">{{ $t('admin.commissionDetails') }}</h3>
            <p class="mt-1 text-sm text-gray-500">{{ $t('admin.commissionDetailsDesc') }}</p>
          </div>

          <div v-if="professorPaymentsForMonth.length === 0" class="text-center py-8">
            <svg class="mx-auto h-10 w-10 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
            <p class="mt-2 text-sm text-gray-500">{{ $t('admin.noCommissionsFound') }}</p>
          </div>

          <!-- Mobile Cards -->
          <div class="sm:hidden divide-y divide-gray-200">
            <div v-for="payment in professorPaymentsForMonth" :key="payment.id" class="p-4">
              <div class="flex justify-between items-start">
                <div>
                  <p class="text-sm font-medium text-gray-900">{{ payment.studentName }}</p>
                  <p class="text-xs text-gray-500 mt-1">{{ formatDate(payment.paymentDate) }}</p>
                </div>
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-md text-sm font-medium bg-indigo-50 text-indigo-700">
                  {{ currency }} {{ formatCurrency(payment.amount) }}
                </span>
              </div>
              <div class="mt-2 flex items-center text-xs text-gray-500">
                <span>{{ $t('admin.studentPaymentLabel') }}: {{ currency }} {{ formatCurrency(payment.originalStudentPayment || 0) }}</span>
                <span class="mx-2">&middot;</span>
                <span>{{ payment.commissionPercent }}%</span>
              </div>
            </div>
          </div>

          <!-- Desktop Table -->
          <table class="hidden sm:table min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">{{ $t('professor.student') }}</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">{{ $t('admin.studentPaymentLabel') }}</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">{{ $t('professor.commission') }}</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">{{ $t('admin.commissionValue') }}</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">{{ $t('common.date') }}</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="payment in professorPaymentsForMonth" :key="payment.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ payment.studentName }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right">{{ currency }} {{ formatCurrency(payment.originalStudentPayment || 0) }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right">{{ payment.commissionPercent }}%</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-indigo-600 text-right">{{ currency }} {{ formatCurrency(payment.amount) }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right">{{ formatDate(payment.paymentDate) }}</td>
              </tr>
            </tbody>
            <tfoot class="bg-gray-50">
              <tr>
                <td class="px-6 py-4 text-sm font-bold text-gray-900" colspan="3">{{ $t('professor.total') }}</td>
                <td class="px-6 py-4 text-sm font-bold text-indigo-700 text-right">{{ currency }} {{ formatCurrency(totalCommission) }}</td>
                <td></td>
              </tr>
            </tfoot>
          </table>

          <!-- Mobile Total -->
          <div class="sm:hidden border-t border-gray-200 px-4 py-3 bg-gray-50 flex justify-between items-center">
            <span class="text-sm font-bold text-gray-900">{{ $t('professor.total') }}</span>
            <span class="text-sm font-bold text-indigo-700">{{ currency }} {{ formatCurrency(totalCommission) }}</span>
          </div>
        </div>

        <!-- All Professors Overview -->
        <div class="mt-8 bg-white shadow rounded-lg overflow-hidden">
          <div class="px-4 py-4 sm:px-6 sm:py-5 border-b border-gray-200">
            <h3 class="text-base sm:text-lg font-medium text-gray-900">{{ $t('admin.allProfessorsOverview') }}</h3>
            <p class="mt-1 text-sm text-gray-500">{{ $t('admin.allProfessorsOverviewDesc') }}</p>
          </div>

          <!-- Mobile Cards -->
          <div class="sm:hidden divide-y divide-gray-200">
            <div v-for="overview in professorOverview" :key="overview.professorId" class="p-4">
              <div class="flex justify-between items-start">
                <div>
                  <p class="text-sm font-medium text-gray-900">{{ overview.name }}</p>
                  <p class="text-xs text-gray-500 mt-1">{{ overview.count }} {{ overview.count === 1 ? $t('professor.student').toLowerCase() : $t('professor.students').toLowerCase() }}</p>
                </div>
                <div class="flex items-center gap-2">
                  <span class="text-sm font-medium text-gray-900">{{ currency }} {{ formatCurrency(overview.total) }}</span>
                  <span v-if="overview.isPaid" class="h-5 w-5 text-green-500">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                  </span>
                  <span v-else class="h-5 w-5 text-yellow-500">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Desktop Table -->
          <table class="hidden sm:table min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">{{ $t('admin.professor') }}</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">{{ $t('admin.studentsWithCommission') }}</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">{{ $t('admin.totalCommissions') }}</th>
                <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">{{ $t('common.status') }}</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr
                v-for="overview in professorOverview"
                :key="overview.professorId"
                class="hover:bg-gray-50 cursor-pointer"
                @click="selectedProfessorId = overview.professorId"
                :class="selectedProfessorId === overview.professorId ? 'bg-indigo-50' : ''"
              >
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ overview.name }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right">{{ overview.count }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 text-right">{{ currency }} {{ formatCurrency(overview.total) }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-center">
                  <span v-if="overview.isPaid" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                    <svg class="h-3.5 w-3.5 mr-1" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>
                    {{ $t('admin.paid') }}
                  </span>
                  <span v-else class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                    <svg class="h-3.5 w-3.5 mr-1" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                    {{ $t('admin.pending') }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>

          <div v-if="professorOverview.length === 0" class="text-center py-8">
            <p class="text-sm text-gray-500">{{ $t('admin.noCommissionsFound') }}</p>
          </div>
        </div>
      </template>

      <!-- Confirmation Modal -->
      <Teleport to="body">
        <Transition name="fade">
          <div v-if="showConfirmModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
            <div class="fixed inset-0 bg-black bg-opacity-40" @click="showConfirmModal = false"></div>
            <div class="relative bg-white rounded-lg shadow-xl max-w-sm w-full p-6">
              <h3 class="text-lg font-medium text-gray-900 mb-2">{{ confirmTitle }}</h3>
              <p class="text-sm text-gray-500 mb-5">{{ confirmMessage }}</p>
              <div class="flex justify-end gap-3">
                <button @click="showConfirmModal = false" class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
                  {{ $t('common.cancel') }}
                </button>
                <button @click="confirmAction" :class="confirmButtonClass" class="px-4 py-2 text-sm font-medium rounded-md text-white">
                  {{ $t('common.confirm') }}
                </button>
              </div>
            </div>
          </div>
        </Transition>
      </Teleport>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';
import { usePaymentsStore } from '../../stores/payments';
import Breadcrumb from '@/components/Breadcrumb.vue';
import PaymentNav from '@/components/admin/PaymentNav.vue';
import { useCompanyCurrency } from '@/composables/useCompanyCurrency';

const { t } = useI18n();
const { currency, formatCurrency, currencyLocale } = useCompanyCurrency();

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const paymentsStore = usePaymentsStore();

const loading = ref(false);
const loadingProfessors = ref(true);
const processingPay = ref(false);
const generatingPdf = ref(false);
const professors = ref([]);
const students = ref([]);
const allProfessorPayments = ref([]);
const selectedProfessorId = ref('');
const isMonthSelectorOpen = ref(false);
const selectedMonth = ref(new Date().getMonth());
const selectedYear = ref(new Date().getFullYear());

const showConfirmModal = ref(false);
const confirmTitle = ref('');
const confirmMessage = ref('');
const confirmButtonClass = ref('bg-green-600 hover:bg-green-700');
let pendingConfirmAction = null;

const months = computed(() => [
  t('common.months.january'), t('common.months.february'), t('common.months.march'),
  t('common.months.april'), t('common.months.may'), t('common.months.june'),
  t('common.months.july'), t('common.months.august'), t('common.months.september'),
  t('common.months.october'), t('common.months.november'), t('common.months.december')
]);

const currentMonthYear = computed(() => `${months.value[selectedMonth.value]} ${selectedYear.value}`);

const breadcrumbItems = computed(() => {
  const path = route.path;
  const segments = path.split('/').filter(Boolean);
  return segments.map((segment, index) => {
    let segPath = '/' + segments.slice(0, index + 1).join('/');
    let name = segment.charAt(0).toUpperCase() + segment.slice(1);
    if (segment === 'payments') { name = t('admin.payments'); segPath = '/admin'; }
    else if (segment === 'professors') { name = t('admin.professorPaymentsTitle'); }
    return { name, path: segPath };
  });
});

const professorPaymentsForMonth = computed(() => {
  if (!selectedProfessorId.value) return [];
  return allProfessorPayments.value.filter(p => {
    if (p.professorId !== selectedProfessorId.value) return false;
    const d = new Date(p.paymentDate);
    return d.getMonth() === selectedMonth.value && d.getFullYear() === selectedYear.value;
  });
});

const totalCommission = computed(() => {
  return professorPaymentsForMonth.value.reduce((sum, p) => sum + (p.amount || 0), 0);
});

const currentPayout = computed(() => {
  return paymentsStore.professorPayouts.find(
    p => p.professorId === selectedProfessorId.value
  );
});

const isPaid = computed(() => !!currentPayout.value);

const professorOverview = computed(() => {
  const map = {};
  allProfessorPayments.value.forEach(p => {
    const d = new Date(p.paymentDate);
    if (d.getMonth() !== selectedMonth.value || d.getFullYear() !== selectedYear.value) return;
    if (!map[p.professorId]) {
      const prof = professors.value.find(pr => pr.id === p.professorId);
      map[p.professorId] = {
        professorId: p.professorId,
        name: prof ? (prof.name || `${prof.firstName || ''} ${prof.lastName || ''}`.trim()) : p.professorId.substring(0, 8),
        total: 0,
        count: 0,
        isPaid: !!paymentsStore.professorPayouts.find(po => po.professorId === p.professorId)
      };
    }
    map[p.professorId].total += p.amount || 0;
    map[p.professorId].count++;
  });
  return Object.values(map).sort((a, b) => a.name.localeCompare(b.name));
});

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat(currencyLocale.value, { day: '2-digit', month: '2-digit', year: 'numeric' }).format(date);
};

const toggleMonthSelector = () => { isMonthSelectorOpen.value = !isMonthSelectorOpen.value; };

const handleClickOutside = (event) => {
  if (!event.target.closest('#monthSelector') && isMonthSelectorOpen.value) {
    isMonthSelectorOpen.value = false;
  }
};

const selectMonth = (monthIndex) => {
  selectedMonth.value = monthIndex;
  isMonthSelectorOpen.value = false;
  fetchData();
};

const handleMarkPaid = () => {
  const prof = professors.value.find(p => p.id === selectedProfessorId.value);
  const profName = prof ? (prof.name || `${prof.firstName || ''} ${prof.lastName || ''}`.trim()) : '';
  confirmTitle.value = t('admin.confirmPayment');
  confirmMessage.value = t('admin.confirmPaymentMessage', {
    professor: profName,
    amount: `${currency.value} ${formatCurrency(totalCommission.value)}`,
    month: currentMonthYear.value
  });
  confirmButtonClass.value = 'bg-green-600 hover:bg-green-700';
  pendingConfirmAction = doMarkPaid;
  showConfirmModal.value = true;
};

const doMarkPaid = async () => {
  processingPay.value = true;
  try {
    await paymentsStore.markProfessorPaid(
      selectedProfessorId.value,
      selectedMonth.value,
      selectedYear.value,
      totalCommission.value
    );
  } catch (error) {
    console.error('Error marking professor as paid:', error);
  } finally {
    processingPay.value = false;
  }
};

const handleUnmarkPaid = () => {
  confirmTitle.value = t('admin.undoPaymentTitle');
  confirmMessage.value = t('admin.undoPaymentMessage');
  confirmButtonClass.value = 'bg-red-600 hover:bg-red-700';
  pendingConfirmAction = doUnmarkPaid;
  showConfirmModal.value = true;
};

const doUnmarkPaid = async () => {
  processingPay.value = true;
  try {
    if (currentPayout.value) {
      await paymentsStore.unmarkProfessorPaid(currentPayout.value.id);
    }
  } catch (error) {
    console.error('Error undoing professor payment:', error);
  } finally {
    processingPay.value = false;
  }
};

const confirmAction = () => {
  showConfirmModal.value = false;
  if (pendingConfirmAction) {
    pendingConfirmAction();
    pendingConfirmAction = null;
  }
};

const downloadPDF = async () => {
  generatingPdf.value = true;
  try {
    const { jsPDF } = await import('jspdf');
    const doc = new jsPDF();
    const prof = professors.value.find(p => p.id === selectedProfessorId.value);
    const profName = prof ? (prof.name || `${prof.firstName || ''} ${prof.lastName || ''}`.trim()) : '';

    doc.setFontSize(18);
    doc.text(t('admin.professorPaymentsTitle'), 14, 22);

    doc.setFontSize(12);
    doc.setTextColor(100);
    doc.text(`${t('admin.professor')}: ${profName}`, 14, 32);
    doc.text(`${t('admin.period')}: ${currentMonthYear.value}`, 14, 39);
    doc.text(`${t('common.status')}: ${isPaid.value ? t('admin.paid') : t('admin.pending')}`, 14, 46);

    let y = 58;
    doc.setTextColor(0);
    doc.setFontSize(10);

    doc.setFillColor(240, 240, 240);
    doc.rect(14, y - 5, 182, 8, 'F');
    doc.setFont(undefined, 'bold');
    doc.text(t('professor.student'), 16, y);
    doc.text(t('admin.studentPaymentLabel'), 90, y, { align: 'left' });
    doc.text(t('professor.commission'), 135, y, { align: 'left' });
    doc.text(t('admin.commissionValue'), 190, y, { align: 'right' });
    doc.setFont(undefined, 'normal');
    y += 8;

    professorPaymentsForMonth.value.forEach(payment => {
      if (y > 270) { doc.addPage(); y = 20; }
      doc.text(payment.studentName || '-', 16, y);
      doc.text(`${currency.value} ${formatCurrency(payment.originalStudentPayment || 0)}`, 90, y, { align: 'left' });
      doc.text(`${payment.commissionPercent}%`, 135, y, { align: 'left' });
      doc.text(`${currency.value} ${formatCurrency(payment.amount)}`, 190, y, { align: 'right' });
      y += 7;
    });

    y += 3;
    doc.setDrawColor(200);
    doc.line(14, y, 196, y);
    y += 7;
    doc.setFont(undefined, 'bold');
    doc.setFontSize(12);
    doc.text(t('professor.total'), 16, y);
    doc.text(`${currency.value} ${formatCurrency(totalCommission.value)}`, 190, y, { align: 'right' });

    const fileName = `${profName.replace(/\s+/g, '_')}_${months.value[selectedMonth.value]}_${selectedYear.value}.pdf`;
    doc.save(fileName);
  } catch (error) {
    console.error('Error generating PDF:', error);
  } finally {
    generatingPdf.value = false;
  }
};

const fetchData = async () => {
  loading.value = true;
  try {
    const [profPayments] = await Promise.all([
      paymentsStore.fetchProfessorPayments(),
      paymentsStore.fetchProfessorPayouts(selectedMonth.value, selectedYear.value)
    ]);

    const studentsMap = {};
    students.value.forEach(s => {
      studentsMap[s.id] = s.name || `${s.firstName || ''} ${s.lastName || ''}`.trim() || s.id.substring(0, 8);
    });

    allProfessorPayments.value = profPayments.map(p => ({
      ...p,
      studentName: studentsMap[p.studentId] || p.studentId?.substring(0, 8) || '-'
    }));
  } catch (error) {
    console.error('Error fetching data:', error);
  } finally {
    loading.value = false;
  }
};

watch(selectedProfessorId, () => {
  // re-fetch payouts isn't needed since they're for the whole month, already loaded
});

onMounted(async () => {
  if (!authStore.isAdmin) { router.push('/login'); return; }

  loadingProfessors.value = true;
  try {
    const [profs, studs] = await Promise.all([
      authStore.getUsersByCompany('professor'),
      authStore.getUsersByCompany('student')
    ]);
    professors.value = profs.filter(p => p.isActive !== false);
    students.value = studs;
  } catch (error) {
    console.error('Error loading professors:', error);
  } finally {
    loadingProfessors.value = false;
  }

  await fetchData();
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
