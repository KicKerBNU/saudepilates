<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8 flex justify-between items-center">
        <h1 class="text-3xl font-bold text-gray-900">Gerenciamento de Alunos</h1>
        <button 
          @click="openAddStudentModal"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 cursor-pointer"
        >
          <svg class="h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Adicionar Aluno
        </button>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="mb-4">
        <Breadcrumb :items="breadcrumbItems" />
      </div>
      
      <!-- Company Info -->
      <div class="bg-white shadow overflow-hidden sm:rounded-lg mb-6">
        <div class="px-4 py-5 sm:px-6">
          <h3 class="text-lg leading-6 font-medium text-gray-900">
            Detalhes da Empresa
          </h3>
          <p class="mt-1 max-w-2xl text-sm text-gray-500">
            Informações da sua empresa registrada.
          </p>
        </div>
        <div class="border-t border-gray-200">
          <dl>
            <div class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt class="text-sm font-medium text-gray-500">
                Nome da Empresa
              </dt>
              <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                {{ companyName }}
              </dd>
            </div>
            <div class="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt class="text-sm font-medium text-gray-500">
                Total de Alunos
              </dt>
              <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                {{ studentsList.length }}
              </dd>
            </div>
          </dl>
        </div>
      </div>
      
      <!-- Students List -->
      <div class="bg-white shadow overflow-hidden sm:rounded-md">
        <div class="px-4 py-5 sm:px-6">
          <h3 class="text-lg leading-6 font-medium text-gray-900">
            Lista de Alunos
          </h3>
          <p class="mt-1 max-w-2xl text-sm text-gray-500">
            Alunos associados à sua empresa.
          </p>
        </div>
        
        <div v-if="loading" class="text-center py-10">
          <svg class="animate-spin h-10 w-10 mx-auto text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p class="mt-2 text-sm text-gray-500">Carregando alunos...</p>
        </div>
        
        <div v-else-if="studentsList.length === 0" class="text-center py-10">
          <svg class="mx-auto h-12 w-12 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <p class="mt-2 text-sm text-gray-500">Você ainda não possui alunos cadastrados.</p>
          <button 
            @click="openAddStudentModal"
            class="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 cursor-pointer"
          >
            Adicionar Aluno
          </button>
        </div>
        
        <ul v-else role="list" class="divide-y divide-gray-200">
          <li v-for="student in studentsList" :key="student.id" class="px-4 py-4 sm:px-6 hover:bg-gray-50">
            <div class="flex items-center justify-between">
              <div class="min-w-0 flex-1">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-12 w-12 rounded-full bg-gray-200 flex items-center justify-center">
                    <svg class="h-8 w-8 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                  </div>
                  <div class="ml-4">
                    <h4 class="text-lg font-medium text-gray-900">{{ student.name }}</h4>
                    <p class="text-sm text-gray-500">{{ student.email }}</p>
                    <p v-if="student.phone" class="text-sm text-gray-500">{{ student.phone }}</p>
                    <div class="flex items-center space-x-2 mt-1">
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium" :class="{
                        'bg-green-100 text-green-800': student.planId,
                        'bg-gray-100 text-gray-800': !student.planId
                      }">
                        {{ plansList.find(p => p.id === student.planId)?.title || 'Sem plano' }}
                      </span>
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium" :class="{
                        'bg-blue-100 text-blue-800': student.professorId,
                        'bg-gray-100 text-gray-800': !student.professorId
                      }">
                        Professor: {{ professorsList.find(p => p.id === student.professorId)?.name || 'Não atribuído' }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="ml-4 flex">
                <button 
                  @click="openEditStudentModal(student)"
                  class="inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-indigo-700 bg-indigo-100 hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 mr-2 cursor-pointer"
                >
                  Editar
                </button>
                <button 
                  @click="confirmDeleteStudent(student)"
                  class="inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-red-700 bg-red-100 hover:bg-red-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 cursor-pointer"
                >
                  Remover
                </button>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </main>
    
    <!-- Add Student Modal -->
    <div v-if="showAddStudentModal" class="fixed z-10 inset-0 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" aria-hidden="true" @click="closeAddStudentModal"></div>

        <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <div class="sm:flex sm:items-start">
            <div class="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-indigo-100 sm:mx-0 sm:h-10 sm:w-10">
              <svg class="h-6 w-6 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
              </svg>
            </div>
            <div class="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left">
              <h3 class="text-lg leading-6 font-medium text-gray-900" id="modal-title">
                Adicionar Novo Aluno
              </h3>
              <div class="mt-2">
                <p class="text-sm text-gray-500">
                  Preencha os detalhes para adicionar um novo aluno à sua empresa.
                </p>
              </div>
            </div>
          </div>
          
          <form @submit.prevent="registerStudent" class="mt-5">
            <div v-if="error" class="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {{ error }}
            </div>
            
            <!-- Missing Professors Warning -->
            <div v-if="missingProfessors" class="mb-4 bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded flex items-start">
              <svg class="flex-shrink-0 h-5 w-5 text-yellow-400 mt-0.5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
              </svg>
              <div class="ml-3">
                <h3 class="text-sm font-medium">Nenhum professor cadastrado</h3>
                <div class="mt-2 text-sm">
                  <p>Você precisa cadastrar pelo menos um professor antes de adicionar um aluno.</p>
                  <router-link 
                    to="/admin/professors" 
                    class="mt-2 inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md shadow-sm text-white bg-yellow-600 hover:bg-yellow-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500"
                  >
                    Ir para Cadastro de Professores
                  </router-link>
                </div>
              </div>
            </div>
            
            <!-- Missing Plans Warning -->
            <div v-if="missingPlans" class="mb-4 bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded flex items-start">
              <svg class="flex-shrink-0 h-5 w-5 text-yellow-400 mt-0.5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
              </svg>
              <div class="ml-3">
                <h3 class="text-sm font-medium">Nenhum plano cadastrado</h3>
                <div class="mt-2 text-sm">
                  <p>Você precisa cadastrar pelo menos um plano antes de adicionar um aluno.</p>
                  <router-link 
                    to="/admin/plans" 
                    class="mt-2 inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md shadow-sm text-white bg-yellow-600 hover:bg-yellow-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500"
                  >
                    Ir para Cadastro de Planos
                  </router-link>
                </div>
              </div>
            </div>
            
            <div class="mb-4">
              <label for="name" class="block text-sm font-medium text-gray-700 mb-1">Nome Completo</label>
              <input 
                type="text" 
                id="name" 
                v-model="newStudent.name" 
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="João Silva"
              />
            </div>
            
            <div class="mb-4">
              <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input 
                type="email" 
                id="email" 
                v-model="newStudent.email" 
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="exemplo@email.com"
              />
            </div>
            
            <div class="mb-4">
              <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">Telefone</label>
              <input 
                type="tel" 
                id="phone" 
                v-model="newStudent.phone" 
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="(00) 00000-0000"
              />
            </div>

            <div class="mb-4">
              <label for="planId" class="block text-sm font-medium text-gray-700 mb-1">Plano *</label>
              <select
                id="planId"
                v-model="newStudent.planId"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="" disabled>Selecione um plano</option>
                <option v-for="plan in plansList" :key="plan.id" :value="plan.id">
                  {{ plan.title }} - {{ plan.sessionsPerWeek }} sessões/semana - R$ {{ plan.price.toFixed(2) }}/mês
                </option>
              </select>
            </div>

            <div class="mb-4">
              <label for="professorId" class="block text-sm font-medium text-gray-700 mb-1">Professor *</label>
              <select
                id="professorId"
                v-model="newStudent.professorId"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="" disabled>Selecione um professor</option>
                <option v-for="professor in professorsList" :key="professor.id" :value="professor.id">
                  {{ professor.name }}
                </option>
              </select>
            </div>
            
            <div class="mb-4">
              <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Senha</label>
              <input 
                type="password" 
                id="password" 
                v-model="newStudent.password" 
                required
                minlength="6"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" 
                placeholder="******"
              />
            </div>
            
            <div class="mb-4">
              <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">Confirmar Senha</label>
              <input 
                type="password" 
                id="confirmPassword" 
                v-model="confirmPassword" 
                required
                minlength="6"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" 
                placeholder="******"
              />
            </div>
            
            <div class="mt-5 sm:mt-4 sm:flex sm:flex-row-reverse">
              <button 
                type="submit" 
                class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3 sm:w-auto sm:text-sm"
                :disabled="isCreating || missingProfessors || missingPlans"
              >
                <span v-if="isCreating">
                  <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Criando...
                </span>
                <span v-else>Adicionar Aluno</span>
              </button>
              <button 
                type="button" 
                class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:w-auto sm:text-sm"
                @click="closeAddStudentModal"
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    
    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteConfirmModal" class="fixed z-10 inset-0 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" aria-hidden="true" @click="cancelDelete"></div>

        <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <div>
            <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100">
              <svg class="h-6 w-6 text-red-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
            <div class="mt-3 text-center sm:mt-5">
              <h3 class="text-lg leading-6 font-medium text-gray-900" id="modal-title">
                Remover Aluno
              </h3>
              <div class="mt-2">
                <p class="text-sm text-gray-500">
                  Tem certeza que deseja remover o aluno <span class="font-semibold">{{ studentToDelete?.name }}</span>?
                  Esta ação não pode ser desfeita.
                </p>
              </div>
            </div>
            
            <div v-if="error" class="mt-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {{ error }}
            </div>
          </div>
          <div class="mt-5 sm:mt-6 sm:grid sm:grid-cols-2 sm:gap-3 sm:grid-flow-row-dense">
            <button
              type="button"
              @click="deleteStudent"
              :disabled="isDeleting"
              class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:col-start-2 sm:text-sm"
            >
              <svg v-if="isDeleting" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ isDeleting ? 'Removendo...' : 'Remover' }}
            </button>
            <button
              type="button"
              @click="cancelDelete"
              class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:col-start-1 sm:text-sm"
            >
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Edit Student Modal -->
    <div v-if="showEditStudentModal" class="fixed z-10 inset-0 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" aria-hidden="true" @click="closeEditStudentModal"></div>

        <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <div class="sm:flex sm:items-start">
            <div class="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-indigo-100 sm:mx-0 sm:h-10 sm:w-10">
              <svg class="h-6 w-6 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
              </svg>
            </div>
            <div class="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left">
              <h3 class="text-lg leading-6 font-medium text-gray-900" id="modal-title">
                Editar Aluno
              </h3>
              <div class="mt-2">
                <p class="text-sm text-gray-500">
                  Atualize os detalhes do aluno.
                </p>
              </div>
            </div>
          </div>
          
          <form @submit.prevent="updateStudent" class="mt-5">
            <div v-if="error" class="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {{ error }}
            </div>
            
            <div class="mb-4">
              <label for="edit-name" class="block text-sm font-medium text-gray-700 mb-1">Nome Completo</label>
              <input 
                type="text" 
                id="edit-name" 
                v-model="editingStudent.name" 
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            
            <div class="mb-4">
              <label for="edit-email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input 
                type="email" 
                id="edit-email" 
                v-model="editingStudent.email" 
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            
            <div class="mb-4">
              <label for="edit-phone" class="block text-sm font-medium text-gray-700 mb-1">Telefone</label>
              <input 
                type="tel" 
                id="edit-phone" 
                v-model="editingStudent.phone" 
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div class="mb-4">
              <label for="edit-planId" class="block text-sm font-medium text-gray-700 mb-1">Plano *</label>
              <select
                id="edit-planId"
                v-model="editingStudent.planId"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="" disabled>Selecione um plano</option>
                <option v-for="plan in plansList" :key="plan.id" :value="plan.id">
                  {{ plan.title }} - {{ plan.sessionsPerWeek }} sessões/semana - R$ {{ plan.price.toFixed(2) }}/mês
                </option>
              </select>
            </div>

            <div class="mb-4">
              <label for="edit-professorId" class="block text-sm font-medium text-gray-700 mb-1">Professor *</label>
              <select
                id="edit-professorId"
                v-model="editingStudent.professorId"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="" disabled>Selecione um professor</option>
                <option v-for="professor in professorsList" :key="professor.id" :value="professor.id">
                  {{ professor.name }}
                </option>
              </select>
            </div>
            
            <div class="mt-5 sm:mt-4 sm:flex sm:flex-row-reverse">
              <button
                type="submit"
                :disabled="isUpdating"
                class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3 sm:w-auto sm:text-sm"
              >
                <svg v-if="isUpdating" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                {{ isUpdating ? 'Atualizando...' : 'Atualizar' }}
              </button>
              <button
                type="button"
                @click="closeEditStudentModal"
                class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:w-auto sm:text-sm"
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import Breadcrumb from '@/components/Breadcrumb.vue';

