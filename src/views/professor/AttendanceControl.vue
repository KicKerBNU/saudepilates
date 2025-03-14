<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">Controle de Presença</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <div class="bg-white shadow overflow-hidden sm:rounded-lg">
          <div class="px-6 py-6 sm:p-8">
            <!-- Tabs -->
            <div class="border-b border-gray-200">
              <nav class="-mb-px flex space-x-8">
                <button 
                  @click="activeTab = 'schedule'"
                  :class="[
                    activeTab === 'schedule'
                      ? 'border-indigo-500 text-indigo-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
                    'whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm'
                  ]"
                >
                  Agendar Aulas
                </button>
                <button 
                  @click="activeTab = 'attendance'"
                  :class="[
                    activeTab === 'attendance'
                      ? 'border-indigo-500 text-indigo-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
                    'whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm'
                  ]"
                >
                  Registrar Presença
                </button>
              </nav>
            </div>
            
            <!-- Tab Content -->
            <div class="mt-6">
              <!-- Schedule Tab -->
              <div v-if="activeTab === 'schedule'" class="p-6">
                <div class="max-w-3xl mx-auto bg-white rounded-xl shadow-md overflow-hidden">
                  <!-- Form Header -->
                  <div class="bg-gradient-to-r from-indigo-600 to-purple-600 px-6 py-4">
                    <h2 class="text-xl font-bold text-white flex items-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                      Agendar Nova Aula
                    </h2>
                    <p class="text-indigo-100 text-sm mt-1">Preencha os dados abaixo para agendar uma nova aula</p>
                  </div>
                  
                  <!-- Form Body -->
                  <div class="p-6 space-y-6">
                    <!-- Student Selection Section -->
                    <div class="bg-indigo-50 p-4 rounded-lg">
                      <div class="mb-1 flex items-center">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                        </svg>
                        <label for="student" class="block text-sm font-semibold text-indigo-900">Selecione o Aluno</label>
                      </div>
                      <select 
                        id="student" 
                        v-model="selectedStudent" 
                        class="block w-full pl-3 pr-10 py-3 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-lg shadow-sm transition duration-150 ease-in-out"
                      >
                        <option value="">-- Selecione um aluno --</option>
                        <option v-for="student in students" :key="student.id" :value="student.id">
                          {{ student.name }}
                        </option>
                      </select>
                    </div>
                    
                    <!-- Date and Time Section -->
                    <div class="border-t border-gray-200 pt-6">
                      <h3 class="text-lg font-medium text-gray-900 mb-4">Data e Horário</h3>
                      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
                        <!-- Date Picker -->
                        <div class="relative">
                          <div class="mb-1 flex items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                            </svg>
                            <label class="block text-sm font-medium text-gray-700">Data da Aula</label>
                          </div>
                          <input 
                            type="date" 
                            v-model="selectedDate"
                            class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-lg p-3 bg-white"
                          />
                        </div>
                        
                        <!-- Time Picker -->
                        <div class="relative">
                          <div class="mb-1 flex items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                            <label class="block text-sm font-medium text-gray-700">Hora de Início</label>
                          </div>
                          <input 
                            type="time" 
                            v-model="startTime"
                            class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-lg p-3 bg-white"
                          />
                        </div>
                      </div>
                    </div>
                    
                    <!-- Duration Section -->
                    <div class="border-t border-gray-200 pt-6">
                      <div class="relative">
                        <div class="mb-1 flex items-center">
                          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                          <label class="block text-sm font-medium text-gray-700">Duração da Aula</label>
                        </div>
                        <div class="flex items-center">
                          <input 
                            type="number" 
                            v-model="duration"
                            min="15"
                            step="15"
                            class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-lg p-3 bg-white"
                          />
                          <span class="ml-2 text-sm text-gray-500">minutos</span>
                        </div>
                        
                        <!-- Quick Duration Buttons -->
                        <div class="mt-2 flex space-x-2">
                          <button 
                            type="button"
                            @click="duration = 30"
                            class="inline-flex items-center px-2.5 py-1.5 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                          >
                            30 min
                          </button>
                          <button 
                            type="button"
                            @click="duration = 45"
                            class="inline-flex items-center px-2.5 py-1.5 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                          >
                            45 min
                          </button>
                          <button 
                            type="button"
                            @click="duration = 60"
                            class="inline-flex items-center px-2.5 py-1.5 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                          >
                            60 min
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <!-- Form Footer/Actions -->
                  <div class="bg-gray-50 px-6 py-4 flex items-center justify-end space-x-3 border-t border-gray-200">
                    <button 
                      type="button" 
                      class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-150 ease-in-out"
                      @click="resetForm"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="-ml-1 mr-2 h-5 w-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                      Cancelar
                    </button>
                    <button 
                      type="button" 
                      class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-150 ease-in-out disabled:opacity-50 disabled:cursor-not-allowed"
                      @click="scheduleClass"
                      :disabled="!isFormValid || isSubmitting"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="-ml-1 mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                      {{ isSubmitting ? 'Agendando...' : 'Agendar Aula' }}
                    </button>
                  </div>
                  
                  <!-- Form Validation Help Messages -->
                  <div v-if="!isFormValid && (selectedStudent || selectedDate || startTime || duration)" class="bg-yellow-50 border-l-4 border-yellow-400 p-4 mt-4">
                    <div class="flex">
                      <div class="flex-shrink-0">
                        <svg class="h-5 w-5 text-yellow-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                          <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
                        </svg>
                      </div>
                      <div class="ml-3">
                        <p class="text-sm text-yellow-700">
                          Preencha todos os campos obrigatórios para agendar a aula.
                        </p>
                        <ul class="mt-2 text-sm text-yellow-700 list-disc list-inside">
                          <li v-if="!selectedStudent">Selecione um aluno</li>
                          <li v-if="!selectedDate">Escolha a data da aula</li>
                          <li v-if="!startTime">Defina o horário de início</li>
                          <li v-if="!duration || duration <= 0">Informe a duração da aula</li>
                        </ul>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <!-- Attendance Tab -->
              <div v-if="activeTab === 'attendance'" class="space-y-6 p-4">
                <div class="flex space-x-4 mb-4 p-2">
                  <div>
                    <label class="block text-sm font-medium text-gray-700">Data</label>
                    <input 
                      type="date" 
                      v-model="attendanceDate"
                      class="mt-1 shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md p-3"
                    />
                  </div>
                  <div class="flex-1">
                    <label class="block text-sm font-medium text-gray-700">Filtrar por nome</label>
                    <input
                      type="text"
                      v-model="searchQuery"
                      placeholder="Buscar aluno..."
                      class="mt-1 shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md p-3"
                    />
                  </div>
                </div>
                
                <div v-if="isLoading" class="flex justify-center p-4">
                  <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500"></div>
                </div>
                
                <div v-else-if="scheduledClasses.length === 0" class="py-8 px-4 text-center">
                  <p class="text-gray-500">Não há aulas agendadas para esta data.</p>
                  <button
                    @click="activeTab = 'schedule'"
                    class="mt-2 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    Agendar Aula
                  </button>
                </div>
                
                <div v-else class="overflow-x-auto p-2">
                  <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                      <tr>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Aluno
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Horário
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Duração
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Presença
                        </th>
                        <th scope="col" class="relative px-6 py-3">
                          <span class="sr-only">Ações</span>
                        </th>
                      </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                      <tr v-for="(classItem, index) in filteredClasses" :key="classItem.id">
                        <td class="px-6 py-4 whitespace-nowrap">
                          <div class="flex items-center">
                            <div>
                              <div class="text-sm font-medium text-gray-900">
                                {{ classItem.studentName }}
                              </div>
                            </div>
                          </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                          <div class="text-sm text-gray-900">{{ formatTime(classItem.startTime) }}</div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                          <div class="text-sm text-gray-900">{{ classItem.duration }} min</div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                          <div class="flex items-center space-x-4">
                            <button
                              @click="markAttendance(classItem.id, true)"
                              :class="[
                                classItem.present === true
                                  ? 'bg-green-100 text-green-800 border-green-300 hover:bg-green-200'
                                  : 'bg-gray-100 text-gray-800 border-gray-300 hover:bg-gray-200',
                                'inline-flex items-center px-2.5 py-1.5 border text-xs font-medium rounded'
                              ]"
                            >
                              Presente
                            </button>
                            <button
                              @click="markAttendance(classItem.id, false)"
                              :class="[
                                classItem.present === false
                                  ? 'bg-red-100 text-red-800 border-red-300 hover:bg-red-200'
                                  : 'bg-gray-100 text-gray-800 border-gray-300 hover:bg-gray-200',
                                'inline-flex items-center px-2.5 py-1.5 border text-xs font-medium rounded'
                              ]"
                            >
                              Ausente
                            </button>
                          </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                          <button
                            @click="deleteClass(classItem.id)"
                            class="text-red-600 hover:text-red-900"
                          >
                            Excluir
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
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
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { useStudentsStore } from '../../stores/students';
import { useAttendanceStore } from '../../stores/attendance';
import { 
  collection, 
  addDoc, 
  query, 
  where, 
  getDocs,
  getDoc,
  doc, 
  updateDoc, 
  deleteDoc,
  setDoc,
  Timestamp 
} from 'firebase/firestore';
import { db } from '../../firebase/config';
import { dateToFirebaseTimestamp, firebaseTimestampToLocalDate, formatDateYYYYMMDD, isSameDay, createNormalizedDate } from '../../utils/dateUtils';

