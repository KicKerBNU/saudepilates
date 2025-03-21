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
          Preços simples e transparentes
        </h2>
        <p class="mt-4 text-lg text-gray-600">
          Escolha o plano ideal para o seu estúdio e otimize sua gestão
        </p>
      </div>

      <div class="mt-16 grid grid-cols-1 gap-8 lg:grid-cols-3">
        <!-- Plano Mensal -->
        <div class="relative w-full">
          <div class="rounded-lg shadow-lg overflow-hidden transform hover:scale-105 transition-transform duration-300">
            <div class="px-6 py-8 bg-white sm:p-10 sm:pb-6">
              <div class="flex justify-center">
                <span class="inline-flex px-4 py-1 rounded-full text-sm font-semibold tracking-wide uppercase bg-indigo-100 text-indigo-600">
                  Plano Mensal
                </span>
              </div>
              <div class="mt-4 flex justify-center text-6xl font-extrabold text-gray-900">
                <span class="ml-1 mr-3 text-xl font-medium text-gray-500 leading-loose">R$</span>
                129<span class="text-3xl">,90</span>
                <span class="ml-1 text-xl font-medium text-gray-500 leading-loose">/mês</span>
              </div>
              <p class="mt-5 text-lg text-gray-500 text-center">
                Acesso completo a todos os recursos
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
                  <p class="ml-3 text-base text-gray-700">Acesso a todas as funcionalidades</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Até 10 professores</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Alunos ilimitados</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Suporte prioritário</p>
                </li>
              </ul>
              <div class="mt-6 rounded-md shadow">
                <a href="#" @click="handlePlanSelect('mensal')" class="flex items-center justify-center px-5 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                  Assinar Agora
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- Plano Trimestral -->
        <div class="relative w-full">
          <div class="rounded-lg shadow-lg overflow-hidden transform hover:scale-105 transition-transform duration-300 border-2 border-indigo-500">
            <div class="absolute top-0 right-0 px-3 py-1 bg-indigo-500 text-white text-xs font-semibold">
              POPULAR
            </div>
            <div class="px-6 py-8 bg-white sm:p-10 sm:pb-6">
              <div class="flex justify-center">
                <span class="inline-flex px-4 py-1 rounded-full text-sm font-semibold tracking-wide uppercase bg-indigo-100 text-indigo-600">
                  Plano Trimestral
                </span>
              </div>
              <div class="mt-4 flex justify-center text-6xl font-extrabold text-gray-900">
                <span class="ml-1 mr-3 text-xl font-medium text-gray-500 leading-loose">R$</span>
                349<span class="text-3xl">,90</span>
                <span class="ml-1 text-xl font-medium text-gray-500 leading-loose">/trimestre</span>
              </div>
              <p class="mt-5 text-lg text-gray-500 text-center">
                Economize pagando trimestralmente
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
                  <p class="ml-3 text-base text-gray-700">Acesso a todas as funcionalidades</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Até 20 professores</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Alunos ilimitados</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Suporte prioritário</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Economia de 10%</p>
                </li>
              </ul>
              <div class="mt-6 rounded-md shadow">
                <a href="#" @click="handlePlanSelect('trimestral')" class="flex items-center justify-center px-5 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                  Assinar Agora
                </a>
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
                  Plano Anual
                </span>
              </div>
              <div class="mt-4 flex justify-center text-6xl font-extrabold text-gray-900">
                <span class="ml-1 mr-3 text-xl font-medium text-gray-500 leading-loose">R$</span>
                1.299<span class="text-3xl">,90</span>
                <span class="ml-1 text-xl font-medium text-gray-500 leading-loose">/ano</span>
              </div>
              <p class="mt-5 text-lg text-gray-500 text-center">
                Melhor valor! Economize com o plano anual
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
                  <p class="ml-3 text-base text-gray-700">Acesso a todas as funcionalidades</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Professores ilimitados</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Alunos ilimitados</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Suporte VIP</p>
                </li>
                <li class="flex items-start">
                  <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <p class="ml-3 text-base text-gray-700">Economia de 20%</p>
                </li>
              </ul>
              <div class="mt-6 rounded-md shadow">
                <a href="#" @click="handlePlanSelect('anual')" class="flex items-center justify-center px-5 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                  Assinar Agora
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-16 text-center">
        <p class="text-lg text-gray-600">
          Não tem certeza de qual plano escolher? Experimente gratuitamente por 30 dias!
        </p>
        <div class="mt-6">
          <a href="/register" class="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700">
            Iniciar Período de Teste
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import MetaTags from '@/components/MetaTags.vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { computed, onMounted, ref } from 'vue';
import { getStripeUrl, STRIPE_PLANS } from '@/utils/stripeConfig';

const authStore = useAuthStore();
const router = useRouter();

// Debug variables
const showDebug = ref(true); // Set to false for production

const isAuthenticated = computed(() => authStore.isAuthenticated);
const userEmail = computed(() => authStore.user?.email || '');

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