const router = useRouter();
const authStore = useAuthStore();
const route = useRoute();

// Add breadcrumb items
const breadcrumbItems = computed(() => {
  const path = route.path;
  const segments = path.split('/').filter(Boolean);
  
  return segments.map((segment, index) => {
    const path = '/' + segments.slice(0, index + 1).join('/');
    const name = segment.charAt(0).toUpperCase() + segment.slice(1);
    return { name, path };
  });
});

// UI state
const loading = ref(true);
const error = ref('');
const showAddStudentModal = ref(false);
const showEditStudentModal = ref(false);
const showDeleteConfirmModal = ref(false);
const isCreating = ref(false);
const isUpdating = ref(false);
const isDeleting = ref(false);
const studentToDelete = ref(null);
const missingProfessors = ref(false);
const missingPlans = ref(false);

// Data
const studentsList = ref([]);
const plansList = ref([]);
const professorsList = ref([]);
const newStudent = reactive({
  name: '',
  email: '',
  phone: '',
  password: '',
  planId: '',
  professorId: ''
});
const editingStudent = reactive({
  id: '',
  name: '',
  email: '',
  phone: '',
  planId: '',
  professorId: ''
});
const confirmPassword = ref('');

// Computed properties
const companyName = computed(() => authStore.companyName || 'Carregando...');

