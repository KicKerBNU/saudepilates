<script setup>
import NavBar from './components/NavBar.vue'
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <NavBar />
    <router-view></router-view>
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
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
