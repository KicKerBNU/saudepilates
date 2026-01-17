<script setup>
import { onMounted, ref } from 'vue';
import NavBar from './components/NavBar.vue';

const isLoading = ref(true);

onMounted(() => {
  // Simply set loading to false - no Firebase initialization
  isLoading.value = false;
});

</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div v-if="isLoading" class="flex items-center justify-center min-h-screen">
      <div class="text-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto"></div>
        <p class="mt-4 text-gray-600">{{ $t('app.loading') }}</p>
      </div>
    </div>
    <div v-else>
      <NavBar />
      <router-view></router-view>
    </div>
  </div>
</template>

<style scoped>
.logo {
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
