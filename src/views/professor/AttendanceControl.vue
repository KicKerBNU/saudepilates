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
              <div v-if="activeTab === 'schedule'" class="space-y-6 p-4">
                <div>
                  <label for="student" class="block text-sm font-medium text-gray-700">Aluno</label>
                  <select 
                    id="student" 
                    v-model="selectedStudent" 
                    class="mt-1 block w-full pl-3 pr-10 py-3 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
                  >
                    <option value="">Selecione um aluno</option>
                    <option v-for="student in students" :key="student.id" :value="student.id">
                      {{ student.name }}
                    </option>
                  </select>
                </div>
                
                <div>
                  <label class="block text-sm font-medium text-gray-700">Data da Aula</label>
                  <div class="mt-1">
                    <input 
                      type="date" 
                      v-model="selectedDate"
                      class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md p-3"
                    />
                  </div>
                </div>
                
                <div class="grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-2">
                  <div>
                    <label class="block text-sm font-medium text-gray-700">Hora de Início</label>
                    <div class="mt-1">
                      <input 
                        type="time" 
                        v-model="startTime"
                        class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md p-3"
                      />
                    </div>
                  </div>
                  
                  <div>
                    <label class="block text-sm font-medium text-gray-700">Duração (minutos)</label>
                    <div class="mt-1">
                      <input 
                        type="number" 
                        v-model="duration"
                        min="15"
                        step="15"
                        class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md p-3"
                      />
                    </div>
                  </div>
                </div>
                
                <div class="pt-5 px-2">
                  <div class="flex justify-end">
                    <button 
                      type="button" 
                      class="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                      @click="resetForm"
                    >
                      Cancelar
                    </button>
                    <button 
                      type="button" 
                      class="ml-3 inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                      @click="scheduleClass"
                      :disabled="!isFormValid || isSubmitting"
                    >
                      {{ isSubmitting ? 'Agendando...' : 'Agendar Aula' }}
                    </button>
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
    
    // Convert date string to Date objects for Firestore query
    const startOfDay = new Date(attendanceDate.value);
    startOfDay.setHours(0, 0, 0, 0);
    
    const endOfDay = new Date(attendanceDate.value);
    endOfDay.setHours(23, 59, 59, 999);
    
    // Firestore query
    // Using a simpler approach that doesn't require a compound index
    // We'll filter by professor first, then filter the results by date in JavaScript
    const classesQuery = query(
      collection(db, 'scheduledClasses'),
      where('professorId', '==', authStore.userId)
    );
    
    const snapshot = await getDocs(classesQuery);
    
    // Filter for the correct date range in JavaScript
    const filteredDocs = snapshot.docs.filter(doc => {
      const data = doc.data();
      const classDate = data.date.toDate();
      return classDate >= startOfDay && classDate <= endOfDay;
    });
    
    console.log(`Found ${filteredDocs.length} classes for the selected date`);
    
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
    
    const classDate = new Date(selectedDate.value);
    // Parse time string (HH:MM) and set hours and minutes
    const [hours, minutes] = startTime.value.split(':').map(Number);
    classDate.setHours(hours, minutes, 0, 0);
    
    // Add to Firestore
    await addDoc(collection(db, 'scheduledClasses'), {
      studentId: selectedStudent.value,
      professorId: authStore.userId,
      date: Timestamp.fromDate(classDate),
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
    
    // Get the class date as a JavaScript Date object
    const classDate = classItem.date.toDate();
    
    // Create a unique ID for this attendance record (to prevent duplicates)
    const attendanceId = `${classItem.studentId}_${classItem.professorId}_${classDate.toISOString().split('T')[0]}`;
    
    // Also update or create a record in the attendanceRecords collection for earnings calculation
    if (present) {
      // Get student data to make sure we have all necessary info for earnings calculation
      const studentDoc = await getDoc(doc(db, 'users', classItem.studentId));
      const studentData = studentDoc.exists() ? studentDoc.data() : {};
      
      console.log('Student data for earnings:', {
        id: classItem.studentId,
        name: studentData.name,
        planId: studentData.planId
      });
      
      // If marked as present, create/update the attendance record
      // Include additional info to help with debugging
      const attendanceData = {
        studentId: classItem.studentId,
        professorId: classItem.professorId,
        studentName: studentData.name || 'Unknown',
        studentPlanId: studentData.planId, // Include plan ID for easier debugging
        date: classItem.date,
        present: true,
        updatedAt: Timestamp.now()
      };
      
      console.log('Creating attendance record with data:', attendanceData);
      await setDoc(doc(db, 'attendanceRecords', attendanceId), attendanceData);
      console.log('Attendance record created/updated for earnings calculation with ID:', attendanceId);
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
        console.log('Attendance record updated to absent');
      }
    }
    
    // Update local state
    const index = scheduledClasses.value.findIndex(item => item.id === classId);
    if (index !== -1) {
      scheduledClasses.value[index].present = present;
    }
    
    // Recalculate earnings in the attendance store
    console.log('About to recalculate earnings for professor:', authStore.userId);
    const professorDoc = await getDoc(doc(db, 'users', authStore.userId));
    console.log('Professor commission rate:', professorDoc.exists() ? professorDoc.data().commission : 'not set');
    
    const newEarnings = await attendanceStore.calculateMonthlyEarnings(authStore.userId);
    console.log('Earnings recalculated after attendance update. New earnings:', newEarnings);
    
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
