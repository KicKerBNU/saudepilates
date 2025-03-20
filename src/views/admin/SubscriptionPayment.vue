<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { useSubscriptionStore } from '../../stores/subscription';
import SeoHead from '../../components/SeoHead.vue';

const router = useRouter();
const authStore = useAuthStore();
const subscriptionStore = useSubscriptionStore();

const loading = ref(false);
const error = ref(null);
const success = ref(false);
const selectedPlan = ref('monthly');

const plans = [
  {
    id: 'monthly',
    name: 'Mensal',
    price: 129.90,
    duration: 1,
    description: 'Acesso a todas as funcionalidades com pagamento mensal.',
    features: [
      'Acesso a todas as funcionalidades',
      'Até 10 professores',
      'Alunos ilimitados',
      'Suporte prioritário'
    ]
  },
  {
    id: 'quarterly',
    name: 'Trimestral',
    price: 349.90,
    duration: 3,
    description: 'Economize pagando trimestralmente.',
    features: [
      'Acesso a todas as funcionalidades',
      'Até 20 professores',
      'Alunos ilimitados',
      'Suporte prioritário',
      'Economia de 10%'
    ]
  },
  {
    id: 'annual',
    name: 'Anual',
    price: 1299.90,
    duration: 12,
    description: 'Melhor valor! Economize pagando anualmente.',
    features: [
      'Acesso a todas as funcionalidades',
      'Professores ilimitados',
      'Alunos ilimitados',
      'Suporte VIP',
      'Economia de 20%'
    ]
  }
];

const selectedPlanDetails = computed(() => {
  return plans.find(plan => plan.id === selectedPlan.value) || plans[0];
});

const subscription = ref({
  isSubscribed: false,
  expirationDate: null,
  plan: null
});

const formattedExpirationDate = computed(() => {
  if (!subscription.value.expirationDate) return 'N/A';
  const date = new Date(subscription.value.expirationDate);
  return new Intl.DateTimeFormat('pt-BR', { 
    day: 'numeric', 
    month: 'long', 
    year: 'numeric' 
  }).format(date);
});

const daysUntilExpiration = computed(() => {
  return subscriptionStore.daysUntilExpiration;
});

const subscriptionStatus = computed(() => {
  if (!subscription.value.isSubscribed) return 'Inativa';
  if (!subscriptionStore.isValid) return 'Expirada';
  return 'Ativa';
});

const statusColor = computed(() => {
  if (!subscription.value.isSubscribed) return 'bg-red-100 text-red-800';
  if (!subscriptionStore.isValid) return 'bg-orange-100 text-orange-800';
  if (daysUntilExpiration.value < 7) return 'bg-yellow-100 text-yellow-800';
  return 'bg-green-100 text-green-800';
});

onMounted(async () => {
  loading.value = true;
  try {
    const result = await subscriptionStore.fetchSubscription();
    subscription.value = result;
  } catch (err) {
    error.value = `Erro ao carregar informações da assinatura: ${err.message}`;
  } finally {
    loading.value = false;
  }
});

