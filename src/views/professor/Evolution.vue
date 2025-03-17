<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">Evolução de Alunos</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 sm:px-0">
        <!-- Student Selection Card -->
        <div class="bg-white shadow overflow-hidden sm:rounded-lg mb-6">
          <div class="px-4 py-5 sm:px-6 flex justify-between items-center">
            <div>
              <h3 class="text-lg font-medium leading-6 text-gray-900">Selecionar Aluno</h3>
              <p class="mt-1 max-w-2xl text-sm text-gray-500">Escolha o aluno para visualizar ou adicionar informações de evolução</p>
            </div>
          </div>
          <div class="px-4 py-5 sm:p-6">
            <div v-if="loading" class="flex justify-center">
              <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
            </div>
            <div v-else-if="error" class="text-red-500">{{ error }}</div>
            <div v-else class="w-full max-w-2xl mx-auto">
              <label for="student-select" class="block text-sm font-medium text-gray-700 mb-1">Aluno:</label>
              <div class="relative">
                <select
                  id="student-select"
                  v-model="selectedStudentId"
                  @change="onStudentChange"
                  class="block w-full pl-3 pr-10 py-3 text-base border-2 border-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-indigo-300 sm:text-sm rounded-lg shadow-sm appearance-none"
                >
                  <option value="" disabled>Selecione um aluno</option>
                  <option v-for="student in students" :key="student.id" :value="student.id">
                    {{ student.name }}
                  </option>
                </select>
                <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-600">
                  <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                    <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
                  </svg>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="selectedStudentId" class="mb-6">
          <!-- Add New Evolution Record Card -->
          <div class="bg-white shadow overflow-hidden sm:rounded-lg mb-6">
            <div class="px-4 py-5 sm:px-6 flex justify-between items-center">
              <div>
                <h3 class="text-lg font-medium leading-6 text-gray-900">Adicionar Nova Evolução</h3>
                <p class="mt-1 max-w-2xl text-sm text-gray-500">Registre o progresso atual do aluno</p>
              </div>
              <button
                @click="toggleAddForm"
                class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                <span v-if="showAddForm">Fechar</span>
                <span v-else>Adicionar</span>
              </button>
            </div>
            <div v-if="showAddForm" class="px-4 py-5 sm:p-6 border-t border-gray-200">
              <form @submit.prevent="saveEvolution">
                <div class="grid grid-cols-1 gap-y-8 gap-x-6 sm:grid-cols-6">
                  <div class="sm:col-span-6">
                    <label for="date" class="block text-sm font-medium text-gray-700">Data</label>
                    <div class="mt-2">
                      <input
                        type="date"
                        id="date"
                        v-model="newEvolution.date"
                        required
                        class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-2 border-gray-300 rounded-lg px-4 py-3"
                      />
                    </div>
                  </div>
                  
                  <div class="sm:col-span-3">
                    <label for="category" class="block text-sm font-medium text-gray-700">Categoria</label>
                    <div class="mt-2 relative">
                      <select
                        id="category"
                        v-model="newEvolution.category"
                        required
                        class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-2 border-gray-300 rounded-lg px-4 py-3 appearance-none"
                      >
                        <option value="" disabled>Selecione uma categoria</option>
                        <option value="postura">Postura</option>
                        <option value="flexibilidade">Flexibilidade</option>
                        <option value="forca">Força</option>
                        <option value="equilibrio">Equilíbrio</option>
                        <option value="respiracao">Respiração</option>
                        <option value="outro">Outro</option>
                      </select>
                      <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-indigo-600">
                        <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                          <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
                        </svg>
                      </div>
                    </div>
                  </div>
                  
                  <div class="sm:col-span-3">
                    <label class="block text-sm font-medium text-gray-700 mb-2">Avaliação</label>
                    <div class="mt-2">
                      <div class="flex h-[46px] border-2 border-gray-300 rounded-lg shadow-sm bg-white px-4 py-3 items-center">
                        <div class="flex justify-between w-full">
                          <template v-for="rating in 5" :key="rating">
                            <button 
                              type="button"
                              @click="newEvolution.rating = rating"
                              class="flex items-center justify-center focus:outline-none transition-all duration-150 relative h-full"
                            >
                              <svg 
                                class="w-[24px] h-[24px] transition-colors duration-150" 
                                :class="{
                                  'text-yellow-400': newEvolution.rating >= rating,
                                  'text-gray-300 hover:text-yellow-300': newEvolution.rating < rating
                                }"
                                fill="currentColor" 
                                viewBox="0 0 20 20" 
                                xmlns="http://www.w3.org/2000/svg"
                              >
                                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                              </svg>
                            </button>
                          </template>
                        </div>
                      </div>
                    </div>
                    <div class="mt-1 text-sm text-gray-500 pl-1">
                      {{ ratingDescription }}
                    </div>
                    <input type="hidden" v-model="newEvolution.rating" required />
                  </div>
                  
                  <div class="sm:col-span-6">
                    <label for="notes" class="block text-sm font-medium text-gray-700">Observações</label>
                    <div class="mt-2 relative">
                      <textarea
                        id="notes"
                        v-model="newEvolution.notes"
                        rows="4"
                        required
                        class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-2 border-gray-300 rounded-lg px-4 py-3"
                        placeholder="Adicione observações detalhadas sobre a evolução do aluno..."
                      ></textarea>
                    </div>
                  </div>
                </div>
                <div class="mt-5 flex justify-end">
                  <button
                    type="button"
                    @click="toggleAddForm"
                    class="mr-3 inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    :disabled="saving"
                    class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    <span v-if="saving">Salvando...</span>
                    <span v-else>Salvar</span>
                  </button>
                </div>
              </form>
            </div>
          </div>
          
          <!-- Evolution History Card -->
          <div class="bg-white shadow overflow-hidden sm:rounded-lg">
            <div class="px-4 py-5 sm:px-6">
              <h3 class="text-lg font-medium leading-6 text-gray-900">Histórico de Evolução</h3>
              <p class="mt-1 max-w-2xl text-sm text-gray-500">Registro da progressão do aluno</p>
            </div>
            <div v-if="loadingEvolutions" class="px-4 py-5 sm:p-6 flex justify-center">
              <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
            </div>
            <div v-else-if="evolutionError" class="px-4 py-5 sm:p-6 text-red-500">
              {{ evolutionError }}
            </div>
            <div v-else-if="studentEvolutions.length === 0" class="px-4 py-5 sm:p-6 text-gray-500">
              Nenhum registro de evolução encontrado para este aluno.
            </div>
            <div v-else class="border-t border-gray-200">
              <div class="overflow-hidden sm:rounded-lg">
                <ul class="divide-y divide-gray-200">
                  <li v-for="(evolution, index) in sortedEvolutions" :key="index" class="px-4 py-5 sm:px-6">
                    <div class="flex items-center justify-between">
                      <div class="flex-1">
                        <div class="flex items-center mb-2">
                          <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full" 
                                :class="getCategoryClass(evolution.category)">
                            {{ getCategoryLabel(evolution.category) }}
                          </span>
                          <span class="ml-2 text-sm text-gray-500">
                            {{ formatDate(evolution.date) }}
                          </span>
                          <span class="ml-auto flex">
                            <span v-for="i in 5" :key="i" class="text-sm">
                              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" :class="i <= evolution.rating ? 'text-yellow-400' : 'text-gray-300'" viewBox="0 0 20 20" fill="currentColor">
                                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                              </svg>
                            </span>
                          </span>
                        </div>
                        <p class="text-sm text-gray-900 whitespace-pre-line">{{ evolution.notes }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useStudentsStore } from '@/stores/students';
