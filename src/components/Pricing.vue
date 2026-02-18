<!-- Pricing.vue -->
<template>
  <MetaTags
    title="SaúdePilates - Planos e Preços | Software de Gestão para Pilates"
    description="Conheça nossos planos e preços acessíveis para gestão de estúdios de Pilates. Escolha o plano ideal para o seu negócio e comece a otimizar já a sua gestão."
    keywords="preços pilates, planos pilates, software pilates preço, gestão pilates custo, assinatura pilates"
  />
  <div class="min-h-screen bg-gray-50 py-12">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center">
        <h2 class="text-3xl font-extrabold text-gray-900 sm:text-4xl">
          {{ $t('pricing.simplePrices') }}
        </h2>
        <p class="mt-4 text-lg text-gray-600">
          {{ $t('pricing.choosePlan') }}
        </p>
      </div>

      <div class="mt-16 grid grid-cols-1 gap-8 lg:grid-cols-3">
        <!-- Plano Mensal -->
        <div class="relative w-full">
          <div class="rounded-lg shadow-lg overflow-hidden transform hover:scale-105 transition-transform duration-300">
            <div class="px-6 py-8 bg-white sm:p-10 sm:pb-6">
              <div class="flex justify-center">
                <span class="inline-flex px-4 py-1 rounded-full text-sm font-semibold tracking-wide uppercase bg-indigo-100 text-indigo-600">
                  {{ $t('pricing.monthlyPlan') }}
                </span>
              </div>
              <div class="mt-4 flex flex-wrap items-baseline justify-center gap-x-1 text-gray-900">
                <span class="text-3xl font-extrabold sm:text-4xl lg:text-5xl">{{ formatCurrency(49.99) }}</span>
                <span class="text-base font-medium text-gray-500 sm:text-lg">/mês</span>
              </div>
              <p class="mt-5 text-lg text-gray-500 text-center">
                {{ $t('pricing.accessAllFeatures') }}
              </p>
            </div>
            <div class="px-6 pt-6 pb-8 bg-gray-50 sm:p-10 sm:pt-6">
              <ul class="space-y-4">
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.accessAllFeatures') }}</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.upTo10Teachers') }}</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.unlimitedStudents') }}</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.prioritySupport') }}</p>
                </li>
              </ul>
              <div class="mt-6 rounded-md shadow">
                <a href="#" @click="handlePlanSelect('mensal')" class="flex items-center justify-center px-5 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                  {{ $t('pricing.subscribeNow') }}
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- Plano Gratuito -->
        <div class="relative w-full">
          <div class="rounded-lg shadow-lg overflow-hidden transform hover:scale-105 transition-transform duration-300 border-2 border-indigo-500">
            <div class="px-6 py-8 bg-white sm:p-10 sm:pb-6">
              <div class="flex justify-center">
                <span class="inline-flex px-4 py-1 rounded-full text-sm font-semibold tracking-wide uppercase bg-indigo-100 text-indigo-600">
                  {{ $t('pricing.freePlan') }}
                </span>
              </div>
              <div class="mt-4 flex flex-wrap items-baseline justify-center gap-x-1 text-gray-900">
                <span class="text-3xl font-extrabold sm:text-4xl lg:text-5xl">{{ formatCurrency(0) }}</span>
                <span class="text-base font-medium text-gray-500 sm:text-lg">/mês</span>
              </div>
              <p class="mt-5 text-lg text-gray-500 text-center">
                {{ $t('pricing.freePlanDescription') }}
              </p>
            </div>
            <div class="px-6 pt-6 pb-8 bg-gray-50 sm:p-10 sm:pt-6">
              <ul class="space-y-4">
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.unlimitedStudents') }}</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.accessAllFeatures') }}</p>
                </li>
              </ul>
              <div class="mt-6 rounded-md shadow">
                <button
                  type="button"
                  @click="goToRegister"
                  class="flex items-center justify-center w-full px-5 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
                >
                  {{ $t('pricing.startTrialPeriod') }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Plano Anual -->
        <div class="relative w-full">
          <div class="rounded-lg shadow-lg overflow-hidden transform hover:scale-105 transition-transform duration-300">
            <div class="px-6 py-8 bg-white sm:p-10 sm:pb-6">
              <div class="flex justify-center">
                <span class="inline-flex px-4 py-1 rounded-full text-sm font-semibold tracking-wide uppercase bg-indigo-100 text-indigo-600">
                  {{ $t('pricing.annualPlan') }}
                </span>
              </div>
              <div class="mt-4 flex flex-wrap items-baseline justify-center gap-x-1 text-gray-900">
                <span class="text-3xl font-extrabold sm:text-4xl lg:text-5xl">{{ formatCurrency(499) }}</span>
                <span class="text-base font-medium text-gray-500 sm:text-lg">/ano</span>
              </div>
              <p class="mt-5 text-lg text-gray-500 text-center">
                {{ $t('pricing.bestValue') }}
              </p>
            </div>
            <div class="px-6 pt-6 pb-8 bg-gray-50 sm:p-10 sm:pt-6">
              <ul class="space-y-4">
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.accessAllFeatures') }}</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.unlimitedTeachers') }}</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.unlimitedStudents') }}</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.vipSupport') }}</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">{{ $t('pricing.save20') }}</p>
                </li>
              </ul>
              <div class="mt-6 rounded-md shadow">
                <a href="#" @click="handlePlanSelect('anual')" class="flex items-center justify-center px-5 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                  {{ $t('pricing.subscribeNow') }}
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-16 text-center">
        <p class="text-lg text-gray-600">
          {{ $t('pricing.notSure') }}
        </p>
        <div class="mt-6">
          <a href="/register" class="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700">
            {{ $t('pricing.startTrialPeriod') }}
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n';
import MetaTags from '@/components/MetaTags.vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { computed, onMounted, ref, watch } from 'vue';
import { getStripeUrl, STRIPE_PLANS } from '@/utils/stripeConfig';