const router = useRouter();
const authStore = useAuthStore();
const studentsStore = useStudentsStore();
const attendanceStore = useAttendanceStore();

// State for UI
const activeTab = ref('schedule');
const students = ref([]);
const scheduledClasses = ref([]);
const isLoading = ref(false);
const isSubmitting = ref(false);
const error = ref(null);
const searchQuery = ref('');

// Form fields
const selectedStudent = ref('');
const selectedDate = ref(new Date().toISOString().split('T')[0]); // Today's date in YYYY-MM-DD format
const startTime = ref('');
const duration = ref(60);
const attendanceDate = ref(new Date().toISOString().split('T')[0]); // Today's date for attendance tab

// Form validation
const isFormValid = computed(() => {
  return (
    selectedStudent.value &&
    selectedDate.value &&
    startTime.value &&
    duration.value > 0
  );
});

// Filtered classes based on search query
const filteredClasses = computed(() => {
  if (!searchQuery.value) return scheduledClasses.value;
  
  const query = searchQuery.value.toLowerCase();
  return scheduledClasses.value.filter(classItem => 
    classItem.studentName.toLowerCase().includes(query)
  );
});

// Lifecycle hooks
onMounted(async () => {
  if (!authStore.isProfessor) {
    router.push('/login');
    return;
  }
  
  await Promise.all([
    fetchStudents(),
    fetchScheduledClasses()
  ]);
});

