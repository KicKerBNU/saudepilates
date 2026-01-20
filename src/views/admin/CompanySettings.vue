<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-4 px-4 sm:py-6 sm:px-6 lg:px-8">
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-900">{{ $t('admin.companySettings') }}</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-4 sm:py-6 px-4 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="mb-4 sm:mb-6">
        <Breadcrumb :items="breadcrumbItems" />
      </div>
      
      <!-- Company Settings Card -->
      <div class="bg-white shadow overflow-hidden sm:rounded-lg">
        <div class="px-4 py-4 sm:px-6 sm:py-5">
          <h3 class="text-base sm:text-lg leading-6 font-medium text-gray-900">
            {{ $t('admin.companyConfiguration') }}
          </h3>
          <p class="mt-1 max-w-2xl text-sm text-gray-500">
            {{ $t('admin.companyConfigurationDesc') }}
          </p>
        </div>
        
        <div class="border-t border-gray-200 px-4 py-4 sm:px-6 sm:py-5">
          <!-- Language Selection -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-4">
              {{ $t('admin.companyLanguage') }}
            </label>
            <p class="text-sm text-gray-500 mb-4">
              {{ $t('admin.companyLanguageDesc') }}
            </p>
            
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              <!-- English (USA) -->
              <button
                @click="selectLanguage('en')"
                :class="[
                  'relative flex flex-col items-center p-4 border-2 rounded-lg transition-all',
                  companyLanguage === 'en'
                    ? 'border-indigo-500 bg-indigo-50'
                    : 'border-gray-200 hover:border-gray-300 bg-white'
                ]"
              >
                <div class="text-4xl mb-2">🇺🇸</div>
                <div class="font-medium text-gray-900">English</div>
                <div class="text-sm text-gray-500">USD ($)</div>
                <div v-if="companyLanguage === 'en'" class="absolute top-2 right-2">
                  <svg class="h-5 w-5 text-indigo-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                  </svg>
                </div>
              </button>
              
              <!-- Spanish (Spain) -->
              <button
                @click="selectLanguage('es')"
                :class="[
                  'relative flex flex-col items-center p-4 border-2 rounded-lg transition-all',
                  companyLanguage === 'es'
                    ? 'border-indigo-500 bg-indigo-50'
                    : 'border-gray-200 hover:border-gray-300 bg-white'
                ]"
              >
                <div class="text-4xl mb-2">🇪🇸</div>
                <div class="font-medium text-gray-900">Español</div>
                <div class="text-sm text-gray-500">EUR (€)</div>
                <div v-if="companyLanguage === 'es'" class="absolute top-2 right-2">
                  <svg class="h-5 w-5 text-indigo-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                  </svg>
                </div>
              </button>
              
              <!-- French (France) -->
              <button
                @click="selectLanguage('fr')"
                :class="[
                  'relative flex flex-col items-center p-4 border-2 rounded-lg transition-all',
                  companyLanguage === 'fr'
                    ? 'border-indigo-500 bg-indigo-50'
                    : 'border-gray-200 hover:border-gray-300 bg-white'
                ]"
              >
                <div class="text-4xl mb-2">🇫🇷</div>
                <div class="font-medium text-gray-900">Français</div>
                <div class="text-sm text-gray-500">EUR (€)</div>
                <div v-if="companyLanguage === 'fr'" class="absolute top-2 right-2">
                  <svg class="h-5 w-5 text-indigo-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                  </svg>
                </div>
              </button>
              
              <!-- Portuguese (Brazil) -->
              <button
                @click="selectLanguage('pt')"
                :class="[
                  'relative flex flex-col items-center p-4 border-2 rounded-lg transition-all',
                  companyLanguage === 'pt'
                    ? 'border-indigo-500 bg-indigo-50'
                    : 'border-gray-200 hover:border-gray-300 bg-white'
                ]"
              >
                <div class="text-4xl mb-2">🇧🇷</div>
                <div class="font-medium text-gray-900">Português</div>
                <div class="text-sm text-gray-500">BRL (R$)</div>
                <div v-if="companyLanguage === 'pt'" class="absolute top-2 right-2">
                  <svg class="h-5 w-5 text-indigo-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                  </svg>
                </div>
              </button>
            </div>
          </div>
          
          <!-- Save Button -->
          <div class="flex justify-end mt-6">
            <button
              @click="saveSettings"
              :disabled="isSaving || !hasChanges"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <svg 
                v-if="isSaving" 
                class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" 
                xmlns="http://www.w3.org/2000/svg" 
                fill="none" 
                viewBox="0 0 24 24"
              >
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ isSaving ? $t('common.loading') : $t('common.save') }}
            </button>
          </div>
          
          <!-- Success/Error Messages -->
          <div v-if="successMessage" class="mt-4 bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded">
            {{ successMessage }}
          </div>
          <div v-if="errorMessage" class="mt-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {{ errorMessage }}
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';
import Breadcrumb from '@/components/Breadcrumb.vue';

const { t } = useI18n();
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// State
const companyLanguage = ref('pt'); // Default to Portuguese
const originalLanguage = ref('pt');
const isSaving = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

// Breadcrumb
const breadcrumbItems = computed(() => {
  const path = route.path;
  const segments = path.split('/').filter(Boolean);
  
  return segments.map((segment, index) => {
    const path = '/' + segments.slice(0, index + 1).join('/');
    let name = segment.charAt(0).toUpperCase() + segment.slice(1);
    
    if (segment === 'settings') {
      name = t('admin.settings');
    }
    
    return { name, path };
  });
});

// Check if there are changes
const hasChanges = computed(() => {
  return companyLanguage.value !== originalLanguage.value;
});

// Load company settings
const loadCompanySettings = async () => {
  try {
    const companyId = authStore.companyId;
    if (!companyId) {
      errorMessage.value = t('admin.errorLoadingCompanySettings');
      return;
    }
    
    // Fetch company info from store (it should already be loaded)
    const companyInfo = authStore.companyInfo;
    if (companyInfo && companyInfo.language) {
      companyLanguage.value = companyInfo.language;
      originalLanguage.value = companyInfo.language;
    } else {
      // Default to Portuguese if no language is set
      companyLanguage.value = 'pt';
      originalLanguage.value = 'pt';
    }
  } catch (error) {
    console.error('Error loading company settings:', error);
    errorMessage.value = t('admin.errorLoadingCompanySettings');
  }
};

// Select language
const selectLanguage = (lang) => {
  companyLanguage.value = lang;
  errorMessage.value = '';
  successMessage.value = '';
};

// Save settings
const saveSettings = async () => {
  if (!hasChanges.value) return;
  
  isSaving.value = true;
  errorMessage.value = '';
  successMessage.value = '';
  
  try {
    const companyId = authStore.companyId;
    if (!companyId) {
      throw new Error(t('admin.noCompanyId'));
    }
    
    // Update company language in Firestore
    await authStore.updateCompanyLanguage(companyId, companyLanguage.value);
    
    // Update local state
    originalLanguage.value = companyLanguage.value;
    
    successMessage.value = t('admin.settingsSaved');
    
    // Clear success message after 3 seconds
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (error) {
    console.error('Error saving settings:', error);
    errorMessage.value = error.message || t('admin.errorSavingSettings');
  } finally {
    isSaving.value = false;
  }
};

onMounted(async () => {
  if (!authStore.isAdmin) {
    router.push('/login');
    return;
  }
  
  await loadCompanySettings();
});
</script>