import { useAuthStore } from '@/stores/auth';
import { useEvolutionStore } from '@/stores/evolution';

const router = useRouter();
const studentsStore = useStudentsStore();
const authStore = useAuthStore();
const evolutionStore = useEvolutionStore();

// State
const loading = ref(false);
const error = ref(null);
const students = ref([]);
const selectedStudentId = ref('');
const showAddForm = ref(false);
const saving = ref(false);
const loadingEvolutions = ref(false);
const evolutionError = ref(null);
const studentEvolutions = ref([]);

// New evolution data
const newEvolution = ref({
  date: new Date().toISOString().split('T')[0],
  category: '',
  rating: 3,
  notes: ''
});

// Fetch students for the current professor
const fetchStudents = async () => {
  try {
    loading.value = true;
    error.value = null;
    const professorStudents = await studentsStore.fetchStudents();
    students.value = professorStudents || [];
  } catch (err) {
    console.error('Error fetching students:', err);
    error.value = 'Erro ao carregar alunos: ' + err.message;
  } finally {
    loading.value = false;
  }
};

// Fetch evolutions for a specific student
const fetchStudentEvolutions = async (studentId) => {
  if (!studentId) return;
  
  try {
    loadingEvolutions.value = true;
    evolutionError.value = null;
    const evolutions = await evolutionStore.fetchStudentEvolutions(studentId);
    studentEvolutions.value = evolutions || [];
  } catch (err) {
    console.error('Error fetching evolutions:', err);
    evolutionError.value = 'Erro ao carregar evoluções: ' + err.message;
  } finally {
    loadingEvolutions.value = false;
  }
};

