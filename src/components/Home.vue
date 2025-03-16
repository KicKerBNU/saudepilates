<!-- Home.vue -->
<template>
  <div class="min-h-screen bg-gray-50">
    <MetaTags
      title="SaúdePilates - Sistema de Gestão para Estúdios de Pilates"
      description="Gerencie seu estúdio de Pilates com eficiência. Software completo com controle de alunos, agendamento de aulas e gestão financeira."
      keywords="pilates, gestão de estúdio, sistema para pilates, agendamento, controle financeiro, software pilates, controle de alunos"
    />
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
                    <router-link to="/register" class="w-full flex items-center justify-center px-8 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 md:py-4 md:text-lg md:px-10">
                      Começar Agora
                    </router-link>
                  </div>
                </div>
              </div>
              <div class="mt-12 relative sm:max-w-lg sm:mx-auto lg:mt-0 lg:max-w-none lg:mx-0">
                <img class="w-full rounded-lg shadow-xl" src="https://images.unsplash.com/photo-1518611012118-696072aa579a?ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80" alt="Woman doing Pilates" />
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
        
        <div class="mt-6 lg:text-center">
          <h3 class="text-xl leading-6 font-bold text-gray-900 mb-4">Para Alunos</h3>
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

    <!-- Professor Features Section -->
    <div class="py-12 bg-gray-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="lg:text-center">
          <h3 class="text-xl leading-6 font-bold text-gray-900 mb-4">Para Professores</h3>
          <p class="mt-2 text-2xl leading-8 font-bold tracking-tight text-indigo-600 sm:text-3xl">
            Ensine com Excelência
          </p>
        </div>

        <div class="mt-10">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
            <div>
              <img class="w-full rounded-lg shadow-xl" src="https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80" alt="Friendly Pilates instructor with student" />
            </div>
            <div class="space-y-8">
              <!-- Student Management -->
              <div class="relative">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-12 w-12 rounded-md bg-indigo-500 text-white flex items-center justify-center">
                    <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                    </svg>
                  </div>
                  <h3 class="ml-4 text-lg leading-6 font-medium text-gray-900">Gerenciamento de Alunos</h3>
                </div>
                <p class="mt-2 ml-16 text-base text-gray-500">
                  Acesse facilmente informações detalhadas de seus alunos, acompanhe seu progresso e personalize seus treinos.
                </p>
              </div>

              <!-- Attendance Control -->
              <div class="relative">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-12 w-12 rounded-md bg-indigo-500 text-white flex items-center justify-center">
                    <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
                    </svg>
                  </div>
                  <h3 class="ml-4 text-lg leading-6 font-medium text-gray-900">Controle de Presença</h3>
                </div>
                <p class="mt-2 ml-16 text-base text-gray-500">
                  Registre presenças com apenas um clique, visualize histórico de frequência e receba notificações de ausências.
                </p>
              </div>

              <!-- Financial Tracking -->
              <div class="relative">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-12 w-12 rounded-md bg-indigo-500 text-white flex items-center justify-center">
                    <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                  <h3 class="ml-4 text-lg leading-6 font-medium text-gray-900">Acompanhamento Financeiro</h3>
                </div>
                <p class="mt-2 ml-16 text-base text-gray-500">
                  Acompanhe seus ganhos mensais, visualize relatórios e planeje seu crescimento profissional com dados claros.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Admin Features Section -->
    <div class="py-12 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="lg:text-center">
          <h3 class="text-xl leading-6 font-bold text-gray-900 mb-4">Para Administradores</h3>
          <p class="mt-2 text-2xl leading-8 font-bold tracking-tight text-indigo-600 sm:text-3xl">
            Gerencie com Poder e Precisão
          </p>
        </div>

        <div class="mt-10">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
            <div class="order-2 md:order-1 space-y-8">
              <!-- System Management -->
              <div class="relative">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-12 w-12 rounded-md bg-indigo-500 text-white flex items-center justify-center">
                    <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                  </div>
                  <h3 class="ml-4 text-lg leading-6 font-medium text-gray-900">Gestão Completa do Sistema</h3>
                </div>
                <p class="mt-2 ml-16 text-base text-gray-500">
                  Configure todos os aspectos do estúdio, aulas, horários e perfis de usuários a partir de um único painel.
                </p>
              </div>

              <!-- Financial Reports -->
              <div class="relative">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-12 w-12 rounded-md bg-indigo-500 text-white flex items-center justify-center">
                    <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                  </div>
                  <h3 class="ml-4 text-lg leading-6 font-medium text-gray-900">Relatórios Financeiros</h3>
                </div>
                <p class="mt-2 ml-16 text-base text-gray-500">
                  Acesse relatórios detalhados de receitas, pagamentos e projeções financeiras para tomar decisões estratégicas.
                </p>
              </div>

              <!-- User Administration -->
              <div class="relative">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-12 w-12 rounded-md bg-indigo-500 text-white flex items-center justify-center">
                    <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                  </div>
                  <h3 class="ml-4 text-lg leading-6 font-medium text-gray-900">Administração de Usuários</h3>
                </div>
                <p class="mt-2 ml-16 text-base text-gray-500">
                  Gerencie permissões, crie novos usuários e atribua funções específicas para professores e alunos.
                </p>
              </div>
            </div>
            <div class="order-1 md:order-2">
              <img class="w-full rounded-lg shadow-xl" src="https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80" alt="Administrator working on dashboard" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- CTA Section -->
    <div class="bg-indigo-700">
      <div class="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:py-16 lg:px-8 lg:flex lg:items-center lg:justify-between">
        <h2 class="text-3xl font-extrabold tracking-tight text-white sm:text-4xl">
          <span class="block">Pronto para começar?</span>
          <span class="block text-indigo-200">Junte-se à comunidade SaúdePilates hoje.</span>
        </h2>
        <div class="mt-8 flex lg:mt-0 lg:flex-shrink-0">
          <div class="inline-flex rounded-md shadow">
            <router-link to="/register" class="inline-flex items-center justify-center px-5 py-3 border border-transparent text-base font-medium rounded-md text-indigo-600 bg-white hover:bg-indigo-50">
              Cadastre-se Agora
            </router-link>
          </div>
          <div class="ml-3 inline-flex rounded-md shadow">
            <router-link to="/login" class="inline-flex items-center justify-center px-5 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
              Fazer Login
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import MetaTags from '@/components/MetaTags.vue';
</script>
