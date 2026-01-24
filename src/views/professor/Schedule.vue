<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">{{ $t('professor.classSchedule') }}</h1>
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
            class="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 whitespace-nowrap"
          >
            {{ $t('professor.today') }}
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
                'min-h-[300px] p-3 relative',
                { 'bg-indigo-50': day.isToday },
                { 'bg-white': !day.isToday && index !== 0 && index !== 6 },
                { 'bg-red-50': !day.isToday && (index === 0 || index === 6) }
              ]"
              @click="handleEmptySpaceClick(day.date, $event)"
            >
              <!-- Appointments for this day -->
              <div class="space-y-2 overflow-y-auto pr-1 h-full">
                <div 
                  v-if="day.appointments.length === 0"
                  class="text-xs text-gray-400 italic text-center mt-2 cursor-pointer hover:text-gray-600"
                >
                  {{ $t('professor.noClasses') }} - {{ $t('professor.clickToSchedule') }}
                </div>
                <div 
                  v-for="appointment in day.appointments" 
                  :key="appointment.id"
                  :data-appointment-id="appointment.id"
                  class="p-2 text-xs rounded-md border-l-4 shadow-sm mb-2 relative cursor-pointer hover:shadow-md transition-shadow"
                  :class="getAppointmentColor(appointment.type, appointment.present)"
                  @click.stop="toggleDropdown(appointment.id)"
                >
                  <div class="font-medium flex items-center justify-between">
                    <div class="relative flex-1">
                      <span class="text-left">
                        {{ appointment.studentName }}
                      </span>
                      <!-- Dropdown Menu -->
                      <Teleport to="body">
                        <div
                          v-if="openDropdownId === appointment.id"
                          data-dropdown-menu
                          class="fixed w-48 rounded-lg bg-white z-[9999]"
                          :style="{...dropdownStyle, boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04), 0 0 0 1px rgba(0, 0, 0, 0.05)'}"
                        >
                          <div class="py-1" role="menu" @click.stop>
                            <button
                              @click.stop="registerPresence(appointment)"
                              class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center"
                              role="menuitem"
                            >
                              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                              </svg>
                              {{ $t('professor.registerAttendance') }}
                            </button>
                            <button
                              @click.stop="makeEvolution(appointment)"
                              class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center"
                              role="menuitem"
                            >
                              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                              </svg>
                              {{ $t('professor.studentEvolution') }}
                            </button>
                          </div>
                        </div>
                      </Teleport>
                    </div>
                    <span v-if="appointment.present !== null && appointment.present !== undefined" class="ml-2">
                      <span v-if="appointment.present" class="text-green-600">✓</span>
                      <span v-else class="text-red-600">✗</span>
                    </span>
                  </div>
                  <div class="mt-1">
                    <span class="font-semibold">{{ formatTimeRange(appointment.time, appointment.duration) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>


      <!-- Schedule New Class Modal -->
      <div v-if="showScheduleModal" class="fixed inset-0 overflow-y-auto h-full w-full z-50" @click.self="closeScheduleModal">
        <div class="relative top-10 mx-auto p-5 w-full max-w-2xl rounded-lg bg-white my-10" style="box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04), 0 0 0 1px rgba(0, 0, 0, 0.05);">
          <div class="mt-3">
            <h3 class="text-lg font-medium text-gray-900 mb-4">{{ $t('professor.scheduleNewClass') }}</h3>
            <form @submit.prevent="scheduleClass">
              <!-- Student Selection -->
              <div class="mb-4">
                <label for="student" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('professor.selectStudent') }}</label>
                <select 
                  id="student" 
                  v-model="selectedStudent" 
                  required
                  class="block w-full pl-3 pr-10 py-3 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-lg shadow-sm"
                >
                  <option value="">{{ $t('professor.selectAStudent') }}</option>
                  <option v-for="student in students" :key="student.id" :value="student.id">
                    {{ student.name }}
                  </option>
                </select>
              </div>

              <!-- Date and Time -->
              <div class="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label for="classDate" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('professor.classDate') }}</label>
                  <input 
                    type="date" 
                    id="classDate"
                    v-model="selectedDate"
                    required
                    class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-lg p-3"
                  />
                </div>
                <div>
                  <label for="startTime" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('professor.startTime') }}</label>
                  <input 
                    type="time" 
                    id="startTime"
                    v-model="startTime"
                    required
                    class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-lg p-3"
                  />
                </div>
              </div>

              <!-- Duration -->
              <div class="mb-4">
                <label for="duration" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('professor.classDuration') }}</label>
                <div class="flex items-center">
                  <input 
                    type="number" 
                    id="duration"
                    v-model="duration"
                    min="15"
                    step="15"
                    required
                    class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-lg p-3"
                  />
                  <span class="ml-2 text-sm text-gray-500">{{ $t('professor.minutes') }}</span>
                </div>
                <div class="mt-2 flex space-x-2">
                  <button 
                    type="button"
                    @click="duration = 30"
                    class="inline-flex items-center px-2.5 py-1.5 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50"
                  >
                    30 min
                  </button>
                  <button 
                    type="button"
                    @click="duration = 45"
                    class="inline-flex items-center px-2.5 py-1.5 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50"
                  >
                    45 min
                  </button>
                  <button 
                    type="button"
                    @click="duration = 60"
                    class="inline-flex items-center px-2.5 py-1.5 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50"
                  >
                    60 min
                  </button>
                </div>
              </div>

              <!-- Schedule Period -->
              <div class="mb-4">
                <label for="schedulePeriod" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('professor.schedulePeriod') }}</label>
                <select 
                  id="schedulePeriod" 
                  v-model="schedulePeriod" 
                  class="block w-full pl-3 pr-10 py-3 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-lg shadow-sm"
                >
                  <option value="once">{{ $t('professor.once') }}</option>
                  <option value="weekly">{{ $t('professor.everyWeek') }}</option>
                  <option value="monthly">{{ $t('professor.entireMonth') }}</option>
                  <option value="yearly">{{ $t('professor.entireYear') }}</option>
                </select>
                <p class="mt-1 text-xs text-gray-500">{{ $t('professor.schedulePeriodDescription') }}</p>
              </div>

              <!-- Actions -->
              <div class="flex justify-end space-x-3">
                <button 
                  type="button"
                  @click="closeScheduleModal"
                  class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                >
                  {{ $t('common.cancel') }}
                </button>
                <button 
                  type="submit"
                  :disabled="isSubmitting"
                  class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
                >
                  {{ isSubmitting ? $t('professor.scheduling') : $t('professor.scheduleClassButton') }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Attendance Modal -->
      <div v-if="showAttendanceModal" class="fixed inset-0 overflow-y-auto h-full w-full z-50" @click.self="closeAttendanceModal">
        <div class="relative top-20 mx-auto p-5 w-96 rounded-lg bg-white" style="box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04), 0 0 0 1px rgba(0, 0, 0, 0.05);">
          <div class="mt-3">
            <h3 class="text-lg font-medium text-gray-900 mb-4">{{ $t('professor.registerAttendance') }}</h3>
            <p class="text-sm text-gray-600 mb-4">{{ selectedAppointment?.studentName }}</p>
            <div class="flex flex-col space-y-3">
              <button
                @click="markAttendance(true)"
                :class="[
                  'w-full inline-flex justify-center items-center px-4 py-2 border text-sm font-medium rounded-md',
                  selectedAppointment?.present === true
                    ? 'bg-green-100 text-green-800 border-green-300'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-green-50'
                ]"
              >
                {{ $t('common.present') }}
              </button>
              <button
                @click="markAttendance(false)"
                :class="[
                  'w-full inline-flex justify-center items-center px-4 py-2 border text-sm font-medium rounded-md',
                  selectedAppointment?.present === false
                    ? 'bg-red-100 text-red-800 border-red-300'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-red-50'
                ]"
              >
                {{ $t('common.absent') }}
              </button>
              <button
                @click="closeAttendanceModal"
                class="w-full inline-flex justify-center items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
              >
                {{ $t('common.cancel') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { Teleport } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';
import { useScheduleStore } from '../../stores/schedule';
import { useStudentsStore } from '../../stores/students';
import { useAttendanceStore } from '../../stores/attendance';
import { isSameDay, format, parseISO, addDays, addWeeks, startOfMonth, endOfMonth, startOfYear, endOfYear, startOfDay, isAfter, isToday } from 'date-fns';
import { ptBR, enUS, es as esLocale, fr as frLocale } from 'date-fns/locale';
import { dateToFirebaseTimestamp, firebaseTimestampToLocalDate, createNormalizedDate, formatDateYYYYMMDD, isSameDay as dateUtilsIsSameDay } from '../../utils/dateUtils';
import { collection, addDoc, query, where, getDocs, getDoc, doc, updateDoc, deleteDoc, setDoc, Timestamp } from 'firebase/firestore';
import { db } from '../../firebase/config';
import Breadcrumb from '@/components/Breadcrumb.vue';

const { t, locale } = useI18n();

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const scheduleStore = useScheduleStore();
const studentsStore = useStudentsStore();
const attendanceStore = useAttendanceStore();

// Breadcrumb items
const breadcrumbItems = computed(() => {
  return [
    { name: t('professor.dashboard'), path: '/professor' },
    { name: t('professor.schedule'), path: '/professor/schedule' }
  ];
});

// Calendar state
const currentDate = ref(new Date());
const loading = ref(true);
const appointments = ref([]);
const scheduledClasses = ref([]);

// Modal states
const showScheduleModal = ref(false);
const showAttendanceModal = ref(false);
const selectedAppointment = ref(null);
const selectedDateForSchedule = ref(null);
const openDropdownId = ref(null);
const dropdownStyle = ref({});

// Schedule form state
const students = ref([]);
const selectedStudent = ref('');
const selectedDate = ref(new Date().toISOString().split('T')[0]);
const startTime = ref('');
const duration = ref(60);
const schedulePeriod = ref('once'); // 'once', 'weekly', 'monthly', 'yearly'
const isSubmitting = ref(false);
const isCopying = ref(false);

// Weekly calendar calculations
const weekDays = computed(() => {
  const days = [];
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  // Get the start of the week (Sunday) for the current date
  const weekStart = new Date(currentDate.value);
  const dayOfWeek = weekStart.getDay(); // 0 = Sunday, 6 = Saturday
  weekStart.setDate(weekStart.getDate() - dayOfWeek); // Go back to Sunday
  
  // Create array of 7 days (Sunday to Saturday) - using i18n
  const dayNames = [
    t('common.days.sunday'),
    t('common.days.monday'),
    t('common.days.tuesday'),
    t('common.days.wednesday'),
    t('common.days.thursday'),
    t('common.days.friday'),
    t('common.days.saturday')
  ];
  const shortDayNames = [
    t('common.days.sun'),
    t('common.days.mon'),
    t('common.days.tue'),
    t('common.days.wed'),
    t('common.days.thu'),
    t('common.days.fri'),
    t('common.days.sat')
  ];
  
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

// Format appointment time (HH:MM)
const formatTime = (timeString) => {
  if (!timeString) {
    return '--:--';
  }
  
  if (typeof timeString === 'string' && timeString.length <= 5 && timeString.includes(':')) {
    return timeString;
  }
  
  try {
    const time = new Date(timeString);
    if (isNaN(time.getTime())) {
      if (typeof timeString === 'string') {
        const timeMatch = timeString.match(/T(\d{2}:\d{2})/);
        if (timeMatch && timeMatch[1]) {
          return timeMatch[1];
        }
      }
      return '--:--';
    }
    const localeMap = {
      'pt': 'pt-BR',
      'en': 'en-US',
      'es': 'es-ES',
      'fr': 'fr-FR'
    };
    return time.toLocaleTimeString(localeMap[locale.value] || 'pt-BR', { hour: '2-digit', minute: '2-digit' });
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

// Format time range (e.g., "07:00 - 07:59")
const formatTimeRange = (timeString, durationMinutes) => {
  if (!timeString) {
    return '--:-- - --:--';
  }
  
  // Get formatted start time
  const startTime = formatTime(timeString);
  if (!startTime || startTime === '--:--') {
    return '--:-- - --:--';
  }
  
  // Calculate end time
  try {
    let startDate;
    
    // Parse the start time
    if (typeof timeString === 'string' && timeString.length <= 5 && timeString.includes(':')) {
      // Time-only string like "07:00"
      const [hours, minutes] = timeString.split(':').map(Number);
      startDate = new Date();
      startDate.setHours(hours, minutes, 0, 0);
    } else if (typeof timeString === 'string' && timeString.includes('T')) {
      // ISO string - extract time part
      const timeMatch = timeString.match(/T(\d{2}):(\d{2})/);
      if (timeMatch) {
        const hours = parseInt(timeMatch[1], 10);
        const minutes = parseInt(timeMatch[2], 10);
        startDate = new Date();
        startDate.setHours(hours, minutes, 0, 0);
      } else {
        startDate = new Date(timeString);
      }
    } else {
      // Full date-time string or Date object
      startDate = new Date(timeString);
    }
    
    // Default to 60 minutes if duration not provided
    const duration = durationMinutes || 60;
    
    // Add duration in minutes to get end time
    const endDate = new Date(startDate.getTime() + duration * 60000);
    
    // Format end time as HH:MM
    const endHours = endDate.getHours().toString().padStart(2, '0');
    const endMinutes = endDate.getMinutes().toString().padStart(2, '0');
    const endTime = `${endHours}:${endMinutes}`;
    
    return `${startTime} - ${endTime}`;
  } catch (error) {
    console.error('Error formatting time range:', error);
    return `${startTime} - --:--`;
  }
};

// Format date for display
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = typeof dateStr === 'string' ? parseISO(dateStr) : new Date(dateStr);
  const formatString = locale.value === 'en' ? 'MM/dd/yyyy' : 'dd/MM/yyyy';
  return format(date, formatString, { locale: dateFnsLocale.value });
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

// Toggle dropdown
const toggleDropdown = async (appointmentId) => {
  if (openDropdownId.value === appointmentId) {
    openDropdownId.value = null;
  } else {
    openDropdownId.value = appointmentId;
    await nextTick();
    // Update position after opening
    const cardElement = document.querySelector(`[data-appointment-id="${appointmentId}"]`);
    if (cardElement) {
      const rect = cardElement.getBoundingClientRect();
      dropdownStyle.value = {
        top: `${rect.bottom + 4}px`,
        left: `${rect.left}px`
      };
    }
  }
};

// Close dropdown
const closeDropdown = () => {
  openDropdownId.value = null;
};

// Handle empty space click
const handleEmptySpaceClick = (dateString, event) => {
  // Only trigger if clicking on the day cell itself, not on appointments
  // Check if the click target is not an appointment card
  const isAppointmentClick = event.target.closest('.border-l-4');
  if (!isAppointmentClick) {
    selectedDateForSchedule.value = dateString;
    selectedDate.value = dateString.split('T')[0];
    showScheduleModal.value = true;
  }
};

// Close schedule modal
const closeScheduleModal = () => {
  showScheduleModal.value = false;
  selectedStudent.value = '';
  selectedDate.value = new Date().toISOString().split('T')[0];
  startTime.value = '';
  duration.value = 60;
  schedulePeriod.value = 'once';
};

// Register presence
const registerPresence = (appointment) => {
  selectedAppointment.value = appointment;
  closeDropdown();
  showAttendanceModal.value = true;
};

// Close attendance modal
const closeAttendanceModal = () => {
  showAttendanceModal.value = false;
};

// Make evolution
const makeEvolution = (appointment) => {
  closeDropdown();
  if (appointment?.studentId) {
    router.push({
      path: '/professor/evolution',
      query: { studentId: appointment.studentId }
    });
  } else {
    router.push('/professor/evolution');
  }
};

// Mark attendance
const markAttendance = async (present) => {
  if (!selectedAppointment.value) return;
  
  try {
    loading.value = true;
    
    // Use classId if available (from scheduledClasses), otherwise use id
    const classId = selectedAppointment.value.classId || selectedAppointment.value.id;
    
    // Update attendance status in scheduledClasses collection
    await updateDoc(doc(db, 'scheduledClasses', classId), {
      present: present
    });
    
    // Get the scheduled class details
    const classItem = scheduledClasses.value.find(item => item.id === classId) || selectedAppointment.value;
    
    // Use date utilities to get a consistent local date - always normalize to noon
    let localDate;
    if (classItem.date instanceof Date) {
      localDate = classItem.date;
    } else if (typeof classItem.date === 'string') {
      localDate = parseISO(classItem.date);
    } else {
      localDate = firebaseTimestampToLocalDate(classItem.date);
    }
    
    // Normalize the date to noon to ensure consistent dateKey generation
    // This prevents timezone issues that could create different dateKeys for the same day
    const normalizedDate = new Date(localDate);
    normalizedDate.setHours(12, 0, 0, 0);
    
    // Format the date for the ID - one record per day per student per professor
    const dateKey = formatDateYYYYMMDD(normalizedDate);
    const attendanceId = `${classItem.studentId}_${classItem.professorId}_${dateKey}`;
    
    // Check if attendance record already exists BEFORE creating/updating
    const attendanceRef = doc(db, 'attendanceRecords', attendanceId);
    const attendanceDoc = await getDoc(attendanceRef);
    
    if (present) {
      // Get student data
      const studentDoc = await getDoc(doc(db, 'users', classItem.studentId));
      const studentData = studentDoc.exists() ? studentDoc.data() : {};
      
      // If marked as present, create/update the attendance record
      const attendanceData = {
        studentId: classItem.studentId,
        professorId: classItem.professorId,
        studentName: studentData.name || 'Unknown',
        studentPlanId: studentData.planId,
        date: dateToFirebaseTimestamp(normalizedDate),
        present: true,
        updatedAt: Timestamp.now()
      };
      
      // Only create if it doesn't exist to prevent duplicates
      if (attendanceDoc.exists()) {
        // Update existing record instead of creating a new one
        await updateDoc(attendanceRef, {
          present: true,
          updatedAt: Timestamp.now()
        });
      } else {
        // Create new record only if it doesn't exist
        await setDoc(attendanceRef, attendanceData);
      }
    } else if (present === false) {
      // If marked as absent, check if record exists and update it
      if (attendanceDoc.exists()) {
        await updateDoc(attendanceRef, {
          present: false,
          updatedAt: Timestamp.now()
        });
      }
    }
    
    // Update local state
    const index = appointments.value.findIndex(item => item.id === classId);
    if (index !== -1) {
      appointments.value[index].present = present;
    }
    
    // Recalculate earnings
    await attendanceStore.calculateMonthlyEarnings(authStore.userId);
    
    closeAttendanceModal();
    await fetchAppointments();
  } catch (err) {
    console.error('Error marking attendance:', err);
    window.showErrorToast?.(t('professor.errorMarkingAttendance') || 'Error marking attendance');
  } finally {
    loading.value = false;
  }
};

// Generate dates based on schedule period
const generateScheduleDates = (startDate, period) => {
  const dates = [];
  const [year, month, day] = startDate.split('-').map(Number);
  const baseDate = createNormalizedDate(year, month, day);
  const dayOfWeek = baseDate.getDay(); // 0 = Sunday, 1 = Monday, etc.
  
  // Get today's date (normalized to start of day for comparison)
  const today = startOfDay(new Date());
  
  if (period === 'once') {
    // Single occurrence - only add if it's today or in the future
    const normalizedBaseDate = startOfDay(baseDate);
    if (isToday(normalizedBaseDate) || isAfter(normalizedBaseDate, today)) {
      dates.push(baseDate);
    }
  } else if (period === 'weekly') {
    // Every week for the next 52 weeks (1 year) - only future dates
    for (let i = 0; i < 52; i++) {
      const weekDate = addWeeks(baseDate, i);
      const normalizedWeekDate = startOfDay(weekDate);
      // Only add if it's today or in the future
      if (isToday(normalizedWeekDate) || isAfter(normalizedWeekDate, today)) {
        dates.push(weekDate);
      }
    }
  } else if (period === 'monthly') {
    // All occurrences of this day of week in the selected month - only future dates
    const monthStart = startOfMonth(baseDate);
    const monthEnd = endOfMonth(baseDate);
    
    // Find first occurrence of the day of week in the month
    let currentDate = new Date(monthStart);
    const daysToAdd = (dayOfWeek - currentDate.getDay() + 7) % 7;
    currentDate = addDays(currentDate, daysToAdd);
    
    // Add all occurrences of this day of week in the month that are today or in the future
    while (currentDate <= monthEnd) {
      const normalizedCurrentDate = startOfDay(currentDate);
      // Only add if it's today or in the future
      if (isToday(normalizedCurrentDate) || isAfter(normalizedCurrentDate, today)) {
        dates.push(new Date(currentDate));
      }
      currentDate = addDays(currentDate, 7);
    }
  } else if (period === 'yearly') {
    // All occurrences of this day of week in the selected year - only future dates
    const yearStart = startOfYear(baseDate);
    const yearEnd = endOfYear(baseDate);
    
    // Find first occurrence of the day of week in the year
    let currentDate = new Date(yearStart);
    const daysToAdd = (dayOfWeek - currentDate.getDay() + 7) % 7;
    currentDate = addDays(currentDate, daysToAdd);
    
    // Add all occurrences of this day of week in the year that are today or in the future
    while (currentDate <= yearEnd) {
      const normalizedCurrentDate = startOfDay(currentDate);
      // Only add if it's today or in the future
      if (isToday(normalizedCurrentDate) || isAfter(normalizedCurrentDate, today)) {
        dates.push(new Date(currentDate));
      }
      currentDate = addDays(currentDate, 7);
    }
  }
  
  return dates;
};

// Schedule class
const scheduleClass = async () => {
  try {
    isSubmitting.value = true;
    
    // Parse time
    const [hours, minutes] = startTime.value.split(':').map(Number);
    
    // Generate all dates based on the selected period
    const dates = generateScheduleDates(selectedDate.value, schedulePeriod.value);
    
    if (dates.length === 0) {
      window.showErrorToast?.(t('professor.noDatesGenerated') || 'No dates could be generated. Please try again.');
      return;
    }
    
    // Create scheduled classes for all dates
    let createdCount = 0;
    let skippedCount = 0;
    
    for (const date of dates) {
      // Set the specific time from the time picker
      const classDate = new Date(date);
      classDate.setHours(hours, minutes, 0, 0);
      
      // Check if a class already exists for this date, student, and time
      const existingQuery = query(
        collection(db, 'scheduledClasses'),
        where('professorId', '==', authStore.userId),
        where('studentId', '==', selectedStudent.value)
      );
      
      const existingSnapshot = await getDocs(existingQuery);
      const classDateNormalized = createNormalizedDate(
        classDate.getFullYear(),
        classDate.getMonth() + 1,
        classDate.getDate()
      );
      
      // Check if a similar class already exists
      let alreadyExists = false;
      for (const docSnapshot of existingSnapshot.docs) {
        const existingData = docSnapshot.data();
        const existingDate = firebaseTimestampToLocalDate(existingData.date);
        const existingDateNormalized = createNormalizedDate(
          existingDate.getFullYear(),
          existingDate.getMonth() + 1,
          existingDate.getDate()
        );
        
        if (
          isSameDay(existingDateNormalized, classDateNormalized) &&
          existingData.startTime === startTime.value
        ) {
          alreadyExists = true;
          break;
        }
      }
      
      if (alreadyExists) {
        skippedCount++;
        continue;
      }
      
      // Create the scheduled class
      await addDoc(collection(db, 'scheduledClasses'), {
        studentId: selectedStudent.value,
        professorId: authStore.userId,
        date: dateToFirebaseTimestamp(classDate),
        startTime: startTime.value,
        duration: Number(duration.value),
        present: null,
        createdAt: Timestamp.now()
      });
      
      createdCount++;
    }
    
    // Show success message
    if (createdCount > 0) {
      // Simple success message
      const message = t('professor.datesAddedSuccess') || 'Dates added successfully.';
      window.showSuccessToast?.(message);
      
      // Show warning if some dates were skipped
      if (skippedCount > 0) {
        const skippedMessage = t('professor.someDatesSkipped', { count: skippedCount }) || 
                              `${skippedCount} date(s) skipped (already exist).`;
        window.showWarningToast?.(skippedMessage);
      }
    } else {
      const message = t('professor.allClassesAlreadyExist') || 'All classes already exist.';
      window.showWarningToast?.(message);
    }
    
    // Reset form and close modal
    closeScheduleModal();
    await fetchAppointments();
    
  } catch (err) {
    console.error('Error scheduling class:', err);
    window.showErrorToast?.(t('professor.errorSchedulingClass') || 'Error scheduling class');
  } finally {
    isSubmitting.value = false;
  }
};

// Fetch appointments for the current week
const fetchAppointments = async () => {
  try {
    loading.value = true;
    
    // Get first and last day of the week
    const dayOfWeek = currentDate.value.getDay();
    const firstDay = new Date(currentDate.value);
    firstDay.setDate(currentDate.value.getDate() - dayOfWeek);
    
    const lastDay = new Date(firstDay);
    lastDay.setDate(firstDay.getDate() + 6);
    
    // Fetch appointments for the current week only
    const fetchedAppointments = await scheduleStore.fetchProfessorSchedule(
      authStore.userId,
      firstDay, 
      lastDay
    );
    
    // Also fetch scheduled classes to get attendance status
    const classesQuery = query(
      collection(db, 'scheduledClasses'),
      where('professorId', '==', authStore.userId)
    );
    
    const classesSnapshot = await getDocs(classesQuery);
    scheduledClasses.value = classesSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    
    // Deduplicate appointments and merge attendance status
    // Use a Map to track unique appointments by student, date, and time
    const uniqueAppointmentsMap = new Map();
    
    fetchedAppointments.forEach(appointment => {
      const appDate = typeof appointment.date === 'string' 
        ? parseISO(appointment.date) 
        : new Date(appointment.date);
      const dateKey = formatDateYYYYMMDD(appDate);
      const timeKey = appointment.time || '';
      
      // Create a unique key for each appointment
      const uniqueKey = `${appointment.studentId}_${dateKey}_${timeKey}`;
      
      // Only add if we haven't seen this appointment before
      if (!uniqueAppointmentsMap.has(uniqueKey)) {
        const scheduledClass = scheduledClasses.value.find(sc => {
          const scDate = firebaseTimestampToLocalDate(sc.date);
          const scTime = sc.startTime || '';
          const sameDate = dateUtilsIsSameDay(scDate, appDate);
          const sameStudent = sc.studentId === appointment.studentId;
          const sameTime = !sc.startTime || !appointment.time || sc.startTime === appointment.time;
          return sameDate && sameStudent && sameTime;
        });
        
        uniqueAppointmentsMap.set(uniqueKey, {
          ...appointment,
          present: scheduledClass?.present,
          classId: scheduledClass?.id,
          // Use scheduled class ID if available for attendance marking
          id: scheduledClass?.id || appointment.id
        });
      }
    });
    
    // Convert map back to array
    appointments.value = Array.from(uniqueAppointmentsMap.values());
    
  } catch (error) {
    console.error('Error fetching appointments:', error);
  } finally {
    loading.value = false;
  }
};

// Fetch students
const fetchStudents = async () => {
  try {
    const fetchedStudents = await studentsStore.fetchStudents();
    // Sort students alphabetically by name
    students.value = fetchedStudents.sort((a, b) => {
      const nameA = (a.name || a.displayName || '').toLowerCase();
      const nameB = (b.name || b.displayName || '').toLowerCase();
      return nameA.localeCompare(nameB);
    });
  } catch (err) {
    console.error('Error fetching students:', err);
  }
};

// Copy current week's schedule to next week
const copyWeekToNext = async () => {
  // Confirm action
  if (!confirm(t('professor.confirmCopyWeek') || 'Are you sure you want to copy this week\'s schedule to next week?')) {
    return;
  }

  try {
    isCopying.value = true;
    
    // Get first and last day of the current week
    const dayOfWeek = currentDate.value.getDay();
    const firstDay = new Date(currentDate.value);
    firstDay.setDate(currentDate.value.getDate() - dayOfWeek);
    
    const lastDay = new Date(firstDay);
    lastDay.setDate(firstDay.getDate() + 6);
    
    // Get all scheduled classes for the current week
    const classesQuery = query(
      collection(db, 'scheduledClasses'),
      where('professorId', '==', authStore.userId)
    );
    
    const classesSnapshot = await getDocs(classesQuery);
    const currentWeekClasses = [];
    
    // Filter classes that are in the current week
    for (const docSnapshot of classesSnapshot.docs) {
      const classData = docSnapshot.data();
      const classDate = firebaseTimestampToLocalDate(classData.date);
      
      // Check if the class is within the current week
      if (classDate >= firstDay && classDate <= lastDay) {
        currentWeekClasses.push({
          id: docSnapshot.id,
          ...classData
        });
      }
    }
    
    if (currentWeekClasses.length === 0) {
      window.showWarningToast?.(t('professor.noClassesToCopy') || 'No classes found in the current week to copy.');
      return;
    }
    
    // Calculate next week's dates
    const nextWeekFirstDay = new Date(firstDay);
    nextWeekFirstDay.setDate(firstDay.getDate() + 7);
    
    const nextWeekLastDay = new Date(lastDay);
    nextWeekLastDay.setDate(lastDay.getDate() + 7);
    
    // Check if there are already classes in the next week
    const nextWeekClassesQuery = query(
      collection(db, 'scheduledClasses'),
      where('professorId', '==', authStore.userId)
    );
    
    const nextWeekSnapshot = await getDocs(nextWeekClassesQuery);
    const existingNextWeekClasses = [];
    
    for (const docSnapshot of nextWeekSnapshot.docs) {
      const classData = docSnapshot.data();
      const classDate = firebaseTimestampToLocalDate(classData.date);
      
      if (classDate >= nextWeekFirstDay && classDate <= nextWeekLastDay) {
        existingNextWeekClasses.push({
          studentId: classData.studentId,
          startTime: classData.startTime,
          date: classDate
        });
      }
    }
    
    // Copy each class to next week (7 days later)
    let copiedCount = 0;
    let skippedCount = 0;
    
    for (const classItem of currentWeekClasses) {
      const originalDate = firebaseTimestampToLocalDate(classItem.date);
      
      // Create new date 7 days later
      const newDate = new Date(originalDate);
      newDate.setDate(originalDate.getDate() + 7);
      
      // Check if a similar class already exists in the next week
      const alreadyExists = existingNextWeekClasses.some(existing => {
        const sameDate = isSameDay(existing.date, newDate);
        const sameStudent = existing.studentId === classItem.studentId;
        const sameTime = existing.startTime === classItem.startTime;
        return sameDate && sameStudent && sameTime;
      });
      
      if (alreadyExists) {
        skippedCount++;
        continue;
      }
      
      // Create normalized date for the new week
      const normalizedNewDate = createNormalizedDate(
        newDate.getFullYear(),
        newDate.getMonth() + 1,
        newDate.getDate()
      );
      
      // Set the time from the original class
      const [hours, minutes] = (classItem.startTime || '00:00').split(':').map(Number);
      normalizedNewDate.setHours(hours, minutes, 0, 0);
      
      // Create new scheduled class
      await addDoc(collection(db, 'scheduledClasses'), {
        studentId: classItem.studentId,
        professorId: classItem.professorId,
        date: dateToFirebaseTimestamp(normalizedNewDate),
        startTime: classItem.startTime,
        duration: classItem.duration || 60,
        present: null, // Reset attendance status
        createdAt: Timestamp.now()
      });
      
      copiedCount++;
    }
    
    // Show success message
    let message = '';
    if (copiedCount > 0) {
      const baseMessage = t('professor.weekCopiedSuccess') || '{count} class(es) copied successfully.';
      message = baseMessage.replace('{count}', copiedCount.toString());
      if (skippedCount > 0) {
        message += ` ${skippedCount} ${t('professor.skipped') || 'skipped (already exist).'}`;
      }
    } else {
      message = t('professor.allClassesAlreadyExist') || 'All classes already exist in the next week.';
    }
    
    window.showSuccessToast?.(message);
    
    // Refresh appointments to show the new schedule
    await fetchAppointments();
    
  } catch (err) {
    console.error('Error copying week:', err);
    window.showErrorToast?.(t('professor.errorCopyingWeek') || 'Error copying schedule. Please try again.');
  } finally {
    isCopying.value = false;
  }
};

// Handle clicks outside dropdown
const handleClickOutside = (event) => {
  // Close if dropdown is open and clicking outside
  if (openDropdownId.value) {
    // Check if click is outside the dropdown menu (which is now in body via Teleport)
    const dropdownElement = event.target.closest('[data-dropdown-menu]');
    const appointmentCard = event.target.closest('[data-appointment-id]');
    
    // If clicking on dropdown or appointment card, don't close
    if (!dropdownElement && !appointmentCard) {
      openDropdownId.value = null;
    }
  }
};

// Initialize
onMounted(async () => {
  if (!authStore.isProfessor) {
    router.push('/login');
    return;
  }
  
  await Promise.all([
    fetchStudents(),
    fetchAppointments()
  ]);
  
  // Add click outside handler
  document.addEventListener('click', handleClickOutside);
});

// Clean up event listener
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>
