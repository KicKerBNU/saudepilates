<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8 flex justify-between items-center">
        <h1 class="text-3xl font-bold text-gray-900">Gerenciamento de Professores</h1>
        <button 
          @click="openAddProfessorModal"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 cursor-pointer"
        >
          <svg class="h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Adicionar Professor
        </button>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
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
                Total de Professores
              </dt>
              <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                {{ professorsList.length }}
              </dd>
            </div>
          </dl>
        </div>
      </div>
      
      <!-- Professors List -->
      <div class="bg-white shadow overflow-hidden sm:rounded-md">
        <div class="px-4 py-5 sm:px-6">
          <h3 class="text-lg leading-6 font-medium text-gray-900">
            Lista de Professores
          </h3>
          <p class="mt-1 max-w-2xl text-sm text-gray-500">
            Professores associados à sua empresa.
          </p>
        </div>
        
        <div v-if="loading" class="text-center py-10">
          <svg class="animate-spin h-10 w-10 mx-auto text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p class="mt-2 text-sm text-gray-500">Carregando professores...</p>
        </div>
        
        <div v-else-if="professorsList.length === 0" class="text-center py-10">
          <svg class="mx-auto h-12 w-12 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <p class="mt-2 text-sm text-gray-500">Você ainda não possui professores cadastrados.</p>
          <button 
            @click="openAddProfessorModal"
            class="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 cursor-pointer"
          >
            Adicionar Professor
          </button>
        </div>
        
        <ul v-else role="list" class="divide-y divide-gray-200">
          <li v-for="professor in professorsList" :key="professor.id" class="px-4 py-4 sm:px-6 hover:bg-gray-50">
            <div class="flex items-center justify-between">
              <div class="min-w-0 flex-1">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-12 w-12 rounded-full bg-gray-200 flex items-center justify-center">
                    <svg class="h-8 w-8 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                  </div>
                  <div class="ml-4">
                    <h4 class="text-lg font-medium text-gray-900">{{ professor.name }}</h4>
                    <p class="text-sm text-gray-500">{{ professor.email }}</p>
                    <p v-if="professor.phone" class="text-sm text-gray-500">{{ professor.phone }}</p>
                    <p class="text-sm text-gray-500">
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                        Comissão: {{ professor.commission || 0 }}%
                      </span>
                    </p>
                  </div>
                </div>
              </div>
              <div class="ml-4 flex">
                <button 
                  @click="openEditProfessorModal(professor)"
                  class="inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-indigo-700 bg-indigo-100 hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 mr-2 cursor-pointer"
                >
                  Editar
                </button>
                <button 
                  @click="confirmDeleteProfessor(professor)"
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
    
    <!-- Add Professor Modal -->
    <div v-if="showAddProfessorModal" class="fixed z-10 inset-0 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" aria-hidden="true" @click="closeAddProfessorModal"></div>

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
                Adicionar Novo Professor
              </h3>
              <div class="mt-2">
                <p class="text-sm text-gray-500">
                  Preencha os detalhes para adicionar um novo professor à sua empresa.
                </p>
              </div>
            </div>
          </div>
          
          <form @submit.prevent="registerProfessor" class="mt-5">
            <div v-if="error" class="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {{ error }}
            </div>
            
            <div class="mb-4">
              <label for="name" class="block text-sm font-medium text-gray-700 mb-1">Nome Completo</label>
              <input 
                type="text" 
                id="name" 
                v-model="newProfessor.name" 
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
                v-model="newProfessor.email" 
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
                v-model="newProfessor.phone" 
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="(00) 00000-0000"
              />
            </div>
            
            <div class="mb-4">
              <label for="commission" class="block text-sm font-medium text-gray-700 mb-1">Comissão (%)</label>
              <input 
                type="number" 
                id="commission" 
                v-model.number="newProfessor.commission" 
                required
                min="0"
                max="100"
                step="1"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" 
                placeholder="50"
              />
            </div>

            <div class="mb-4">
              <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Senha</label>
              <input 
                type="password" 
                id="password" 
                v-model="newProfessor.password" 
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
                :disabled="isCreating"
              >
                <span v-if="isCreating">
                  <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Criando...
                </span>
                <span v-else>Adicionar Professor</span>
              </button>
              <button 
                type="button" 
                class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:w-auto sm:text-sm"
                @click="closeAddProfessorModal"
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
                Remover Professor
              </h3>
              <div class="mt-2">
                <p class="text-sm text-gray-500">
                  Tem certeza que deseja remover o professor <span class="font-semibold">{{ professorToDelete?.name }}</span>?
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
              @click="deleteProfessor"
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
    
    <!-- Edit Professor Modal -->
    <div v-if="showEditProfessorModal" class="fixed z-10 inset-0 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" aria-hidden="true" @click="closeEditProfessorModal"></div>

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
                Editar Professor
              </h3>
              <div class="mt-2">
                <p class="text-sm text-gray-500">
                  Atualize os detalhes do professor.
                </p>
              </div>
            </div>
          </div>
          
          <form @submit.prevent="updateProfessor" class="mt-5">
            <div v-if="error" class="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {{ error }}
            </div>
            
            <div class="mb-4">
              <label for="edit-name" class="block text-sm font-medium text-gray-700 mb-1">Nome Completo</label>
              <input 
                type="text" 
                id="edit-name" 
                v-model="editingProfessor.name" 
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            
            <div class="mb-4">
              <label for="edit-email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input 
                type="email" 
                id="edit-email" 
                v-model="editingProfessor.email" 
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            
            <div class="mb-4">
              <label for="edit-phone" class="block text-sm font-medium text-gray-700 mb-1">Telefone</label>
              <input 
                type="tel" 
                id="edit-phone" 
                v-model="editingProfessor.phone" 
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div class="mb-4">
              <label for="edit-commission" class="block text-sm font-medium text-gray-700 mb-1">Comissão (%)</label>
              <input 
                type="number" 
                id="edit-commission" 
                v-model.number="editingProfessor.commission" 
                required
                min="0"
                max="100"
                step="1"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" 
              />
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
                @click="closeEditProfessorModal"
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
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