// Lifecycle hooks
onMounted(async () => {
  if (!authStore.isAdmin) {
    router.push('/login');
    return;
  }
  
  await Promise.all([
    fetchStudents(),
    fetchPlans(),
    fetchProfessors()
  ]);
});

// Methods
const fetchStudents = async () => {
  loading.value = true;
  error.value = '';

  
  try {
    // Fetch students from the auth store where role is student
    studentsList.value = await authStore.getUsersByCompany('student');

  } catch (err) {
    error.value = 'Erro ao carregar alunos: ' + err.message;
    error.value = 'Erro ao carregar alunos. Tente novamente.';
  } finally {
    loading.value = false;
  }
};

const fetchPlans = async () => {
  try {
    plansList.value = await authStore.getPlans();

  } catch (err) {
    error.value = 'Erro ao carregar planos: ' + err.message;
    error.value = 'Erro ao carregar planos. Tente novamente.';
  }
};

const fetchProfessors = async () => {
  try {
    const professors = await authStore.getUsersByCompany('professor');
    professorsList.value = professors;
  } catch (err) {
    console.error('Error fetching professors:', err);
    error.value = 'Erro ao carregar professores. Tente novamente.';
  }
};

const openAddStudentModal = async () => {
  error.value = '';
  
  // Ensure professors and plans are loaded first
  if (professorsList.value.length === 0) {
    await fetchProfessors();
  }
  
  if (plansList.value.length === 0) {
    await fetchPlans();
  }
  
  // Check if there are professors available
  const hasProfessors = professorsList.value.length > 0;
  
  // Check if there are plans available
  const hasPlans = plansList.value.length > 0;
  
  // Show modal even if missing professors or plans - we'll show warnings inside the modal
  showAddStudentModal.value = true;
  
  // Reset validation messages
  missingProfessors.value = !hasProfessors;
  missingPlans.value = !hasPlans;
};

