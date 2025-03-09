<template>
  <div class="container mx-auto py-12 px-4">
    <div class="max-w-md mx-auto">
      <h1 class="text-3xl font-bold text-center text-gray-800 mb-8">Cadastro</h1>
      <RegisterForm />
      <div class="mt-4 text-center">
        <span class="text-gray-600">Já tem uma conta?</span>
        <router-link to="/login" class="ml-1 text-blue-600 hover:text-blue-800">Entrar</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import RegisterForm from '../components/auth/RegisterForm.vue';
</script>
