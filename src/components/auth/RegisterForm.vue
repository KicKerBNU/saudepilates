<template>
  <div class="max-w-md w-full mx-auto bg-white rounded-lg shadow-md overflow-hidden p-6">
    <h2 class="text-2xl font-bold text-gray-900 text-center mb-6">{{ $t('auth.registerTitle') }}</h2>
    <p class="text-center text-gray-600 mb-4">{{ $t('auth.registerDescription') }}</p>
    
    <div v-if="error" class="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
      {{ error }}
    </div>
    
    <form @submit.prevent="handleRegister">
      <div class="mb-4">
        <label for="companyName" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('auth.companyName') }}</label>
        <input 
          type="text" 
          id="companyName" 
          v-model="companyData.name" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          :placeholder="$t('auth.companyNamePlaceholder')"
        />
      </div>
      
      <div class="mb-4">
        <label for="name" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('auth.fullName') }}</label>
        <input 
          type="text" 
          id="name" 
          v-model="userData.name" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          :placeholder="$t('auth.fullNamePlaceholder')"
        />
      </div>
      
      <div class="mb-4">
        <label for="email" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('auth.email') }}</label>
        <input 
          type="email" 
          id="email" 
          v-model="userData.email" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          :placeholder="$t('auth.emailPlaceholder')"
        />
      </div>
      
      <div class="mb-4">
        <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('auth.phone') }}</label>
        <input 
          type="tel" 
          id="phone" 
          v-model="userData.phone" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          :placeholder="$t('auth.phonePlaceholder')"
        />
      </div>
      

      
      <div class="mb-4">
        <label for="password" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('auth.password') }}</label>
        <input 
          type="password" 
          id="password" 
          v-model="userData.password" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" 
          :placeholder="$t('auth.passwordPlaceholder')"
          minlength="6"
        />
      </div>
      
      <div class="mb-6">
        <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('auth.confirmPassword') }}</label>
        <input 
          type="password" 
          id="confirmPassword" 
          v-model="confirmPassword" 
          required
          class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" 
          :placeholder="$t('auth.passwordPlaceholder')"
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
          {{ $t('auth.registering') }}
        </span>
        <span v-else>{{ $t('auth.register') }}</span>
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';

const { t } = useI18n();

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
    error.value = t('auth.companyNameRequired');
    isLoading.value = false;
    return;
  }
  
  // Validate passwords match
  if (userData.password !== confirmPassword.value) {
    error.value = t('auth.passwordsDontMatch');
    isLoading.value = false;
    return;
  }
  
  try {
    await authStore.register(userData.email, userData.password, companyData, {
      name: userData.name,
      phone: userData.phone
    });
    
    // Track Google Ads conversion for successful registration
    if (window.gtag) {
      window.gtag('event', 'conversion', {
        'send_to': 'AW-16916200412/aB3dCJHz9_IYEN2JrqQ9',
      });
      console.log('Conversion tracking sent for user registration');
    }
    
    // Redirect to admin dashboard
    router.push('/admin');
  } catch (err) {
    // Set specific error messages based on Firebase error codes
    if (err.code === 'auth/email-already-in-use') {
      error.value = t('auth.emailInUse');
    } else if (err.code === 'auth/weak-password') {
      error.value = t('auth.weakPassword');
    } else if (err.code === 'auth/invalid-email') {
      error.value = t('auth.invalidEmail');
    } else if (err.code === 'auth/network-request-failed') {
      error.value = t('auth.networkError');
    } else {
      error.value = t('auth.registerError', { message: err.message || t('common.tryAgain') });
    }

  } finally {
    isLoading.value = false;
  }
};
</script>