const closeAddStudentModal = () => {
  showAddStudentModal.value = false;
  
  // Reset form
  newStudent.name = '';
  newStudent.email = '';
  newStudent.phone = '';
  newStudent.password = '';
  newStudent.planId = '';
  newStudent.professorId = '';
  confirmPassword.value = '';
  error.value = '';
};

const openEditStudentModal = async (student) => {
  // Ensure professors are loaded first
  if (professorsList.value.length === 0) {
    await fetchProfessors();
  }

  // Populate edit form with student data
  editingStudent.id = student.id;
  editingStudent.name = student.name;
  editingStudent.email = student.email;
  editingStudent.phone = student.phone || '';
  editingStudent.planId = student.planId || '';
  editingStudent.professorId = student.professorId || '';
  
  // Show modal
  showEditStudentModal.value = true;
};

const closeEditStudentModal = () => {
  showEditStudentModal.value = false;
  
  // Reset form
  editingStudent.id = '';
  editingStudent.name = '';
  editingStudent.email = '';
  editingStudent.phone = '';
  error.value = '';
};

const updateStudent = async () => {
  isUpdating.value = true;
  error.value = '';
  
  if (!editingStudent.planId) {
    error.value = 'Por favor, selecione um plano';
    return;
  }

  try {
    // Update user in Firebase
    await authStore.updateUser(editingStudent.id, {
      name: editingStudent.name,
      email: editingStudent.email,
      phone: editingStudent.phone,
      planId: editingStudent.planId,
      professorId: editingStudent.professorId
    });
    
    // Update the local list
    const index = studentsList.value.findIndex(s => s.id === editingStudent.id);
    if (index !== -1) {
      const updatedStudent = {
        ...studentsList.value[index],
        name: editingStudent.name,
        email: editingStudent.email,
        phone: editingStudent.phone,
        planId: editingStudent.planId,
        professorId: editingStudent.professorId
      };
      studentsList.value[index] = updatedStudent;
    }
    
    // Refresh the students list to ensure we have the latest data
    await fetchStudents();
    
    // Close the modal
    closeEditStudentModal();
  } catch (err) {
    console.error('Error updating student:', err);
    error.value = 'Erro ao atualizar aluno: ' + err.message;
  } finally {
    isUpdating.value = false;
  }
};

