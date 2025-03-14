<!-- NavBar.vue -->
<template>
  <nav class="bg-white shadow-lg">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <div class="flex">
          <div class="flex-shrink-0 flex items-center">
            <router-link :to="homeOrDashboardLink" class="flex items-center">
              <img class="h-8 w-8" src="@/assets/pilates-icon.svg" alt="Pilates Icon" />
              <span class="ml-2 text-xl font-semibold text-gray-800">SaúdePilates</span>
            </router-link>
          </div>
          <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
            <router-link :to="homeOrDashboardLink" :class="[isActiveRoute('/') || isDashboardActive ? activeClass : inactiveClass]">
              Início
            </router-link>
            <router-link to="/pricing" :class="[isActiveRoute('/pricing') ? activeClass : inactiveClass]">
              Preços
            </router-link>
            <router-link to="/contact" :class="[isActiveRoute('/contact') ? activeClass : inactiveClass]">
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
        <div v-else class="flex items-center space-x-4">
          <div class="relative">
            <button @click="toggleDropdown" class="flex items-center space-x-2 focus:outline-none cursor-pointer">
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
              <!-- Dashboard link based on user role -->
              <router-link 
                v-if="authStore.isAdmin" 
                :to="{name: 'AdminDashboard'}" 
                class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                <div class="flex items-center">
                  <svg class="h-4 w-4 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zm10 0a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zm10 0a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                  </svg>
                  Dashboard Admin
                </div>
              </router-link>
              
              <router-link 
                v-if="authStore.isProfessor" 
                :to="{name: 'ProfessorDashboard'}" 
                class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                <div class="flex items-center">
                  <svg class="h-4 w-4 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                  Dashboard Professor
                </div>
              </router-link>
              
              <router-link 
                v-if="authStore.isStudent" 
                :to="{name: 'StudentDashboard'}" 
                class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                <div class="flex items-center">
                  <svg class="h-4 w-4 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path d="M12 14l9-5-9-5-9 5 9 5z" />
                    <path d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l9-5-9-5-9 5 9 5zm0 0l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14zm-4 6v-7.5l4-2.222" />
                  </svg>
                  Dashboard Aluno
                </div>
              </router-link>
              
              <div class="border-t border-gray-100 my-1"></div>
              
              <button @click="handleLogout" class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                <div class="flex items-center">
                  <svg class="h-4 w-4 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                  Sair
                </div>
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
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const showDropdown = ref(false);

// Navigation classes
const activeClass = "text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 border-indigo-500 text-sm font-medium";
const inactiveClass = "text-gray-500 hover:text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 border-transparent hover:border-gray-300 text-sm font-medium";

// Check if route is active
const isActiveRoute = (path) => {
  return route.path === path;
};

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

// Computed property to determine where the home/logo links should go
const homeOrDashboardLink = computed(() => {
  if (!authStore.isAuthenticated) return '/';
  if (authStore.isAdmin) return { name: 'AdminDashboard' };
  if (authStore.isProfessor) return { name: 'ProfessorDashboard' };
  if (authStore.isStudent) return { name: 'StudentDashboard' };
  return '/';
});

// Check if the current route is a dashboard route
const isDashboardActive = computed(() => {
  return route.name === 'AdminDashboard' || route.name === 'ProfessorDashboard' || route.name === 'StudentDashboard';
});
</script>

<style scoped>
.relative {
  position: relative;
}
</style>
