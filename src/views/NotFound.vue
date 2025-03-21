<template>
  <div class="flex flex-col items-center justify-center min-h-screen px-4 text-gray-800">
    <div class="max-w-md text-center">
      <h1 class="text-6xl font-bold mb-8 text-blue-500">404</h1>
      <h2 class="text-3xl font-semibold mb-4">Página não encontrada</h2>
      <p class="text-lg mb-8">
        Desculpe, a página que você está procurando não existe ou foi movida.
      </p>
      
      <div v-if="isAuthenticated" class="mb-2">
        <button 
          @click="redirectToDashboard" 
          class="bg-blue-500 hover:bg-blue-600 text-white py-2 px-6 rounded-lg transition-colors">
          Voltar ao Painel
        </button>
      </div>
      <div v-else class="mb-2">
        <button 
          @click="goToHome" 
          class="bg-blue-500 hover:bg-blue-600 text-white py-2 px-6 rounded-lg transition-colors">
          Voltar à Página Inicial
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'NotFound',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    const authInitialized = ref(false);
    
    // Initialize auth store if needed
    onMounted(async () => {
      if (!authStore._initialized) {
        await authStore.init();
      }
      authInitialized.value = true;
    });
    
    // Use computed property to reactively check authentication status
    const isAuthenticated = computed(() => {
      return authStore.isAuthenticated;
    });

    const redirectToDashboard = () => {
      if (authStore.isAdmin) {
        router.push({ name: 'AdminDashboard' });
      } else if (authStore.isProfessor) {
        router.push({ name: 'ProfessorDashboard' });
      } else if (authStore.isStudent) {
        router.push({ name: 'StudentDashboard' });
      } else {
        router.push({ name: 'Home' });
      }
    };

    const goToHome = () => {
      router.push({ name: 'Home' });
    };

    return {
      isAuthenticated,
      redirectToDashboard,
      goToHome,
      authInitialized
    };
  }
};
</script> 