const confirmDeleteStudent = (student) => {
  studentToDelete.value = student;
  showDeleteConfirmModal.value = true;
  error.value = '';
};

const cancelDelete = () => {
  showDeleteConfirmModal.value = false;
  studentToDelete.value = null;
  error.value = '';
};

const deleteStudent = async () => {
  if (!studentToDelete.value) return;
  
  isDeleting.value = true;
  error.value = '';
  
  try {
    // Delete the student from Firebase
    await authStore.deleteUser(studentToDelete.value.id);
    
    // Remove from the local list
    studentsList.value = studentsList.value.filter(s => s.id !== studentToDelete.value.id);
    
    // Close the modal
    cancelDelete();
  } catch (err) {
    console.error('Error deleting student:', err);
    error.value = 'Erro ao remover aluno: ' + err.message;
  } finally {
    isDeleting.value = false;
  }
};

const registerStudent = async () => {
  // Prevent multiple attempts
  if (isCreating.value) return;
  
  error.value = '';
  isCreating.value = true;
  
  // Check if we have plans and professors available
  if (professorsList.value.length === 0) {
    error.value = 'Não é possível adicionar aluno: Nenhum professor cadastrado.';
    isCreating.value = false;
    missingProfessors.value = true;
    return;
  }
  
  if (plansList.value.length === 0) {
    error.value = 'Não é possível adicionar aluno: Nenhum plano cadastrado.';
    isCreating.value = false;
    missingPlans.value = true;
    return;
  }
  
  // Validate passwords match
  if (newStudent.password !== confirmPassword.value) {
    error.value = 'As senhas não coincidem';
    isCreating.value = false;
    return;
  }

  // Validate plan selection
  if (!newStudent.planId) {
    error.value = 'Por favor, selecione um plano';
    isCreating.value = false;
    return;
  }

  // Validate professor selection
  if (!newStudent.professorId) {
    error.value = 'Por favor, selecione um professor';
    isCreating.value = false;
    return;
  }
  
  try {
    // Create student user with company relationship
    await authStore.createUserForCompany(
      newStudent.email, 
      newStudent.password, 
      'student', 
      {
        name: newStudent.name,
        phone: newStudent.phone,
        planId: newStudent.planId,
        professorId: newStudent.professorId
      }
    );
    
    // Refresh students list
    await fetchStudents();
    
    // Close modal and reset form
    closeAddStudentModal();
  } catch (err) {
    // Set specific error messages based on Firebase error codes
    if (err.code === 'auth/email-already-in-use') {
      error.value = 'Este email já está em uso';
    } else if (err.code === 'auth/weak-password') {
      error.value = 'A senha é muito fraca. Use pelo menos 6 caracteres.';
    } else if (err.code === 'auth/invalid-email') {
      error.value = 'Email inválido. Verifique o formato.';
    } else if (err.code === 'auth/network-request-failed') {
      error.value = 'Falha na conexão. Verifique sua internet.';
    } else {
      error.value = 'Erro ao criar aluno: ' + (err.message || 'Tente novamente.');
    }
    console.error('Registration error:', err);
  } finally {
    isCreating.value = false;
  }
};
</script>
