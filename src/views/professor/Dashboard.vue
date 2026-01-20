<template>
  <div class="min-h-screen bg-gray-100" 
       @touchstart="handleTouchStart" 
       @touchmove="handleTouchMove" 
       @touchend="handleTouchEnd">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">{{ $t('professor.dashboard') }}</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="px-4 py-6 sm:px-0">
        <Breadcrumb :items="breadcrumbItems" />
      </div>

      <!-- Dashboard Stats Overview -->
      <div class="px-4 py-6 sm:px-0">
        <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
          <!-- Today's Classes (Schedule) - Moved to Top -->
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
                <div class="flex-shrink-0 bg-indigo-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <dl>
                    <dt class="text-sm font-medium text-gray-500 truncate">
                      {{ $t('professor.todayClasses') }}
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
                  {{ $t('professor.schedule') }} <span aria-hidden="true">&rarr;</span>
                </router-link>
              </div>
            </div>
          </div>

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
                    <div class="h-4 bg-gray-200 rounded w-1/2 animate-pulse"></div>
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
                      {{ $t('professor.monthlyEarnings') }}
                    </dt>
                    <dd class="mt-1">
                      <div class="text-2xl font-semibold text-gray-900">
                        R$ {{ Number(monthlyEarnings).toFixed(2) }}
                      </div>
                      <div class="mt-2">
                        <span class="text-sm font-medium text-gray-500">{{ $t('admin.commission') }}:</span>
                        <span class="ml-1 text-sm text-gray-900">{{ commission }}%</span>
                      </div>
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-4 py-4 sm:px-6">
              <div class="text-sm">
                <router-link to="/professor/earnings" class="font-medium text-indigo-600 hover:text-indigo-500">
                  {{ $t('professor.earningsHistory') }} <span aria-hidden="true">&rarr;</span>
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
                      {{ $t('professor.myStudents') }}
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
                  {{ $t('professor.students') }} <span aria-hidden="true">&rarr;</span>
                </router-link>
              </div>
            </div>
          </div>

          <!-- Student Messages Card -->
          <div class="bg-white overflow-hidden shadow rounded-lg flex flex-col" style="height: 100%;">
            <div class="px-4 py-5 sm:p-6 flex-grow">
              <!-- Loading state -->
              <div v-if="isLoadingMessages" class="py-2">
                <div class="flex items-center">
                  <div class="flex-shrink-0 bg-gray-200 rounded-md p-3 animate-pulse">
                    <div class="h-6 w-6"></div>
                  </div>
                  <div class="ml-5 w-0 flex-1">
                    <div class="h-5 bg-gray-200 rounded w-1/2 animate-pulse mb-4"></div>
                    <div class="h-8 bg-gray-200 rounded w-3/4 animate-pulse mb-3"></div>
                    <div class="h-4 bg-gray-200 rounded w-1/2 animate-pulse"></div>
                  </div>
                </div>
              </div>
              
              <!-- Content when loaded -->
              <div v-else>
                <div class="flex items-center mb-4">
                  <div class="flex-shrink-0 bg-green-500 rounded-md p-3">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654z"/>
                    </svg>
                  </div>
                  <div class="ml-5 w-0 flex-1">
                    <dl>
                      <dt class="text-sm font-medium text-gray-500 truncate">
                        {{ $t('professor.studentMessages') }}
                      </dt>
                      <dd class="mt-1">
                        <div class="text-2xl font-semibold text-gray-900">
                          {{ unreadMessages }} <span class="text-sm text-gray-500">{{ $t('professor.unread') }}</span>
                        </div>
                      </dd>
                    </dl>
                  </div>
                </div>
                
                <!-- List of recent messages -->
                <div v-if="studentMessages.length > 0" class="mt-3 space-y-3 max-h-48 overflow-y-auto">
                  <div 
                    v-for="message in studentMessages.slice(0, 3)" 
                    :key="message.id" 
                    class="bg-gray-50 p-3 rounded-lg"
                    :class="{'border-l-4 border-green-500': !message.isRead}"
                  >
                    <div class="flex justify-between items-start mb-1">
                      <span class="text-sm font-medium text-gray-900">{{ message.studentName }}</span>
                      <span class="text-xs text-gray-500">{{ formatMessageTime(message.createdAt) }}</span>
                    </div>
                    <p class="text-sm text-gray-700 line-clamp-2">{{ message.text }}</p>
                    <div class="mt-2 flex justify-between items-center">
                      <button 
                        @click="markAsRead(message)" 
                        v-if="!message.isRead"
                        class="text-xs text-indigo-600 hover:text-indigo-800"
                      >
                        {{ $t('professor.markAsRead') }}
                      </button>
                      <button 
                        @click="replyToMessage(message)" 
                        class="text-xs text-green-600 hover:text-green-800 flex items-center"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="currentColor" viewBox="0 0 24 24">
                          <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654z"/>
                        </svg>
                        {{ $t('professor.reply') }}
                      </button>
                    </div>
                  </div>
                </div>
                
                <!-- No messages -->
                <div v-else class="text-center py-2 text-gray-500">
                  {{ $t('professor.noRecentMessages') }}
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-4 py-4 sm:px-6 mt-auto">
              <div class="text-sm">
                <router-link to="/professor/messages" class="font-medium text-indigo-600 hover:text-indigo-500">
                  {{ $t('professor.viewAllMessages') }} <span aria-hidden="true">&rarr;</span>
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Student List Section -->
      <div class="mt-8 px-4 sm:px-0">
        <h2 class="text-lg font-medium text-gray-900 mb-4">{{ $t('professor.recentStudents') }}</h2>
        <div class="bg-white shadow overflow-hidden sm:rounded-md">
          <ul role="list" class="divide-y divide-gray-200">
            <li v-if="students.length === 0" class="px-6 py-4 text-gray-500">
              {{ $t('professor.noStudents') }}
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
        <h2 class="text-lg font-medium text-gray-900 mb-4">{{ $t('professor.quickActions') }}</h2>
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <!-- Schedule Button - Moved to First Position -->
          <router-link to="/professor/schedule" class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50">
            <div class="px-4 py-5 sm:p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0 bg-indigo-500 rounded-md p-3">
                  <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                  <h3 class="text-lg font-medium text-gray-900">{{ $t('professor.schedule') }}</h3>
                  <p class="mt-1 text-sm text-gray-500">
                    {{ $t('professor.scheduleDesc') }}
                  </p>
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
                  <h3 class="text-lg font-medium text-gray-900">{{ $t('professor.manageStudents') }}</h3>
                  <p class="mt-1 text-sm text-gray-500">
                    {{ $t('professor.manageStudentsDesc') }}
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
                  <h3 class="text-lg font-medium text-gray-900">{{ $t('professor.studentEvolution') }}</h3>
                  <p class="mt-1 text-sm text-gray-500">
                    {{ $t('professor.studentEvolutionDesc') }}
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
import { ref, onMounted, onActivated, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';

const { t } = useI18n();
import { useStudentsStore } from '../../stores/students';
import { usePaymentsStore } from '../../stores/payments';
import { useAttendanceStore } from '../../stores/attendance';
import { useScheduleStore } from '../../stores/schedule';
import { collection, query, where, getDocs, updateDoc, doc, orderBy, limit, Timestamp, getDoc } from 'firebase/firestore';
import { db } from '../../firebase/config';
import Breadcrumb from '@/components/Breadcrumb.vue';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const studentsStore = useStudentsStore();
const paymentsStore = usePaymentsStore();
const attendanceStore = useAttendanceStore();
const scheduleStore = useScheduleStore();

const monthlyEarnings = ref(0);
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

// Student messages
const studentMessages = ref([]);
const isLoadingMessages = ref(false);
const unreadMessages = computed(() => {
  return studentMessages.value.filter(msg => !msg.isRead).length;
});

// Modal state
const showMessagesModal = ref(false);
const selectedMessage = ref(null);

// Breadcrumb items
const breadcrumbItems = computed(() => {
  const path = route.path;
  const segments = path.split('/').filter(Boolean);
  
  return segments.map((segment, index) => {
    const path = '/' + segments.slice(0, index + 1).join('/');
    const name = segment.charAt(0).toUpperCase() + segment.slice(1);
    return { name, path };
  });
});

// Touch event variables
const touchStartX = ref(0);
const touchEndX = ref(0);
const minSwipeDistance = 50; // Minimum distance for a swipe to be registered

// Touch event handlers
const handleTouchStart = (e) => {
  touchStartX.value = e.touches[0].clientX;
};

const handleTouchMove = (e) => {
  touchEndX.value = e.touches[0].clientX;
};

const handleTouchEnd = () => {
  const swipeDistance = touchEndX.value - touchStartX.value;
  
  // Check if it's a left swipe (negative distance) and meets minimum distance
  if (swipeDistance < -minSwipeDistance) {
    router.go(-1); // Go back one step in history
  }
};

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
  await fetchStudentMessages();
});