async function renewSubscription() {
  loading.value = true;
  error.value = null;
  success.value = false;
  
  try {
    const duration = selectedPlanDetails.value.duration;
    await subscriptionStore.renewSubscription(duration);
    subscription.value = {
      isSubscribed: subscriptionStore.isSubscribed,
      expirationDate: subscriptionStore.expirationDate,
      plan: subscriptionStore.subscriptionPlan
    };
    success.value = true;
    
    // Simulate a payment processing delay
    setTimeout(() => {
      router.push({ 
        name: 'AdminDashboard', 
        query: { 
          subscriptionRenewed: 'true',
          plan: selectedPlanDetails.value.id
        } 
      });
    }, 2000);
  } catch (err) {
    error.value = `Erro ao renovar assinatura: ${err.message}`;
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <SeoHead
    title="Renovar Assinatura - SaúdePilates"
    description="Renove sua assinatura do SaúdePilates para continuar gerenciando seu estúdio de forma eficiente."
    keywords="assinatura pilates, renovar assinatura, plano mensal pilates, plano anual pilates"
    canonicalPath="/admin/subscription"
  />
  
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
    <div class="text-center">
      <h1 class="text-3xl font-extrabold text-gray-900 sm:text-4xl">
        Assinatura SaúdePilates
      </h1>
      <p class="mt-3 max-w-2xl mx-auto text-xl text-gray-500 sm:mt-4">
        Continue aproveitando todos os benefícios da nossa plataforma para seu estúdio.
      </p>
    </div>
    
    <div class="mt-10 max-w-3xl mx-auto bg-white rounded-lg shadow overflow-hidden">
      <div class="px-6 py-8">
        <h2 class="text-lg font-medium text-gray-900">Status da sua assinatura</h2>
        
        <div v-if="loading" class="mt-4 flex justify-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500"></div>
        </div>
        
        <div v-else class="mt-6 grid grid-cols-1 gap-6 sm:grid-cols-2">
          <div class="rounded-md bg-gray-50 p-6 border border-gray-200">
            <div class="flex items-center">
              <div :class="statusColor" class="flex-shrink-0 rounded-full p-1">
                <div class="h-4 w-4 rounded-full"></div>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-gray-900">Status</h3>
                <p class="mt-1 text-sm text-gray-500">{{ subscriptionStatus }}</p>
              </div>
            </div>
          </div>
          
          <div class="rounded-md bg-gray-50 p-6 border border-gray-200">
            <div class="flex items-center">
              <div class="flex-shrink-0 bg-gray-100 rounded-full p-1">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-gray-900">Expira em</h3>
                <p class="mt-1 text-sm text-gray-500">{{ formattedExpirationDate }}</p>
                <p v-if="subscriptionStore.isValid" class="mt-1 text-xs text-indigo-600">
                  ({{ daysUntilExpiration }} dias restantes)
                </p>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="error" class="mt-4 bg-red-50 text-red-700 p-4 rounded-md">
          {{ error }}
        </div>
        
        <div v-if="success" class="mt-4 bg-green-50 text-green-700 p-4 rounded-md">
          Assinatura renovada com sucesso! Redirecionando...
        </div>
      </div>
    </div>
    
    <div class="mt-10">
      <h2 class="text-2xl font-bold text-gray-900 text-center">
        Escolha seu plano
      </h2>
      
      <div class="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-3">
        <div 
          v-for="plan in plans" 
          :key="plan.id"
          :class="[
            'border rounded-lg shadow-sm p-6 bg-white',
            selectedPlan === plan.id ? 'border-indigo-500 ring-2 ring-indigo-500' : 'border-gray-300'
          ]"
          @click="selectedPlan = plan.id"
        >
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-medium text-gray-900">{{ plan.name }}</h3>
            <div 
              :class="[
                'h-5 w-5 rounded-full border-2',
                selectedPlan === plan.id ? 'border-indigo-500 bg-indigo-500' : 'border-gray-300'
              ]"
            ></div>
          </div>
          
          <p class="mt-4 text-sm text-gray-500">{{ plan.description }}</p>
          
          <p class="mt-8">
            <span class="text-4xl font-extrabold text-gray-900">R$ {{ plan.price.toFixed(2) }}</span>
            <span class="text-base font-medium text-gray-500">/{{ plan.id === 'annual' ? 'ano' : plan.id === 'quarterly' ? 'trimestre' : 'mês' }}</span>
          </p>
          
          <ul class="mt-6 space-y-4">
            <li v-for="(feature, index) in plan.features" :key="index" class="flex">
              <svg class="flex-shrink-0 h-5 w-5 text-green-500" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
              </svg>
              <span class="ml-3 text-sm text-gray-500">{{ feature }}</span>
            </li>
          </ul>
        </div>
      </div>
      
      <div class="mt-10 flex justify-center">
        <button
          @click="renewSubscription"
          :disabled="loading"
          class="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <span v-if="loading" class="mr-2">
            <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
          </span>
          {{ loading ? 'Processando...' : 'Renovar Assinatura' }}
        </button>
      </div>
    </div>
  </div>
</template> 