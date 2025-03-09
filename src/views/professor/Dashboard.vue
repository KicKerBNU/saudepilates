<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <h1 class="text-3xl font-bold text-gray-900">Dashboard do Professor</h1>
          <button @click="logout" class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md">
            Sair
          </button>
        </div>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Dashboard Stats Overview -->
      <div class="px-4 py-6 sm:px-0">
        <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
          <!-- Monthly Earnings -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-green-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">
                      Ganhos do Mês
                    </dt>
                    <dd class="flex items-baseline">
                      <div class="text-2xl font-semibold text-gray-900">
                        R$ {{ monthlyEarnings.toFixed(2) }}
                      </div>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-4 py-4 sm:px-6">
              <div class="text-sm">
                <router-link to="/professor/earnings" class="font-medium text-indigo-600 hover:text-indigo-500">
                  Ver histórico de ganhos <span aria-hidden="true">&rarr;</span>
                </router-link>
              </div>
            </div>
          </div>

          <!-- Students Count -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-indigo-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">
                      Meus Alunos
                    </dt>
                    <dd class="flex items-baseline">
                      <div class="text-2xl font-semibold text-gray-900">
                        {{ totalStudents }}
                      </div>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-4 py-4 sm:px-6">
              <div class="text-sm">
                <router-link to="/professor/students" class="font-medium text-indigo-600 hover:text-indigo-500">
                  Ver todos os alunos <span aria-hidden="true">&rarr;</span>
                </router-link>
              </div>
            </div>
          </div>

          <!-- Today's Classes -->
          <div class="bg-white overflow-hidden shadow rounded-lg">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-purple-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">
                      Aulas Hoje
                    </dt>
                    <dd class="flex items-baseline">
                      <div class="text-2xl font-semibold text-gray-900">
                        {{ todaysClasses }}
                      </div>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-4 py-4 sm:px-6">
              <div class="text-sm">
                <router-link to="/professor/schedule" class="font-medium text-indigo-600 hover:text-indigo-500">
                  Ver agenda completa <span aria-hidden="true">&rarr;</span>
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Student List Section -->
      <div class="mt-8 px-4 sm:px-0">
        <h2 class="text-lg font-medium text-gray-900 mb-4">Alunos Recentes</h2>
        <div class="bg-white shadow overflow-hidden sm:rounded-md">
          <ul role="list" class="divide-y divide-gray-200">
            <li v-if="students.length === 0" class="px-6 py-4 text-gray-500">
              Nenhum aluno encontrado.
            </li>
            <li v-for="student in students.slice(0, 5)" :key="student.id" class="px-4 py-4 sm:px-6">
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
                        <p class="mt-2 flex items-center text-sm text-gray-500">
                          Status: {{ student.isActive ? 'Ativo' : 'Inativo' }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="ml-5 flex-shrink-0">
                  <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                    :class="student.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                    {{ student.isActive ? 'Ativo' : 'Inativo' }}
                  </span>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>

      <!-- Today's Attendance Section -->
      <div class="mt-8 px-4 sm:px-0">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-lg font-medium text-gray-900">Controle de Presença - Hoje</h2>
          <button @click="openAttendanceModal" class="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md text-sm">
            Registrar Presença
          </button>
        </div>
        
        <div class="bg-white shadow overflow-hidden sm:rounded-md">
          <ul role="list" class="divide-y divide-gray-200">
            <li v-if="todayAttendance.length === 0" class="px-6 py-4 text-gray-500">
              Nenhuma presença registrada hoje.
            </li>
            <li v-for="attendance in todayAttendance" :key="attendance.id" class="px-4 py-4 sm:px-6">
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <div class="min-w-0 flex-1 px-4">
                    <p class="text-sm font-medium text-indigo-600 truncate">{{ attendance.studentName }}</p>
                    <p class="mt-2 flex items-center text-sm text-gray-500">
                      <span class="truncate">{{ formatTime(attendance.date) }}</span>
                    </p>
                  </div>
                </div>
                <div class="ml-5 flex space-x-2">
                  <button @click="toggleAttendanceStatus(attendance.id, !attendance.present)" 
                    class="px-2 py-1 rounded-md text-xs font-medium"
                    :class="attendance.present ? 'bg-green-100 text-green-800 hover:bg-green-200' : 'bg-red-100 text-red-800 hover:bg-red-200'">
                    {{ attendance.present ? 'Presente' : 'Ausente' }}
                  </button>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="mt-8 px-4 sm:px-0">
        <h2 class="text-lg font-medium text-gray-900 mb-4">Ações Rápidas</h2>
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <router-link to="/professor/students" class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-indigo-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <h3 class="text-lg font-medium text-gray-900">Gerenciar Alunos</h3>
                  <p class="mt-1 text-sm text-gray-500">
                    Acesse a lista completa de alunos e gerencie suas informações.
                  </p>
                </div>
              </div>
            </div>
          </router-link>

          <router-link to="/professor/attendance" class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-green-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <h3 class="text-lg font-medium text-gray-900">Controle de Presenças</h3>
                  <p class="mt-1 text-sm text-gray-500">
                    Registre a presença dos alunos e visualize o histórico.
                  </p>
                </div>
              </div>
            </div>
          </router-link>

          <router-link to="/professor/evolution" class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-purple-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <h3 class="text-lg font-medium text-gray-900">Evolução dos Alunos</h3>
                  <p class="mt-1 text-sm text-gray-500">
                    Acompanhe o progresso e cadastre a evolução dos alunos.
                  </p>
                </div>
              </div>
            </div>
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { useStudentsStore } from '../../stores/students';
import { usePaymentsStore } from '../../stores/payments';
import { useAttendanceStore } from '../../stores/attendance';

const router = useRouter();
const authStore = useAuthStore();
const studentsStore = useStudentsStore();
const paymentsStore = usePaymentsStore();
const attendanceStore = useAttendanceStore();

const monthlyEarnings = ref(0);
const totalStudents = ref(0);
const todaysClasses = ref(0);
const students = ref([]);
const todayAttendance = ref([]);

// Check if user is professor, if not redirect
onMounted(async () => {
  if (!authStore.isProfessor) {
    router.push('/login');
    return;
  }

  // Fetch data for dashboard
  await Promise.all([
    fetchEarnings(),
    fetchStudents(),
    fetchTodayAttendance()
  ]);
  
  // Calculate today's classes
  calculateTodaysClasses();
});

const fetchEarnings = async () => {
  try {
    await paymentsStore.fetchProfessorPayments(authStore.userId);
    
    // Calculate monthly earnings (current month)
    const currentMonth = new Date().getMonth();
    const currentYear = new Date().getFullYear();
    
    monthlyEarnings.value = paymentsStore.currentMonthEarnings(authStore.userId);
  } catch (error) {
    console.error('Error fetching earnings:', error);
  }
};

const fetchStudents = async () => {
  try {
    const professorStudents = await studentsStore.fetchStudents();
    students.value = professorStudents;
    totalStudents.value = professorStudents.length;
  } catch (error) {
    console.error('Error fetching students:', error);
  }
};

const fetchTodayAttendance = async () => {
  try {
    const today = new Date().toISOString().split('T')[0];
    await attendanceStore.fetchAttendanceRecords(null, authStore.userId, today, today);
    todayAttendance.value = attendanceStore.attendanceRecords;
  } catch (error) {
    console.error('Error fetching attendance:', error);
  }
};

const calculateTodaysClasses = () => {
  // This would typically be calculated based on the schedule
  // For now, we'll just use a simple calculation based on active students
  const activeStudents = students.value.filter(student => student.isActive);
  todaysClasses.value = Math.min(activeStudents.length, 10); // Assuming max 10 classes per day
};

const formatTime = (dateString) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('pt-BR', {
    hour: '2-digit', 
    minute: '2-digit'
  }).format(date);
};

const openAttendanceModal = () => {
  // This would typically open a modal to register attendance
  // For now, we'll just navigate to the attendance page
  router.push('/professor/attendance/new');
};

const toggleAttendanceStatus = async (attendanceId, present) => {
  try {
    await attendanceStore.updateAttendanceRecord(attendanceId, { present });
    // Refresh attendance data
    fetchTodayAttendance();
  } catch (error) {
    console.error('Error updating attendance:', error);
  }
};

const logout = async () => {
  try {
    await authStore.logout();
    router.push('/login');
  } catch (error) {
    console.error('Error logging out:', error);
  }
};
</script>
