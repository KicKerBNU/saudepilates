<template>
  <div class="flex flex-col items-center justify-center min-h-screen px-4 text-gray-800">
    <div class="max-w-md text-center">
      <h1 class="text-6xl font-bold mb-8 text-blue-500">{{ $t('notFound.title') }}</h1>
      <h2 class="text-3xl font-semibold mb-4">{{ $t('notFound.pageNotFound') }}</h2>
      <p class="text-lg mb-8">
        {{ $t('notFound.description') }}
      </p>
      
      <div v-if="isAuthenticated" class="mb-2">
        <button 
          @click="redirectToDashboard" 
          class="bg-blue-500 hover:bg-blue-600 text-white py-2 px-6 rounded-lg transition-colors">
          {{ $t('notFound.backToDashboard') }}
        </button>
      </div>
      <div v-else class="mb-2">
        <button 
          @click="goToHome" 
          class="bg-blue-500 hover:bg-blue-600 text-white py-2 px-6 rounded-lg transition-colors">
          {{ $t('notFound.backToHome') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth';
import { useI18n } from 'vue-i18n';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'NotFound',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    const { t } = useI18n();
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