// Refresh data when component is activated
onActivated(async () => {
  if (authStore.isProfessor) {
    await fetchEarnings();
    await fetchStudents();
    await fetchTodayAttendance();
    await fetchStudentMessages();
  }
});

// Method to fetch professor's earnings
const fetchEarnings = async () => {
  isLoadingEarnings.value = true;
  try {
    // Get professor's base commission from their user profile
    commission.value = authStore.userProfile?.commission || 0;
    
    // Get ALL students assigned to the professor
    const studentsQuery = query(
      collection(db, 'users'),
      where('professorId', '==', authStore.userId),
      where('role', '==', 'student')
    );
    
    const studentsSnapshot = await getDocs(studentsQuery);
    const allStudents = studentsSnapshot.docs
      .filter(doc => doc.exists())
      .map(doc => ({
        id: doc.id,
        ...doc.data()
      }))
      .filter(student => student && student.planId);
    
    // Get all plans
    const planIds = [...new Set(allStudents.map(student => student.planId))];
    const plansData = await Promise.all(
      planIds.map(id => getDoc(doc(db, 'plans', id)))
    );
    
    const plansMap = {};
    plansData.forEach(doc => {
      if (doc.exists()) {
        plansMap[doc.id] = { id: doc.id, ...doc.data() };
      }
    });
    
    // Calculate total earnings (sum of ALL students' plan prices * commission)
    let total = 0;
    for (const student of allStudents) {
      if (student.planId) {
        const plan = plansMap[student.planId];
        if (plan && plan.price) {
          const price = Number(plan.price) || 0;
          const earning = price * (commission.value / 100);
          
          if (!isNaN(earning)) {
            total += earning;
          }
        }
      }
    }
    monthlyEarnings.value = total;
    potentialEarnings.value = total;
    
  } catch (error) {
    console.error('Error fetching earnings:', error);
    monthlyEarnings.value = 0;
    potentialEarnings.value = 0;
  } finally {
    isLoadingEarnings.value = false;
  }
};

