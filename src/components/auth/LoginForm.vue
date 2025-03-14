<template>
  <div class="max-w-md w-full mx-auto bg-white rounded-lg shadow-md overflow-hidden p-6">
    <h2 class="text-2xl font-bold text-gray-900 text-center mb-6">Entrar no SaúdePilates</h2>
    
    <div v-if="error" class="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
      {{ error }}
    </div>
    
    <form @submit.prevent="handleLogin">
      <div class="mb-4">
        <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
        <input 
          type="email" 
          id="email" 
          v-model="email" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="exemplo@email.com"
        />
      </div>
      
      <div class="mb-6">
        <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Senha</label>
        <input 
          type="password" 
          id="password" 
          v-model="password" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" 
          placeholder="******"
        />
      </div>
      
      <button 
        type="submit" 
        class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150 ease-in-out"
        :disabled="isLoading"
      >
        <span v-if="isLoading">
          <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Entrando...
        </span>
        <span v-else>Entrar</span>
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';

const email = ref('');
const password = ref('');
const error = ref('');
const isLoading = ref(false);

const router = useRouter();
const authStore = useAuthStore();

const handleLogin = async () => {
  // Prevent multiple attempts while loading
  if (isLoading.value) return;
  
  error.value = '';
  isLoading.value = true;

  try {
    // Login the user
    const user = await authStore.login(email.value, password.value);
    
    
    // Add a small delay to ensure profile is loaded (temporary fix)
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // Redirect based on role - using route names instead of paths
    try {
      if (authStore.isAdmin) {

        await router.push({ name: 'AdminDashboard' });
      } else if (authStore.isProfessor) {

        await router.push({ name: 'ProfessorDashboard' });
      } else if (authStore.isStudent) {

        await router.push({ name: 'StudentDashboard' });
      } else {

        await router.push({ name: 'Home' });
      }
    } catch (navError) {
      error.value = 'Erro de navegação: ' + navError.message;
      // Fallback to direct URL if named route fails
      if (authStore.isAdmin) await router.push('/admin');
      else if (authStore.isProfessor) await router.push('/professor');
      else if (authStore.isStudent) await router.push('/student');
      else await router.push('/');
    }
  } catch (err) {
    // Set specific error messages based on Firebase error codes
    if (err.code === 'auth/invalid-credential') {
      error.value = 'Credenciais inválidas. Por favor, tente novamente.';
    } else if (err.code === 'auth/user-not-found') {
      error.value = 'Usuário não encontrado.';
    } else if (err.code === 'auth/wrong-password') {
      error.value = 'Senha incorreta.';
    } else if (err.code === 'auth/too-many-requests') {
      error.value = 'Muitas tentativas. Tente novamente mais tarde.';
    } else {
      error.value = 'Erro ao fazer login: ' + (err.message || 'Tente novamente.');
    }

  } finally {
    isLoading.value = false;
  }
};
</script>
