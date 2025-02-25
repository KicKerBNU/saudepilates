<!-- Home.vue -->
<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Hero Section -->
    <div class="relative overflow-hidden">
      <div class="max-w-7xl mx-auto">
        <div class="relative z-10 pb-8 sm:pb-16 md:pb-20 lg:w-full lg:pb-28 xl:pb-32">
          <main class="mt-10 mx-auto max-w-7xl px-4 sm:mt-12 sm:px-6 lg:mt-16 lg:px-8 xl:mt-28">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
              <div>
                <h1 class="text-4xl tracking-tight font-extrabold text-gray-900 sm:text-5xl md:text-6xl">
                  <span class="block">Transforme seu corpo</span>
                  <span class="block text-indigo-600">com Pilates</span>
                </h1>
                <p class="mt-3 text-base text-gray-500 sm:mt-5 sm:text-lg sm:max-w-xl sm:mx-auto md:mt-5 md:text-xl lg:mx-0">
                  Experimente o poder transformador do Pilates. Fortaleça seu core, melhore a flexibilidade e aprimore seu bem-estar geral com nossas sessões guiadas e acompanhamento de progresso.
                </p>
                <div class="mt-5 sm:mt-8 sm:flex sm:justify-start">
                  <div class="rounded-md shadow">
                    <a href="#features" class="w-full flex items-center justify-center px-8 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 md:py-4 md:text-lg md:px-10">
                      Começar Agora
                    </a>
                  </div>
                </div>
              </div>
              <div class="mt-12 relative sm:max-w-lg sm:mx-auto lg:mt-0 lg:max-w-none lg:mx-0">
                <img class="w-full rounded-lg shadow-xl" src="@/assets/pilates-hero.jpg" alt="Woman doing Pilates" />
              </div>
            </div>
          </main>
        </div>
      </div>
    </div>

    <!-- Features Section -->
    <div id="features" class="py-12 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="lg:text-center">
          <h2 class="text-base text-indigo-600 font-semibold tracking-wide uppercase">Recursos</h2>
          <p class="mt-2 text-3xl leading-8 font-extrabold tracking-tight text-gray-900 sm:text-4xl">
            Tudo que você precisa para ter sucesso
          </p>
        </div>

        <div class="mt-10">
          <div class="space-y-10 md:space-y-0 md:grid md:grid-cols-3 md:gap-x-8 md:gap-y-10">
            <!-- Progress Tracking -->
            <div class="relative">
              <div class="flex items-center justify-center h-12 w-12 rounded-md bg-indigo-500 text-white">
                <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
              </div>
              <div class="mt-5">
                <h3 class="text-lg leading-6 font-medium text-gray-900">Acompanhamento de Progresso</h3>
                <p class="mt-2 text-base text-gray-500">
                  Acompanhe sua evolução em cada exercício com métricas detalhadas de progresso e conquistas.
                </p>
              </div>
            </div>

            <!-- Calendar Integration -->
            <div class="relative">
              <div class="flex items-center justify-center h-12 w-12 rounded-md bg-indigo-500 text-white">
                <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
              <div class="mt-5">
                <h3 class="text-lg leading-6 font-medium text-gray-900">Agenda Inteligente</h3>
                <p class="mt-2 text-base text-gray-500">
                  Gerencie seus horários de aula e nunca perca uma sessão com nossa agenda integrada.
                </p>
              </div>
            </div>

            <!-- Absence Notification -->
            <div class="relative">
              <div class="flex items-center justify-center h-12 w-12 rounded-md bg-indigo-500 text-white">
                <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
                </svg>
              </div>
              <div class="mt-5">
                <h3 class="text-lg leading-6 font-medium text-gray-900">Comunicação Fácil</h3>
                <p class="mt-2 text-base text-gray-500">
                  Avise seu instrutor sobre faltas com apenas alguns cliques.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Component logic can be added here if needed
</script>
