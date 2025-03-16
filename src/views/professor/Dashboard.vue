<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">Dashboard do Professor</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Dashboard Stats Overview -->
      <div class="px-4 py-6 sm:px-0">
        <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
          <!-- Monthly Earnings -->
          <div class="bg-white overflow-hidden shadow rounded-lg flex flex-col" style="height: 100%;">
            <div class="px-4 py-5 sm:p-6">
              <!-- Loading state -->
              <div v-if="isLoadingEarnings" class="py-2">
                <div class="flex items-center">
                  <div class="flex-shrink-0 bg-gray-200 rounded-md p-3 animate-pulse">
                    <div class="h-6 w-6"></div>
                  </div>
                  <div class="ml-5 w-0 flex-1">
                    <div class="h-5 bg-gray-200 rounded w-1/2 animate-pulse mb-4"></div>
                    <div class="h-8 bg-gray-200 rounded w-3/4 animate-pulse mb-3"></div>
                    <div class="h-4 bg-gray-200 rounded w-1/2 animate-pulse mb-2"></div>
                    <div class="h-4 bg-gray-200 rounded w-2/3 animate-pulse"></div>
                  </div>
                </div>
              </div>
              
              <!-- Content when loaded -->
              <div v-else class="flex items-center">
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
                    <dd class="mt-1">
                      <div class="text-2xl font-semibold text-gray-900">
                        R$ {{ Number(monthlyEarnings).toFixed(2) }}
                      </div>
                      <div class="mt-2">
                        <span class="text-sm font-medium text-gray-500">Comissão:</span>
                        <span class="ml-1 text-sm text-gray-900">{{ commission }}%</span>
                      </div>
                      <div class="mt-1">
                        <span class="text-sm font-medium text-gray-500">Potencial Máximo:</span>
                        <span class="ml-1 text-sm text-green-600">R$ {{ potentialEarnings.toFixed(2) }}</span>
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
          <div class="bg-white overflow-hidden shadow rounded-lg flex flex-col" style="height: 100%;">
            <div class="px-4 py-5 sm:p-6">
              <!-- Loading state -->
              <div v-if="isLoadingStudents" class="py-2">
                <div class="flex items-center">
                  <div class="flex-shrink-0 bg-gray-200 rounded-md p-3 animate-pulse">
                    <div class="h-6 w-6"></div>
                  </div>
                  <div class="ml-5 w-0 flex-1">
                    <div class="h-5 bg-gray-200 rounded w-1/2 animate-pulse mb-4"></div>
                    <div class="h-8 bg-gray-200 rounded w-1/4 animate-pulse"></div>
                  </div>
                </div>
              </div>
              
              <!-- Content when loaded -->
              <div v-else class="flex items-center">
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
            <div class="bg-gray-50 px-4 py-4 sm:px-6 mt-auto">
              <div class="text-sm">
                <router-link to="/professor/students" class="font-medium text-indigo-600 hover:text-indigo-500">
                  Ver todos os alunos <span aria-hidden="true">&rarr;</span>
                </router-link>
              </div>
            </div>
          </div>

          <!-- Today's Classes -->
          <div class="bg-white overflow-hidden shadow rounded-lg flex flex-col" style="height: 100%;">
            <div class="px-4 py-5 sm:p-6">
              <!-- Loading state -->
              <div v-if="isLoadingClasses" class="py-2">
                <div class="flex items-center">
                  <div class="flex-shrink-0 bg-gray-200 rounded-md p-3 animate-pulse">
                    <div class="h-6 w-6"></div>
                  </div>
                  <div class="ml-5 w-0 flex-1">
                    <div class="h-5 bg-gray-200 rounded w-1/2 animate-pulse mb-4"></div>
                    <div class="h-8 bg-gray-200 rounded w-1/4 animate-pulse"></div>
                  </div>
                </div>
              </div>
              
              <!-- Content when loaded -->
              <div v-else class="flex items-center">
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
            <div class="bg-gray-50 px-4 py-4 sm:px-6 mt-auto">
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
                          Plano: {{ student.planId && !student.plan ? 'Carregando...' : (student.plan?.title || 'Sem plano') }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="ml-5 flex-shrink-0">
                  <span v-if="student.planId && !student.plan" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">
                    Carregando...
                  </span>
                  <span v-else-if="student.plan" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-indigo-100 text-indigo-800">
                    {{ student.plan.title }} ({{ student.plan.sessionsPerWeek }}x/semana)
                  </span>
                  <span v-else class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">
                    Sem plano
                  </span>
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
          <!-- Attendance Control Button -->
          <router-link to="/professor/attendance-control" class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-blue-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
                  </svg>
                </div>
                <div class="ml-5">
                  <h3 class="text-lg font-medium text-gray-900">Controle de Presença</h3>
                  <p class="mt-1 text-sm text-gray-500">Gerenciar a presença de seus alunos e agendar aulas</p>
                </div>
              </div>
            </div>
          </router-link>
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
          
          <!-- Schedule Button -->
          <router-link to="/professor/schedule" class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-purple-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <h3 class="text-lg font-medium text-gray-900">Agenda</h3>
                  <p class="mt-1 text-sm text-gray-500">
                    Visualize sua agenda completa de aulas com todos os alunos.
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
import { ref, onMounted, onActivated } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { useStudentsStore } from '../../stores/students';
import { usePaymentsStore } from '../../stores/payments';
import { useAttendanceStore } from '../../stores/attendance';
import { useScheduleStore } from '../../stores/schedule';
import { isSameDay, parseISO } from 'date-fns';
import { doc, getDoc, setDoc, updateDoc, collection, query, where, getDocs } from 'firebase/firestore';
import { db } from '../../firebase/config';

const router = useRouter();
const authStore = useAuthStore();
const studentsStore = useStudentsStore();
const paymentsStore = usePaymentsStore();
const attendanceStore = useAttendanceStore();
const scheduleStore = useScheduleStore();

const monthlyEarnings = ref(0); // Make sure this is initialized as a number
const potentialEarnings = ref(0);
const commission = ref(0); 
const totalStudents = ref(0);
const todaysClasses = ref(0);
const students = ref([]);
const todayAttendance = ref([]);
const error = ref(null);

// Independent loading states for each card
const isLoadingEarnings = ref(true);
const isLoadingStudents = ref(true);
const isLoadingClasses = ref(true);

// Check if user is professor, if not redirect
onMounted(async () => {
  
  if (!authStore.isAuthenticated) {
    await new Promise(resolve => setTimeout(resolve, 500));
  }
  
  if (!authStore.isProfessor) {
    router.push('/login');
    return;
  }

  // Fetch data for dashboard
  await fetchEarnings();
  await fetchStudents();
  await fetchTodayAttendance();
  
  // Note: calculateTodaysClasses is no longer needed here
  // as fetchTodayAttendance now sets todaysClasses directly
});

// Refresh data when component is reactivated/revisited (e.g., after marking attendance)
onActivated(async () => {

  if (authStore.isProfessor) {
    // Refresh data when coming back to the dashboard
    await fetchEarnings();
    await fetchTodayAttendance();
    // Note: calculateTodaysClasses is no longer needed
  }
});

const fetchEarnings = async () => {
  try {
    isLoadingEarnings.value = true;
    // Make sure we have userId
    if (!authStore.userId) {
      return;
    }
    
    
    // Try to get user document
    let userDoc = await getDoc(doc(db, 'users', authStore.userId));
    let userData = null;
    
    if (userDoc.exists()) {
      userData = userDoc.data();
      
      
      // Store the commission rate (default is 0%)
      commission.value = typeof userData.commission === 'number' ? userData.commission : 0;
      
      
      // Update Firestore if commission is not set
      if (typeof userData.commission !== 'number') {
      
        await updateDoc(doc(db, 'users', authStore.userId), {
          commission: 0
        });
      }
    } else {
      commission.value = 0;
      
      // Create a user document if it doesn't exist
      try {
        await setDoc(doc(db, 'users', authStore.userId), {
          email: authStore.user && authStore.user.email ? authStore.user.email : '',
          role: 'professor',
          commission: 0,
          createdAt: new Date().toISOString()
        });
      } catch (e) {
        console.error('Error creating user document:', e);
      }
    }

    // Force commission to a number
    commission.value = Number(commission.value) || 0;

    // Calculate actual earnings based on attendance - use improved store method
    try {
      // Directly use the attendance store's optimized calculation
      const earnings = await attendanceStore.calculateMonthlyEarnings(authStore.userId);
      monthlyEarnings.value = Number(earnings) || 0;
    } catch (err) {
      console.error('Error calculating monthly earnings:', err);
      monthlyEarnings.value = 0;
    }

    // Calculate potential earnings (if all students attended all classes)
    try {
      const professorStudents = await studentsStore.fetchStudents() || [];
      
      
      let potentialTotal = 0;
      
      // Calculate potential earnings for each student
      for (const student of professorStudents) {
        if (student.planId) {
          let planData = student.plan;
          
          // If plan data isn't loaded, try to fetch it
          if (!planData && student.planId) {
            try {
              planData = await studentsStore.fetchPlanById(student.planId);
            } catch (err) {
              console.error(`Failed to fetch plan for student ${student.id}:`, err);
            }
          }
          
          if (planData && planData.price && planData.sessionsPerWeek) {
            const classesPerMonth = planData.sessionsPerWeek * 4; // 4 weeks per month
            const pricePerClass = planData.price / classesPerMonth;
            const studentPotential = pricePerClass * classesPerMonth * (commission.value / 100);
            potentialTotal += studentPotential;
          }
        }
      }
      
      potentialEarnings.value = potentialTotal;
    } catch (err) {
      console.error('Error calculating potential earnings:', err);
      potentialEarnings.value = 0;
    }
  } catch (err) {
    console.error('Error in fetchEarnings:', err);
    error.value = 'Erro ao carregar ganhos: ' + err.message;
  } finally {
    isLoadingEarnings.value = false;
  }
};

const fetchStudents = async () => {
  try {
    isLoadingStudents.value = true;
    // Fetch students with their plan data
    const professorStudents = await studentsStore.fetchStudents();
    
    // Ensure plans are loaded for each student
    if (professorStudents && professorStudents.length > 0) {
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
      
      // Sort students by name
      students.value = studentsWithPlans.sort((a, b) => a.name.localeCompare(b.name));
    } else {
      // Sort students by name
      students.value = professorStudents.sort((a, b) => a.name.localeCompare(b.name));
    }
    
    totalStudents.value = students.value.length;
  } catch (error) {
    console.error('Error fetching students:', error);
    error.value = 'Erro ao carregar alunos: ' + error.message;
  } finally {
    isLoadingStudents.value = false;
  }
};

const fetchTodayAttendance = async () => {
  try {
    isLoadingClasses.value = true;
    
    // Get today's date (start and end of day)
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    
    // Fetch appointments scheduled for today
    const appointments = await scheduleStore.fetchProfessorSchedule(
      authStore.userId,
      today,
      tomorrow
    );
    
    // Save the appointments for today
    todayAttendance.value = appointments;
    
    // Update the todaysClasses count
    todaysClasses.value = appointments.length;
    
    
  } catch (error) {
    console.error('Error fetching today\'s classes:', error);
    error.value = 'Erro ao carregar aulas de hoje: ' + error.message;
  } finally {
    isLoadingClasses.value = false;
  }
};

const calculateTodaysClasses = () => {
  // This is no longer needed since we're fetching actual classes from the schedule
  // The count is updated directly in fetchTodayAttendance
  
  // Just in case fetchTodayAttendance failed, ensure we have a default value
  if (todaysClasses.value === 0 && todayAttendance.value.length > 0) {
    todaysClasses.value = todayAttendance.value.length;
  }
};

const formatTime = (dateString) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('pt-BR', {
    hour: '2-digit', 
    minute: '2-digit'
  }).format(date);
};

// Attendance methods removed

// Logout functionality removed - now handled in NavBar
</script>
