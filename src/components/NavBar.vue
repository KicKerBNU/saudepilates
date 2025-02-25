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
          </div>
        </div>
        <div class="flex items-center">
          <router-link to="/login" class="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
            Entrar
          </router-link>
          <router-link to="/register" class="bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700">
            Cadastrar
          </router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
// Component logic can be added here if needed
</script>
