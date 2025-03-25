<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">Agenda de Aulas</h1>
      </div>
    </header>
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="px-4 py-6 sm:px-0">
        <Breadcrumb :items="breadcrumbItems" />
      </div>

      <div class="px-4 py-6 sm:px-0">
        <!-- Calendar Controls -->
        <div class="flex justify-between items-center mb-6">
          <div class="flex items-center">
            <button @click="previousWeek" class="p-2 rounded-full hover:bg-gray-200">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <h2 class="text-xl font-semibold text-gray-800 mx-4">{{ formattedWeekRange }}</h2>
            <button @click="nextWeek" class="p-2 rounded-full hover:bg-gray-200">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
          <button 
            @click="today" 
            class="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Hoje
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="flex justify-center items-center h-64">
          <svg class="animate-spin h-10 w-10 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </div>

        <!-- Weekly Calendar -->
        <div v-else class="bg-white rounded-lg shadow overflow-hidden">
          <!-- Calendar Header -->
          <div class="grid grid-cols-7 gap-px bg-gray-200 border-b">
            <div 
              v-for="(day, index) in weekDays" 
              :key="index" 
              class="py-2 text-center bg-white"
            >
              <div 
                class="text-sm font-semibold" 
                :class="{
                  'text-gray-700': index !== 0 && index !== 6,
                  'text-red-500': index === 0 || index === 6
                }"
              >
                {{ day.dayName }}
              </div>
              <div 
                :class="[
                  'text-sm font-medium mt-1 w-7 h-7 mx-auto flex items-center justify-center rounded-full',
                  day.isToday ? 'bg-indigo-600 text-white' : (index === 0 || index === 6) ? 'text-red-500' : 'text-gray-900'
                ]"
              >
                {{ day.dayNumber }}
              </div>
            </div>
          </div>

          <!-- Calendar Days -->
          <div class="grid grid-cols-7 gap-px bg-gray-200">
            <div 
              v-for="(day, index) in weekDays" 
              :key="index" 
              :class="[
                'min-h-[300px] p-3',
                { 'bg-indigo-50': day.isToday },
                { 'bg-white': !day.isToday && index !== 0 && index !== 6 },
                { 'bg-red-50': !day.isToday && (index === 0 || index === 6) }
              ]"
            >
              <!-- Appointments for this day -->
              <div class="space-y-2 overflow-y-auto pr-1 h-full">
                <div 
                  v-if="day.appointments.length === 0"
                  class="text-xs text-gray-400 italic text-center mt-2"
                >
                  Sem aulas
                </div>
                <div 
                  v-for="appointment in day.appointments" 
                  :key="appointment.id"
                  class="p-2 text-xs rounded-md border-l-4 shadow-sm mb-2"
                  :class="getAppointmentColor(appointment.type)"
                >
                  <div class="font-medium">{{ appointment.studentName }}</div>
                  <div class="mt-1 flex justify-between">
                    <span class="font-semibold">{{ formatTime(appointment.time) }}</span>
                    <span class="text-gray-600">{{ formatDuration(appointment.duration) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { useScheduleStore } from '../../stores/schedule';
import { startOfMonth, endOfMonth, setMonth, isSameDay, format, parseISO, addDays, subDays, startOfWeek, endOfWeek } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { dateToFirebaseTimestamp, firebaseTimestampToLocalDate, formatDateYYYYMMDD } from '../../utils/dateUtils';
import Breadcrumb from '@/components/Breadcrumb.vue';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const scheduleStore = useScheduleStore();

// Breadcrumb items
const breadcrumbItems = computed(() => {
  return [
    { name: 'Professor', path: '/professor' },
    { name: 'Agenda', path: '/professor/schedule' }
  ];
});

// Calendar state
const currentDate = ref(new Date());
const loading = ref(true);
const appointments = ref([]);

// Weekly calendar calculations
const weekDays = computed(() => {
  const days = [];
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  // Get the start of the week (Sunday) for the current date
  const weekStart = new Date(currentDate.value);
  const dayOfWeek = weekStart.getDay(); // 0 = Sunday, 6 = Saturday
  weekStart.setDate(weekStart.getDate() - dayOfWeek); // Go back to Sunday
  
  // Create array of 7 days (Sunday to Saturday)
  const dayNames = ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'];
  const shortDayNames = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
  
  for (let i = 0; i < 7; i++) {
    const date = new Date(weekStart);
    date.setDate(weekStart.getDate() + i);
    
    // Check if this day is today
    const isToday = isSameDay(date, today);
    
    // Get appointments for this day using date-fns
    const dayAppointments = appointments.value.filter(appointment => {
      // Parse the appointment date
      const appointmentDate = typeof appointment.date === 'string' 
        ? parseISO(appointment.date) 
        : new Date(appointment.date);
      
      // Compare dates using date-fns
      return isSameDay(appointmentDate, date);
    });
    
    // Sort appointments by time
    dayAppointments.sort((a, b) => {
      const timeA = a.time || '';
      const timeB = b.time || '';
      return timeA.localeCompare(timeB);
    });
    
    days.push({
      date: date.toISOString(),
      dayNumber: date.getDate(),
      dayName: shortDayNames[i],
      fullDayName: dayNames[i],
      isToday,
      appointments: dayAppointments
    });
  }
  
  return days;
});

// Formatted date range for the week
const formattedWeekRange = computed(() => {
  if (weekDays.value.length === 0) return '';
  
  const firstDay = parseISO(weekDays.value[0].date);
  const lastDay = parseISO(weekDays.value[6].date);
  
  // If same month
  if (firstDay.getMonth() === lastDay.getMonth()) {
    return `${format(firstDay, 'd')} - ${format(lastDay, 'd')} de ${format(firstDay, 'MMMM yyyy', { locale: ptBR })}`;
  }
  
  // If different months
  return `${format(firstDay, 'd MMM', { locale: ptBR })} - ${format(lastDay, 'd MMM yyyy', { locale: ptBR })}`;
});

// Navigate to previous week
const previousWeek = () => {
  const newDate = new Date(currentDate.value);
  newDate.setDate(newDate.getDate() - 7); // Go back 7 days
  currentDate.value = newDate;
  fetchAppointments();
};

// Navigate to next week
const nextWeek = () => {
  const newDate = new Date(currentDate.value);
  newDate.setDate(newDate.getDate() + 7); // Go forward 7 days
  currentDate.value = newDate;
  fetchAppointments();
};

// Navigate to today
const today = () => {
  currentDate.value = new Date();
  fetchAppointments();
};

// Format appointment time (HH:MM)
const formatTime = (timeString) => {
  // Handle various time formats
  if (!timeString) {
    return '--:--';
  }
  
  // If it's just a time string like "14:30" without a date
  if (typeof timeString === 'string' && timeString.length <= 5 && timeString.includes(':')) {
    return timeString;
  }
  
  try {
    const time = new Date(timeString);
    if (isNaN(time.getTime())) {
      // If not a valid date, try to parse it as a time string
      if (typeof timeString === 'string') {
        // Extract time from ISO string if possible
        const timeMatch = timeString.match(/T(\d{2}:\d{2})/);
        if (timeMatch && timeMatch[1]) {
          return timeMatch[1];
        }
      }
      return '--:--';
    }
    return time.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  } catch (error) {
    console.error('Error formatting time:', error);
    return '--:--';
  }
};

// Format duration (e.g., "60 min")
const formatDuration = (minutes) => {
  if (!minutes || isNaN(minutes)) return '';
  return `${minutes} min`;
};

// Get color class based on appointment type
const getAppointmentColor = (type) => {
  const colors = {
    individual: 'border-green-500 bg-green-50 text-green-800',
    group: 'border-blue-500 bg-blue-50 text-blue-800',
    assessment: 'border-purple-500 bg-purple-50 text-purple-800',
    scheduled: 'border-yellow-500 bg-yellow-50 text-yellow-800',
    default: 'border-gray-300 bg-gray-50 text-gray-800'
  };
  
  return colors[type] || colors.default;
};

// Fetch appointments for the current week
const fetchAppointments = async () => {
  try {
    loading.value = true;
    
    // Get first and last day of the week
    // Start on Sunday, end on Saturday
    const dayOfWeek = currentDate.value.getDay();
    const firstDay = new Date(currentDate.value);
    firstDay.setDate(currentDate.value.getDate() - dayOfWeek); // Go to Sunday
    
    const lastDay = new Date(firstDay);
    lastDay.setDate(firstDay.getDate() + 6); // Go to Saturday
    
    // Fetch appointments for the current week only
    appointments.value = await scheduleStore.fetchProfessorSchedule(
      authStore.userId,
      firstDay, 
      lastDay
    );
  } catch (error) {
    console.error('Error fetching appointments:', error);
  } finally {
    loading.value = false;
  }
};

// Initialize the calendar
onMounted(async () => {
  // Check if user is professor, if not redirect
  if (!authStore.isProfessor) {
    router.push('/login');
    return;
  }
  
  await fetchAppointments();
});
</script>