// UI state
const loading = ref(true);
const error = ref('');
const showAddProfessorModal = ref(false);
const showEditProfessorModal = ref(false);
const showDeleteConfirmModal = ref(false);
const isCreating = ref(false);
const isUpdating = ref(false);
const isDeleting = ref(false);
const professorToDelete = ref(null);

// Data
const professorsList = ref([]);
const newProfessor = reactive({
  name: '',
  email: '',
  phone: '',
  password: '',
  commission: 50 // Default commission of 50%
});
const editingProfessor = reactive({
  id: '',
  name: '',
  email: '',
  phone: '',
  commission: 0
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
  
  await fetchProfessors();
});

// Methods
const fetchProfessors = async () => {
  loading.value = true;
  error.value = '';
  
  try {
    // Fetch professors from the auth store where role is professor
    professorsList.value = await authStore.getUsersByCompany('professor');
    console.log('Professors loaded:', professorsList.value);
  } catch (err) {
    console.error('Error fetching professors:', err);
    error.value = 'Erro ao carregar professores. Tente novamente.';
  } finally {
    loading.value = false;
  }
};

const openAddProfessorModal = () => {
  showAddProfessorModal.value = true;
};

const closeAddProfessorModal = () => {
  showAddProfessorModal.value = false;
  
  // Reset form
  newProfessor.name = '';
  newProfessor.email = '';
  newProfessor.phone = '';
  newProfessor.password = '';
  newProfessor.commission = 50; // Reset to default commission
  confirmPassword.value = '';
  error.value = '';
};

const registerProfessor = async () => {
  // Prevent multiple attempts
  if (isCreating.value) return;
  
  error.value = '';
  isCreating.value = true;
  
  // Validate passwords match
  if (newProfessor.password !== confirmPassword.value) {
    error.value = 'As senhas não coincidem';
    isCreating.value = false;
    return;
  }
  
  try {
    // Create professor user with company relationship
    await authStore.createUserForCompany(
      newProfessor.email, 
      newProfessor.password, 
      'professor', 
      {
        name: newProfessor.name,
        phone: newProfessor.phone,
        commission: newProfessor.commission
      }
    );
    
    // Refresh professors list
    await fetchProfessors();
    
    // Close modal and reset form
    closeAddProfessorModal();
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
      error.value = 'Erro ao criar professor: ' + (err.message || 'Tente novamente.');
    }
    console.error('Registration error:', err);
  } finally {
    isCreating.value = false;
  }
};

const openEditProfessorModal = (professor) => {
  // Populate the form with professor data
  editingProfessor.id = professor.id;
  editingProfessor.name = professor.name;
  editingProfessor.email = professor.email;
  editingProfessor.phone = professor.phone || '';
  editingProfessor.commission = professor.commission || 50;
  
  showEditProfessorModal.value = true;
};

const closeEditProfessorModal = () => {
  showEditProfessorModal.value = false;
  error.value = '';
  
  // Reset form
  editingProfessor.id = '';
  editingProfessor.name = '';
  editingProfessor.email = '';
  editingProfessor.phone = '';
  editingProfessor.commission = 50; // Reset to default commission
};

const updateProfessor = async () => {
  isUpdating.value = true;
  error.value = '';
  
  try {
    // Update user in Firebase
    await authStore.updateUser(editingProfessor.id, {
      name: editingProfessor.name,
      email: editingProfessor.email,
      phone: editingProfessor.phone,
      commission: editingProfessor.commission
    });
    
    // Update the local list
    const index = professorsList.value.findIndex(p => p.id === editingProfessor.id);
    if (index !== -1) {
      professorsList.value[index] = {
        ...professorsList.value[index],
        name: editingProfessor.name,
        email: editingProfessor.email,
        phone: editingProfessor.phone,
        commission: editingProfessor.commission
      };
    }
    
    // Close the modal
    closeEditProfessorModal();
  } catch (err) {
    console.error('Error updating professor:', err);
    error.value = 'Erro ao atualizar professor: ' + err.message;
  } finally {
    isUpdating.value = false;
  }
};

const confirmDeleteProfessor = (professor) => {
  professorToDelete.value = professor;
  showDeleteConfirmModal.value = true;
  error.value = '';
};

const cancelDelete = () => {
  showDeleteConfirmModal.value = false;
  professorToDelete.value = null;
  error.value = '';
};

const deleteProfessor = async () => {
  if (!professorToDelete.value) return;
  
  isDeleting.value = true;
  error.value = '';
  
  try {
    // Delete the professor from Firebase
    await authStore.deleteUser(professorToDelete.value.id);
    
    // Remove from the local list
    professorsList.value = professorsList.value.filter(p => p.id !== professorToDelete.value.id);
    
    // Close the modal
    cancelDelete();
  } catch (err) {
    console.error('Error deleting professor:', err);
    error.value = 'Erro ao remover professor: ' + err.message;
  } finally {
    isDeleting.value = false;
  }
};
</script>