// Watch for date changes to refresh scheduled classes
watch(attendanceDate, async () => {
  await fetchScheduledClasses();
});

// Methods
const fetchStudents = async () => {
  try {
    isLoading.value = true;
    students.value = await studentsStore.fetchStudents();
  } catch (err) {
    error.value = 'Erro ao carregar alunos: ' + err.message;
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

const fetchScheduledClasses = async () => {
  try {
    isLoading.value = true;
    scheduledClasses.value = [];
    
    // Parse the selected date properly using date-fns utilities
    const [year, month, day] = attendanceDate.value.split('-').map(Number);
    const selectedDay = createNormalizedDate(year, month, day);
    
    // Firestore query - filter by professor first
    const classesQuery = query(
      collection(db, 'scheduledClasses'),
      where('professorId', '==', authStore.userId)
    );
    
    const snapshot = await getDocs(classesQuery);
    
    
    // Filter for the correct date using date-fns isSameDay function
    const filteredDocs = snapshot.docs.filter(doc => {
      const data = doc.data();
      // Convert Firebase timestamp to local date using our utility
      const classDate = firebaseTimestampToLocalDate(data.date);
      
      // Compare dates using date-fns utility
      const result = isSameDay(classDate, selectedDay);
      
      return result;
    });
    
    // Get student names for each class
    const classesWithStudentNames = await Promise.all(
      filteredDocs.map(async (docSnapshot) => {
        const classData = { id: docSnapshot.id, ...docSnapshot.data() };
        const studentDoc = await getDoc(doc(db, 'users', classData.studentId));
        
        return {
          ...classData,
          studentName: studentDoc.exists() ? studentDoc.data().name : 'Aluno Desconhecido'
        };
      })
    );
    
    // Sort by start time
    scheduledClasses.value = classesWithStudentNames.sort((a, b) => {
      return a.startTime.localeCompare(b.startTime);
    });
    
  } catch (err) {
    error.value = 'Erro ao carregar aulas agendadas: ' + err.message;
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

const scheduleClass = async () => {
  try {
    isSubmitting.value = true;
    
    // Use date-fns to handle the date correctly
    const [year, month, day] = selectedDate.value.split('-').map(Number);
    const [hours, minutes] = startTime.value.split(':').map(Number);
    
    // First create a normalized date at noon
    let classDate = createNormalizedDate(year, month, day);
    
    // Then set the specific time from the time picker
    classDate = new Date(classDate.setHours(hours, minutes, 0, 0));
    
    
    
    // Add to Firestore - use our dateUtils to create a consistent timestamp
    await addDoc(collection(db, 'scheduledClasses'), {
      studentId: selectedStudent.value,
      professorId: authStore.userId,
      date: dateToFirebaseTimestamp(classDate), // Use our utility function
      startTime: startTime.value,
      duration: Number(duration.value),
      present: null, // null means not marked yet
      createdAt: Timestamp.now()
    });
    
    // Reset form and switch to attendance tab
    resetForm();
    activeTab.value = 'attendance';
    attendanceDate.value = selectedDate.value;
    await fetchScheduledClasses();
    
  } catch (err) {
    error.value = 'Erro ao agendar aula: ' + err.message;
    console.error(err);
  } finally {
    isSubmitting.value = false;
  }
};

const markAttendance = async (classId, present) => {
  try {
    isLoading.value = true;
    
    // Get the scheduled class details
    const classItem = scheduledClasses.value.find(item => item.id === classId);
    if (!classItem) {
      throw new Error('Aula não encontrada');
    }
    
    // Update attendance status in scheduledClasses collection
    await updateDoc(doc(db, 'scheduledClasses', classId), {
      present: present
    });
    
    // Use our date utilities to get a consistent local date from the Firebase timestamp
    const localDate = firebaseTimestampToLocalDate(classItem.date);
    
    // Format the date for the ID
    const dateKey = formatDateYYYYMMDD(localDate);
    const attendanceId = `${classItem.studentId}_${classItem.professorId}_${dateKey}`;
    
    
    
    // Also update or create a record in the attendanceRecords collection for earnings calculation
    if (present) {
      // Get student data to make sure we have all necessary info for earnings calculation
      const studentDoc = await getDoc(doc(db, 'users', classItem.studentId));
      const studentData = studentDoc.exists() ? studentDoc.data() : {};
      
      // If marked as present, create/update the attendance record
      // Include additional info to help with debugging
      // Use our date utility to create a consistent timestamp from the local date
      const attendanceData = {
        studentId: classItem.studentId,
        professorId: classItem.professorId,
        studentName: studentData.name || 'Unknown',
        studentPlanId: studentData.planId, // Include plan ID for easier debugging
        date: dateToFirebaseTimestamp(localDate), // Use our utility function
        present: true,
        updatedAt: Timestamp.now()
      };
      
      
      
      await setDoc(doc(db, 'attendanceRecords', attendanceId), attendanceData);
    } else if (present === false) {
      // If marked as absent, check if record exists and update it
      const attendanceRef = doc(db, 'attendanceRecords', attendanceId);
      const attendanceDoc = await getDoc(attendanceRef);
      
      if (attendanceDoc.exists()) {
        // Update to absent
        await updateDoc(attendanceRef, {
          present: false,
          updatedAt: Timestamp.now()
        });
      }
    }
    
    // Update local state
    const index = scheduledClasses.value.findIndex(item => item.id === classId);
    if (index !== -1) {
      scheduledClasses.value[index].present = present;
    }
    
    // Recalculate earnings in the attendance store
    const newEarnings = await attendanceStore.calculateMonthlyEarnings(authStore.userId);
    
  } catch (err) {
    error.value = 'Erro ao marcar presença: ' + err.message;
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

const deleteClass = async (classId) => {
  if (!confirm('Tem certeza que deseja excluir esta aula?')) {
    return;
  }
  
  try {
    isLoading.value = true;
    
    // Delete from Firestore
    await deleteDoc(doc(db, 'scheduledClasses', classId));
    
    // Remove from local state
    scheduledClasses.value = scheduledClasses.value.filter(item => item.id !== classId);
    
  } catch (err) {
    error.value = 'Erro ao excluir aula: ' + err.message;
    console.error(err);

    isLoading.value = false;
  }
};

const resetForm = () => {
  selectedStudent.value = '';
  // Keep the date as is
  startTime.value = '';
  duration.value = 60;
  
  // Redirect to professor dashboard
  router.push('/professor');
};

const formatTime = (timeString) => {
  // Format HH:MM to a more readable format
  if (!timeString) return '';
  
  const [hours, minutes] = timeString.split(':');
  return `${hours}:${minutes}`;
};
</script>
