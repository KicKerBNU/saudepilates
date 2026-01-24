<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">{{ $t('admin.classSchedule') }}</h1>
      </div>
    </header>
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="px-4 py-6 sm:px-0">
        <Breadcrumb :items="breadcrumbItems" />
      </div>

      <div class="px-4 py-6 sm:px-0">
        <!-- Filter and Calendar Controls -->
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 gap-4">
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
          <div class="flex items-center gap-4">
            <!-- Student Filter -->
            <div class="w-full sm:w-64">
              <label for="studentFilter" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('admin.filterByStudent') }}</label>
              <select 
                id="studentFilter" 
                v-model="selectedStudentId" 
                @change="fetchAppointments"
                class="block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-lg shadow-sm"
              >
                <option value="">{{ $t('admin.allStudents') }}</option>
                <option v-for="student in students" :key="student.id" :value="student.id">
                  {{ student.name || student.displayName }}
                </option>
              </select>
            </div>
            <button 
              @click="today" 
              class="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 whitespace-nowrap"
            >
              {{ $t('professor.today') }}
            </button>
          </div>
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
                'min-h-[300px] p-3 relative',
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
                  {{ $t('professor.noClasses') }}
                </div>
                <div 
                  v-for="appointment in day.appointments" 
                  :key="appointment.id"
                  class="p-2 text-xs rounded-md border-l-4 shadow-sm mb-2"
                  :class="getAppointmentColor(appointment.type, appointment.present)"
                >
                  <div class="font-medium">
                    <div class="text-sm">{{ appointment.studentName }}</div>
                    <div class="text-xs text-gray-600 mt-1">{{ appointment.professorName }}</div>
                  </div>
                  <div class="mt-1">
                    <span class="font-semibold">{{ formatTimeRange(appointment.time, appointment.duration) }}</span>
                  </div>
                  <div v-if="appointment.present !== null && appointment.present !== undefined" class="mt-1">
                    <span v-if="appointment.present" class="text-green-600 text-xs">✓ {{ $t('common.present') }}</span>
                    <span v-else class="text-red-600 text-xs">✗ {{ $t('common.absent') }}</span>
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
import { ref, computed, onMounted, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';
import { useScheduleStore } from '../../stores/schedule';
import { useStudentsStore } from '../../stores/students';
import Breadcrumb from '@/components/Breadcrumb.vue';
import { 
  startOfWeek, 
  endOfWeek, 
  addDays, 
  format, 
  isSameDay, 
  parseISO,
  add
} from 'date-fns';
import { ptBR, enUS } from 'date-fns/locale';
import esLocale from 'date-fns/locale/es';
import frLocale from 'date-fns/locale/fr';

const { t, locale } = useI18n();
const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const scheduleStore = useScheduleStore();
const studentsStore = useStudentsStore();

// State
const loading = ref(false);
const currentDate = ref(new Date());
const selectedStudentId = ref('');
const students = ref([]);
const appointments = ref([]);

// Breadcrumb
const breadcrumbItems = computed(() => {
  const path = route.path;
  const segments = path.split('/').filter(Boolean);
  
  return segments.map((segment, index) => {
    const path = '/' + segments.slice(0, index + 1).join('/');
    const name = segment.charAt(0).toUpperCase() + segment.slice(1);
    return { name, path };
  });
});

// Week days calculation
const weekDays = computed(() => {
  const days = [];
  const dayOfWeek = currentDate.value.getDay();
  const firstDay = new Date(currentDate.value);
  firstDay.setDate(currentDate.value.getDate() - dayOfWeek);
  
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  const dayNames = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
  const shortDayNames = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
  
  // Map day names based on locale
  const localeDayNames = {
    'pt': ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'],
    'en': ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
    'es': ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
    'fr': ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi']
  };
  
  const localeShortDayNames = {
    'pt': ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'],
    'en': ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
    'es': ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'],
    'fr': ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam']
  };
  
  const dayNamesList = localeDayNames[locale.value] || localeDayNames['pt'];
  const shortDayNamesList = localeShortDayNames[locale.value] || localeShortDayNames['pt'];
  
  for (let i = 0; i < 7; i++) {
    const date = new Date(firstDay);
    date.setDate(firstDay.getDate() + i);
    const isToday = isSameDay(date, today);
    
    // Get appointments for this day
    const dayAppointments = appointments.value.filter(appointment => {
      const appointmentDate = typeof appointment.date === 'string' 
        ? parseISO(appointment.date) 
        : new Date(appointment.date);
      
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
      dayName: shortDayNamesList[i],
      fullDayName: dayNamesList[i],
      isToday,
      appointments: dayAppointments
    });
  }
  
  return days;
});

// Get date-fns locale based on i18n locale
const dateFnsLocale = computed(() => {
  const localeMap = {
    'pt': ptBR,
    'en': enUS,
    'es': esLocale,
    'fr': frLocale
  };
  return localeMap[locale.value] || ptBR;
});

// Formatted date range for the week
const formattedWeekRange = computed(() => {
  if (weekDays.value.length === 0) return '';
  
  const firstDay = parseISO(weekDays.value[0].date);
  const lastDay = parseISO(weekDays.value[6].date);
  
  // If same month
  if (firstDay.getMonth() === lastDay.getMonth()) {
    return `${format(firstDay, 'd')} - ${format(lastDay, 'd')} ${t('common.of')} ${format(firstDay, 'MMMM yyyy', { locale: dateFnsLocale.value })}`;
  }
  
  // If different months
  return `${format(firstDay, 'd MMM', { locale: dateFnsLocale.value })} - ${format(lastDay, 'd MMM yyyy', { locale: dateFnsLocale.value })}`;
});

// Navigate to previous week
const previousWeek = () => {
  const newDate = new Date(currentDate.value);
  newDate.setDate(newDate.getDate() - 7);
  currentDate.value = newDate;
  fetchAppointments();
};

// Navigate to next week
const nextWeek = () => {
  const newDate = new Date(currentDate.value);
  newDate.setDate(newDate.getDate() + 7);
  currentDate.value = newDate;
  fetchAppointments();
};

// Navigate to today
const today = () => {
  currentDate.value = new Date();
  fetchAppointments();
};

// Format time range (e.g., "07:00 - 07:59")
const formatTimeRange = (timeString, durationMinutes) => {
  if (!timeString) {
    return '--:-- - --:--';
  }
  
  try {
    let startTime;
    if (typeof timeString === 'string') {
      if (/^\d{1,2}:\d{2}$/.test(timeString)) {
        const [hours, minutes] = timeString.split(':').map(Number);
        startTime = new Date();
        startTime.setHours(hours, minutes, 0, 0);
      } else {
        startTime = parseISO(timeString);
      }
    } else if (timeString instanceof Date) {
      startTime = timeString;
    } else {
      startTime = new Date(timeString);
    }

    if (isNaN(startTime.getTime())) {
      console.warn('Invalid startTimeStr for formatTimeRange:', timeString);
      return 'Horário inválido';
    }

    const endTime = add(startTime, { minutes: durationMinutes || 60 });
    const localeMap = {
      'pt': 'pt-BR',
      'en': 'en-US',
      'es': 'es-ES',
      'fr': 'fr-FR'
    };
    const currentLocale = localeMap[locale.value] || 'pt-BR';

    return `${format(startTime, 'HH:mm', { locale: dateFnsLocale.value })} - ${format(endTime, 'HH:mm', { locale: dateFnsLocale.value })}`;
  } catch (error) {
    console.error('Error formatting time range:', error, timeString);
    return 'Erro de horário';
  }
};

// Get color class based on appointment type and attendance
const getAppointmentColor = (type, present) => {
  // If attendance is marked, use attendance colors
  if (present === true) {
    return 'border-green-500 bg-green-50 text-green-800';
  } else if (present === false) {
    return 'border-red-500 bg-red-50 text-red-800';
  }
  
  // Otherwise use type-based colors
  const colors = {
    individual: 'border-green-500 bg-green-50 text-green-800',
    group: 'border-blue-500 bg-blue-50 text-blue-800',
    assessment: 'border-purple-500 bg-purple-50 text-purple-800',
    scheduled: 'border-yellow-500 bg-yellow-50 text-yellow-800',
    default: 'border-gray-300 bg-gray-50 text-gray-800'
  };
  
  return colors[type] || colors.default;
};

// Fetch students
const fetchStudents = async () => {
  try {
    const fetchedStudents = await authStore.getUsersByCompany('student');
    students.value = fetchedStudents.filter(s => s.isActive !== false);
  } catch (error) {
    console.error('Error fetching students:', error);
    students.value = [];
  }
};

// Fetch appointments for the current week
const fetchAppointments = async () => {
  try {
    loading.value = true;
    
    if (!authStore.companyId) {
      console.error('No company ID found');
      return;
    }
    
    // Get first and last day of the week
    const dayOfWeek = currentDate.value.getDay();
    const firstDay = new Date(currentDate.value);
    firstDay.setDate(currentDate.value.getDate() - dayOfWeek);
    
    const lastDay = new Date(firstDay);
    lastDay.setDate(firstDay.getDate() + 6);
    
    // Fetch appointments for the current week
    const fetchedAppointments = await scheduleStore.fetchCompanySchedule(
      authStore.companyId,
      firstDay,
      lastDay,
      selectedStudentId.value || null,
      null
    );
    
    // Also fetch scheduled classes to get attendance status
    const { collection, query, where, getDocs } = await import('firebase/firestore');
    const { db } = await import('../../firebase/config');
    
    let classesQuery;
    if (selectedStudentId.value) {
      classesQuery = query(
        collection(db, 'scheduledClasses'),
        where('studentId', '==', selectedStudentId.value)
      );
    } else {
      classesQuery = query(collection(db, 'scheduledClasses'));
    }
    
    const classesSnapshot = await getDocs(classesQuery);
    const scheduledClasses = classesSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    
    // Merge attendance status
    appointments.value = fetchedAppointments.map(appointment => {
      const classItem = scheduledClasses.find(item => item.id === appointment.id);
      return {
        ...appointment,
        present: classItem?.present ?? null
      };
    });
    
  } catch (error) {
    console.error('Error fetching appointments:', error);
  } finally {
    loading.value = false;
  }
};

// Initialize
onMounted(async () => {
  if (!authStore.isAdmin) {
    router.push('/');
    return;
  }
  
  await fetchStudents();
  await fetchAppointments();
});
</script>
