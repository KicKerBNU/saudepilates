<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">Dashboard do Aluno</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <div class="bg-white shadow overflow-hidden sm:rounded-lg">
          <div class="px-4 py-5 sm:px-6">
            <h2 class="text-lg leading-6 font-medium text-gray-900">Bem-vindo(a), {{ userProfile?.name || 'Aluno' }}</h2>
            <p class="mt-1 max-w-2xl text-sm text-gray-500">Seu portal de acesso ao SaúdePilates</p>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const userProfile = computed(() => authStore.userProfile);

// Check if user is student, if not redirect
onMounted(async () => {
  if (!authStore.isStudent) {
    router.push('/login');
    return;
  }
});

// Logout functionality removed - now handled in NavBar
</script>