const { t, locale } = useI18n();

const authStore = useAuthStore();
const router = useRouter();

const brlToUsdRate = ref(0.2);

const currencyConfig = computed(() => {
  if (locale.value && locale.value.toLowerCase().startsWith('en')) {
    return {
      currency: 'USD',
      rate: brlToUsdRate.value,
    };
  }

  return {
    currency: 'BRL',
    rate: 1,
  };
});

const currencyFormatter = computed(
  () =>
    new Intl.NumberFormat(locale.value, {
      style: 'currency',
      currency: currencyConfig.value.currency,
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })
);

const formatCurrency = (valueInBRL) =>
  currencyFormatter.value.format(valueInBRL * currencyConfig.value.rate);

const fetchExchangeRate = async () => {
  if (!locale.value || !locale.value.toLowerCase().startsWith('en')) return;

  try {
    const response = await fetch(
      'https://api.frankfurter.dev/v1/latest?base=BRL&symbols=USD'
    );
    if (!response.ok) return;

    const data = await response.json();
    const rate = data && data.rates && data.rates.USD;

    if (typeof rate === 'number' && rate > 0) {
      brlToUsdRate.value = rate;
    }
  } catch (error) {
    
  }
};

watch(
  () => locale.value,
  (newLocale) => {
    if (newLocale && newLocale.toLowerCase().startsWith('en')) {
      fetchExchangeRate();
    }
  },
  { immediate: true }
);

// Debug variables
const showDebug = ref(true); // Set to false for production

const isAuthenticated = computed(() => authStore.isAuthenticated);
const userEmail = computed(() => authStore.user?.email || '');

const goToRegister = () => {
  router.push('/register');
};

// Force auth initialization
const forceInit = async () => {
  try {
    
    await authStore.init();
    
  } catch (err) {
    console.error('Error during force init:', err);
  }
};

// Debug authentication state
onMounted(() => {
  // Check if authStore is properly initialized
  if (!authStore._initialized) {
    forceInit();
  }
});

const handlePlanSelect = (planType) => {
  
  if (!isAuthenticated.value) {
    // Redirect to login page if not authenticated
    router.push('/login');
    return;
  }

  // Get the appropriate URL for the current environment
  const stripeUrl = getStripeUrl(planType);
  if (!stripeUrl) {
    console.error('No URL found for plan:', planType);
    return;
  }

  // Append user email to the URL
  const email = encodeURIComponent(userEmail.value);
  const paymentUrl = `${stripeUrl}${email ? `?prefilled_email=${email}` : ''}`;
  
  
  // Open Stripe payment page in a new tab/window
  window.open(paymentUrl, '_blank');
};
</script>
