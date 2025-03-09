<template>
  <div class="container mx-auto py-12 px-4">
    <div class="max-w-md mx-auto">
      <h1 class="text-3xl font-bold text-center text-gray-800 mb-8">Login</h1>
      <LoginForm />
      <div class="mt-4 text-center">
        <span class="text-gray-600">Não tem uma conta?</span>
        <router-link to="/register" class="ml-1 text-blue-600 hover:text-blue-800">Cadastre-se</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import LoginForm from '../components/auth/LoginForm.vue';
</script>
