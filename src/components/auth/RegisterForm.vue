<template>
  <div class="max-w-md w-full mx-auto bg-white rounded-lg shadow-md overflow-hidden p-6">
    <h2 class="text-2xl font-bold text-gray-900 text-center mb-6">Cadastro de Administrador</h2>
    <p class="text-center text-gray-600 mb-4">Crie sua conta de administrador e comece a gerenciar sua empresa</p>
    
    <div v-if="error" class="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
      {{ error }}
    </div>
    
    <form @submit.prevent="handleRegister">
      <div class="mb-4">
        <label for="companyName" class="block text-sm font-medium text-gray-700 mb-1">Nome da Empresa</label>
        <input 
          type="text" 
          id="companyName" 
          v-model="companyData.name" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Studio Pilates"
        />
      </div>
      
      <div class="mb-4">
        <label for="name" class="block text-sm font-medium text-gray-700 mb-1">Nome Completo</label>
        <input 
          type="text" 
          id="name" 
          v-model="userData.name" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="João Silva"
        />
      </div>
      
      <div class="mb-4">
        <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
        <input 
          type="email" 
          id="email" 
          v-model="userData.email" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="exemplo@email.com"
        />
      </div>
      
      <div class="mb-4">
        <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">Telefone</label>
        <input 
          type="tel" 
          id="phone" 
          v-model="userData.phone" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="(00) 00000-0000"
        />
      </div>
      

      
      <div class="mb-4">
        <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Senha</label>
        <input 
          type="password" 
          id="password" 
          v-model="userData.password" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" 
          placeholder="******"
          minlength="6"
        />
      </div>
      
      <div class="mb-6">
        <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">Confirmar Senha</label>
        <input 
          type="password" 
          id="confirmPassword" 
          v-model="confirmPassword" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" 
          placeholder="******"
          minlength="6"
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
          Registrando...
        </span>
        <span v-else>Cadastrar</span>
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';

const userData = reactive({
  name: '',
  email: '',
  phone: '',
  password: ''
});

const companyData = reactive({
  name: ''
});

const confirmPassword = ref('');
const error = ref('');
const isLoading = ref(false);

const router = useRouter();
const authStore = useAuthStore();

const handleRegister = async () => {
  // Prevent multiple attempts while loading
  if (isLoading.value) return;
  
  error.value = '';
  isLoading.value = true;
  
  // Validate company name
  if (!companyData.name.trim()) {
    error.value = 'Nome da empresa é obrigatório';
    isLoading.value = false;
    return;
  }
  
  // Validate passwords match
  if (userData.password !== confirmPassword.value) {
    error.value = 'As senhas não coincidem';
    isLoading.value = false;
    return;
  }
  
  try {
    await authStore.register(userData.email, userData.password, companyData, {
      name: userData.name,
      phone: userData.phone
    });
    
    // Redirect to admin dashboard
    router.push('/admin');
  } catch (err) {
    // Set specific error messages based on Firebase error codes
    if (err.code === 'auth/email-already-in-use') {
      error.value = 'Este email já está em uso';
    } else if (err.code === 'auth/weak-password') {
      error.value = 'A senha é muito fraca. Use pelo menos 6 caracteres.';
    } else if (err.code === 'auth/invalid-email') {
      error.value = 'Email inválido. Verifique o formato.';
    } else if (err.code === 'auth/network-request-failed') {
      error.value = 'Falha na conexão. Verifique sua internet.';
    } else {
      error.value = 'Erro ao criar conta: ' + (err.message || 'Tente novamente.');
    }
    console.error('Registration error:', err.code, err.message);
  } finally {
    isLoading.value = false;
  }
};
</script>
