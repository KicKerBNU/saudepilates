<!-- NavBar.vue -->
<template>
  <nav class="bg-white shadow-lg">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <div class="flex">
          <div class="flex-shrink-0 flex items-center">
            <router-link to="/" class="flex items-center">
              <img class="h-8 w-8" src="@/assets/pilates-icon.svg" alt="Pilates Icon" />
              <span class="ml-2 text-xl font-semibold text-gray-800">SaúdePilates</span>
            </router-link>
          </div>
          <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
            <router-link to="/" class="text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 border-indigo-500 text-sm font-medium">
              Início
            </router-link>
            <router-link to="/pricing" class="text-gray-500 hover:text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 border-transparent hover:border-gray-300 text-sm font-medium">
              Preços
            </router-link>
            <router-link to="/contact" class="text-gray-500 hover:text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 border-transparent hover:border-gray-300 text-sm font-medium">
              Contato
            </router-link>
          </div>
        </div>
        
        <!-- Login/Register buttons when user is not authenticated -->
        <div v-if="!authStore.isAuthenticated" class="flex items-center">
          <router-link to="/login" class="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
            Entrar
          </router-link>
          <router-link to="/register" class="bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700">
            Cadastrar
          </router-link>
        </div>
        
        <!-- Profile dropdown when user is authenticated -->
        <div v-else class="flex items-center">
          <div class="relative">
            <button @click="toggleDropdown" class="flex items-center space-x-2 focus:outline-none">
              <div class="h-9 w-9 rounded-full bg-indigo-600 flex items-center justify-center text-white text-lg font-medium overflow-hidden">
                <img v-if="profileImageUrl" :src="profileImageUrl" alt="Profile" class="h-full w-full object-cover" />
                <span v-else>{{ userFirstLetter }}</span>
              </div>
              <span class="text-sm font-medium text-gray-700">{{ userName }}</span>
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </button>
            <!-- Dropdown menu -->
            <div v-if="showDropdown" class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10">
              <router-link to="/profile" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                Configurações de Perfil
              </router-link>
              <button @click="handleLogout" class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                Sair
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const authStore = useAuthStore();
const showDropdown = ref(false);

// Close dropdown when clicking outside
const closeDropdownOnOutsideClick = (event) => {
  if (showDropdown.value && !event.target.closest('.relative')) {
    showDropdown.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', closeDropdownOnOutsideClick);
});

onUnmounted(() => {
  document.removeEventListener('click', closeDropdownOnOutsideClick);
});

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
};

const handleLogout = async () => {
  try {
    await authStore.logout();
    showDropdown.value = false;
    router.push('/');
  } catch (error) {
    console.error('Logout error:', error);
  }
};

// Computed properties for user info
const userName = computed(() => {
  return authStore.userProfile?.name || authStore.user?.email?.split('@')[0] || 'Usuário';
});

const userFirstLetter = computed(() => {
  const name = authStore.userProfile?.name || authStore.user?.email || '';
  if (!name) return 'U';
  return name[0].toUpperCase() || 'U';
});

const profileImageUrl = computed(() => {
  return authStore.userProfile?.photoURL || null;
});
</script>

<style scoped>
.relative {
  position: relative;
}
</style>
