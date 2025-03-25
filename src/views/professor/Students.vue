<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">Meus Alunos</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="mb-4">
        <Breadcrumb :items="breadcrumbItems" />
      </div>

      <div class="px-4 sm:px-0">
        <div class="bg-white shadow overflow-hidden sm:rounded-lg">
          <div v-if="loading" class="p-4 text-center">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600 mx-auto"></div>
          </div>
          
          <div v-else-if="error" class="p-4 text-center text-red-600">
            {{ error }}
          </div>
          
          <div v-else>
            <ul role="list" class="divide-y divide-gray-200">
              <li v-if="sortedStudents.length === 0" class="px-6 py-4 text-gray-500">
                Nenhum aluno encontrado.
              </li>
              <li v-for="student in sortedStudents" :key="student.id" class="px-4 py-4 sm:px-6">
                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <div class="min-w-0 flex-1 px-4 md:grid md:grid-cols-2 md:gap-4">
                      <div>
                        <p class="text-sm font-medium text-indigo-600 truncate">{{ student.name }}</p>
                        <p class="mt-2 flex items-center text-sm text-gray-500">
                          <span class="truncate">{{ student.email }}</span>
                        </p>
                      </div>
                      <div class="hidden md:block">
                        <div>
                          <p class="text-sm text-gray-900">
                            Telefone: {{ student.phone }}
                          </p>
                          <p class="mt-2 text-sm text-gray-500">
                            Plano: {{ student.planId && !student.plan ? 'Carregando plano...' : (student.plan ? `${student.plan.title} (${student.plan.sessionsPerWeek}x/semana)` : 'Sem plano') }}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="ml-5 flex-shrink-0">
                  <span v-if="student.planId && !student.plan" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">
                    Carregando...
                  </span>
                  <span v-else-if="student.plan" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-indigo-100 text-indigo-800">
                    {{ student.plan.title }}
                  </span>
                  <span v-else class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">
                    Sem plano
                  </span>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useStudentsStore } from '@/stores/students';
import { useAuthStore } from '@/stores/auth';
import Breadcrumb from '@/components/Breadcrumb.vue';

const router = useRouter();
const route = useRoute();
const studentsStore = useStudentsStore();
const authStore = useAuthStore();

const students = ref([]);
const loading = ref(true);
const error = ref(null);

// Breadcrumb items
const breadcrumbItems = computed(() => {
  return [
    { name: 'Professor', path: '/professor' },
    { name: 'Meus Alunos', path: '/professor/students' }
  ];
});

const sortedStudents = computed(() => {
  return [...students.value].sort((a, b) => {
    // First sort by plan (students with plans first)
    if (a.plan && !b.plan) return -1;
    if (!a.plan && b.plan) return 1;
    
    // Then sort by name
    return a.name.localeCompare(b.name);
  });
});

const fetchStudents = async () => {
  try {
    loading.value = true;
    error.value = null;
    const professorStudents = await studentsStore.fetchStudents();
    
    if (!professorStudents || professorStudents.length === 0) {
      students.value = [];
      return;
    }
    
    // For any student with planId but no plan data, fetch plan directly
    const studentsWithPlans = await Promise.all(professorStudents.map(async (student) => {
      if (student.planId && (!student.plan || !student.plan.title)) {
        try {
          // Directly fetch the plan using planId
          const plan = await studentsStore.fetchPlanById(student.planId);
          if (plan) {
            return { ...student, plan };
          }
        } catch (err) {
          console.error(`Error fetching plan for student ${student.id}:`, err);
        }
      }
      return student;
    }));
    
    students.value = studentsWithPlans;
  } catch (err) {
    console.error('Error fetching students:', err);
    error.value = 'Erro ao carregar alunos: ' + err.message;
  } finally {
    loading.value = false;
  }
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