// Method to fetch professor's students
const fetchStudents = async () => {
  isLoadingStudents.value = true;
  try {
    const fetchedStudents = await studentsStore.fetchStudents();
    students.value = fetchedStudents.filter(student => 
      student.professorId === authStore.userId || 
      !student.professorId
    );
    totalStudents.value = students.value.length;
  } catch (error) {
    console.error('Error fetching students:', error);
  } finally {
    isLoadingStudents.value = false;
  }
};

// Method to fetch today's attendance records
const fetchTodayAttendance = async () => {
  isLoadingClasses.value = true;
  try {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    
    const todayTimestamp = Timestamp.fromDate(today);
    const tomorrowTimestamp = Timestamp.fromDate(tomorrow);
    
    const classesRef = collection(db, 'scheduledClasses');
    const classesQuery = query(
      classesRef,
      where('professorId', '==', authStore.userId),
      where('date', '>=', todayTimestamp),
      where('date', '<', tomorrowTimestamp)
    );
    
    const classesSnapshot = await getDocs(classesQuery);
    const fetchedClasses = classesSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    
    todaysClasses.value = fetchedClasses.length;
    todayAttendance.value = fetchedClasses;
  } catch (error) {
    console.error('Error fetching today\'s attendance:', error);
  } finally {
    isLoadingClasses.value = false;
  }
};

// Method to fetch student messages
const fetchStudentMessages = async () => {
  isLoadingMessages.value = true;
  try {
    const messagesQuery = query(
      collection(db, 'studentMessages'),
      where('professorId', '==', authStore.userId),
      orderBy('createdAt', 'desc'),
      limit(20)
    );
    
    const messagesSnapshot = await getDocs(messagesQuery);
    studentMessages.value = messagesSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
  } catch (error) {
    console.error('Error fetching student messages:', error);
  } finally {
    isLoadingMessages.value = false;
  }
};

// Mark message as read
const markAsRead = async (message) => {
  try {
    await updateDoc(doc(db, 'studentMessages', message.id), {
      isRead: true
    });
    
    const index = studentMessages.value.findIndex(msg => msg.id === message.id);
    if (index !== -1) {
      studentMessages.value[index].isRead = true;
    }
  } catch (error) {
    console.error('Error marking message as read:', error);
  }
};

// Reply to a student message via WhatsApp
const replyToMessage = (message) => {
  const studentPhone = message.studentPhone || '';
  if (studentPhone) {
    const encodedReply = encodeURIComponent(`Resposta para sua mensagem: "${message.text.substring(0, 30)}..."`);
    window.open(`https://wa.me/${studentPhone.replace(/\D/g, '')}?text=${encodedReply}`, '_blank');
  } else {
    alert('Número de telefone do aluno não disponível');
  }
};

// Format message time
const formatMessageTime = (timestamp) => {
  if (!timestamp) return '';
  
  const date = timestamp instanceof Timestamp ? timestamp.toDate() : new Date(timestamp);
  const now = new Date();
  const diffInHours = Math.floor((now - date) / (1000 * 60 * 60));
  
  if (diffInHours < 24) {
    return `Hoje, ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  } else if (diffInHours < 48) {
    return `Ontem, ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  } else {
    return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;
  }
};

// Open the full messages modal
const openMessageModal = () => {
  showMessagesModal.value = true;
};
</script>