// Watch for changes in selected student
watch(selectedStudentId, (newValue) => {
  if (newValue) {
    fetchStudentEvolutions(newValue);
  } else {
    studentEvolutions.value = [];
  }
});

// Handle student selection change
const onStudentChange = () => {
  showAddForm.value = false;
  resetForm();
};

// Toggle the add form visibility
const toggleAddForm = () => {
  showAddForm.value = !showAddForm.value;
};

// Reset the form
const resetForm = () => {
  newEvolution.value = {
    date: new Date().toISOString().split('T')[0],
    category: '',
    rating: 3,
    notes: ''
  };
};

// Save a new evolution record
const saveEvolution = async () => {
  if (!selectedStudentId.value) return;
  
  try {
    saving.value = true;
    const evolutionData = {
      ...newEvolution.value,
      studentId: selectedStudentId.value,
      professorId: authStore.userId,
      createdAt: new Date().toISOString()
    };
    
    await evolutionStore.addEvolution(evolutionData);
    await fetchStudentEvolutions(selectedStudentId.value);
    
    showAddForm.value = false;
    resetForm();
  } catch (err) {
    console.error('Error saving evolution:', err);
    evolutionError.value = 'Erro ao salvar evolução: ' + err.message;
  } finally {
    saving.value = false;
  }
};

// Format date
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('pt-BR');
};

// Sort evolutions by date (most recent first)
const sortedEvolutions = computed(() => {
  return [...studentEvolutions.value].sort((a, b) => {
    return new Date(b.date) - new Date(a.date);
  });
});

// Get description based on rating value
const ratingDescription = computed(() => {
  switch(newEvolution.value.rating) {
    case 1: 
      return 'Necessita muita melhoria';
    case 2:
      return 'Ainda com dificuldades';
    case 3:
      return 'Desenvolvimento adequado';
    case 4:
      return 'Bom progresso';
    case 5:
      return 'Excelente evolução';
    default:
      return 'Selecione uma avaliação';
  }
});

// Get category label
const getCategoryLabel = (category) => {
  const categories = {
    'postura': 'Postura',
    'flexibilidade': 'Flexibilidade',
    'forca': 'Força',
    'equilibrio': 'Equilíbrio',
    'respiracao': 'Respiração',
    'outro': 'Outro'
  };
  return categories[category] || category;
};

// Get category class
const getCategoryClass = (category) => {
  const classes = {
    'postura': 'bg-blue-100 text-blue-800',
    'flexibilidade': 'bg-green-100 text-green-800',
    'forca': 'bg-red-100 text-red-800',
    'equilibrio': 'bg-purple-100 text-purple-800',
    'respiracao': 'bg-yellow-100 text-yellow-800',
    'outro': 'bg-gray-100 text-gray-800'
  };
  return classes[category] || 'bg-gray-100 text-gray-800';
};

onMounted(async () => {
  // Check if user is professor, if not redirect
  if (!authStore.isProfessor) {
    router.push('/login');
    return;
  }

  await fetchStudents();
});
</script>
