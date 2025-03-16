<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">Registrar Pagamento</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="bg-white shadow overflow-hidden sm:rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <!-- Payment Registration Form -->
          <form @submit.prevent="registerPayment" class="space-y-8">
            <div class="space-y-6">
              <!-- Student Selection -->
              <div>
                <label for="student" class="block text-sm font-medium text-gray-700">Aluno *</label>
                <div class="mt-1 relative">
                  <div v-if="loading" class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <svg class="animate-spin h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                  </div>
                  <select
                    id="student"
                    v-model="selectedStudentId"
                    @change="loadStudentDetails"
                    class="mt-1 block w-full py-3 px-4 text-base bg-white border border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md shadow-sm"
                    required
                  >
                    <option value="" disabled>Selecione um aluno</option>
                    <option v-for="student in students" :key="student.id" :value="student.id" class="py-2">
                      {{ student.name || `${student.firstName || ''} ${student.lastName || ''}`.trim() }}
                    </option>
                  </select>
                  <div v-if="students.length === 0 && !loading" class="mt-2 text-sm text-red-600">
                    Nenhum aluno encontrado. Por favor, cadastre alunos primeiro.
                  </div>
                </div>
              </div>

              <!-- Student Details (shown once a student is selected) -->
              <div v-if="selectedStudent" class="bg-gray-50 p-4 rounded-lg">
                <h3 class="text-lg font-medium text-gray-900 mb-3">Detalhes do Aluno</h3>
                
                <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <div>
                    <p class="text-sm font-medium text-gray-500">Nome completo</p>
                    <p class="mt-1 text-sm text-gray-900">{{ selectedStudent.name || `${selectedStudent.firstName || ''} ${selectedStudent.lastName || ''}`.trim() }}</p>
                  </div>
                  
                  <div>
                    <p class="text-sm font-medium text-gray-500">Email</p>
                    <p class="mt-1 text-sm text-gray-900">{{ selectedStudent.email }}</p>
                  </div>
                  
                  <div>
                    <p class="text-sm font-medium text-gray-500">Plano atual</p>
                    <p class="mt-1 text-sm text-gray-900">
                      <span v-if="selectedStudent.plan">{{ selectedStudent.plan.title }} (R$ {{ selectedStudent.plan.price.toFixed(2) }}/mês)</span>
                      <span v-else class="text-yellow-600">Sem plano definido</span>
                    </p>
                  </div>
                  
                  <div>
                    <p class="text-sm font-medium text-gray-500">Professor</p>
                    <p class="mt-1 text-sm text-gray-900">
                      <span v-if="professorName">{{ professorName }}</span>
                      <span v-else class="text-yellow-600">Sem professor designado</span>
                    </p>
                  </div>
                </div>
              </div>

              <!-- Payment Details (shown once a student is selected) -->
              <div v-if="selectedStudent && selectedStudent.plan" class="border border-gray-200 rounded-lg p-4">
                <h3 class="text-lg font-medium text-gray-900 mb-4">Detalhes do Pagamento</h3>
                
                <div class="space-y-4">
                  <!-- Payment Reference Period -->
                  <div>
                    <label for="paymentDate" class="block text-sm font-medium text-gray-700">Data de pagamento *</label>
                    <input
                      type="date"
                      id="paymentDate"
                      v-model="paymentData.paymentDate"
                      class="mt-1 block w-full pl-3 pr-10 py-2 text-base border border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
                      required
                    />
                  </div>

                  <!-- Payment Period Selection -->
                  <div>
                    <label for="paymentPeriod" class="block text-sm font-medium text-gray-700">Período de pagamento *</label>
                    <select
                      id="paymentPeriod"
                      v-model="paymentData.periodMonths"
                      @change="calculatePaymentAmount"
                      class="mt-1 block w-full pl-3 pr-10 py-2 text-base border border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
                      required
                    >
                      <option value="1">Mensal (1 mês)</option>
                      <option value="3">Trimestral (3 meses)</option>
                      <option value="6">Semestral (6 meses)</option>
                      <option value="12">Anual (12 meses)</option>
                    </select>
                  </div>

                  <!-- Payment Amount with Plan and Discount details -->
                  <div class="bg-gray-50 p-4 rounded-lg">
                    <div class="flex justify-between items-center mb-2">
                      <span class="text-sm font-medium text-gray-700">Valor base do plano:</span>
                      <span class="text-sm text-gray-900">R$ {{ originalAmount.toFixed(2) }}</span>
                    </div>
                    
                    <div v-if="discount > 0" class="flex justify-between items-center mb-2">
                      <span class="text-sm font-medium text-gray-700">Desconto aplicado:</span>
                      <span class="text-sm text-green-600">-R$ {{ discountAmount.toFixed(2) }} ({{ discount }}%)</span>
                    </div>
                    
                    <div class="flex justify-between items-center pt-2 border-t border-gray-200">
                      <span class="text-base font-medium text-gray-900">Valor final a pagar:</span>
                      <span class="text-base font-bold text-gray-900">R$ {{ finalAmount.toFixed(2) }}</span>
                    </div>

                    <div v-if="paymentData.periodMonths > 1" class="mt-2 text-sm text-gray-500">
                      Pagamento referente a {{ paymentData.periodMonths }} meses ({{ getMonthRangeText() }})
                    </div>
                  </div>

                  <!-- Additional Information -->
                  <div>
                    <label for="notes" class="block text-sm font-medium text-gray-700">Observações (opcional)</label>
                    <div class="mt-1">
                      <textarea
                        id="notes"
                        v-model="paymentData.notes"
                        rows="3"
                        class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border border-gray-300 rounded-md px-4 py-3"
                        placeholder="Adicione qualquer observação relevante sobre o pagamento..."
                      ></textarea>
                    </div>
                  </div>


                </div>
              </div>
            </div>

            <!-- Form Actions -->
            <div class="flex justify-end space-x-3">
              <router-link
                to="/admin"
                class="inline-flex justify-center py-2 px-4 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Cancelar
              </router-link>
              <button
                type="submit"
                :disabled="loading || !canSubmit"
                class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:bg-indigo-400 disabled:cursor-not-allowed"
              >
                <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                {{ loading ? 'Processando...' : 'Registrar Pagamento' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStudentsStore } from '../../stores/students';
import { useProfessorsStore } from '../../stores/professors';
import { usePaymentsStore } from '../../stores/payments';
import { useAuthStore } from '../../stores/auth';
import { collection, query, where, getDocs, getDoc, doc } from 'firebase/firestore';
import { db } from '../../firebase/config';

const router = useRouter();
const studentsStore = useStudentsStore();
const professorsStore = useProfessorsStore();
const paymentsStore = usePaymentsStore();

// State
const loading = ref(false);
const students = ref([]);
const professors = ref([]);
const selectedStudentId = ref('');
const selectedStudent = ref(null);
const professorName = ref('');

// Payment data
const paymentData = ref({
  studentId: '',
  professorId: '',
  planId: '',
  paymentDate: new Date().toISOString().split('T')[0],
  periodMonths: 1,
  originalAmount: 0,
  discountPercent: 0,
  discountAmount: 0,
  finalAmount: 0,

  notes: ''
});

// Computed properties
const originalAmount = computed(() => {
  if (!selectedStudent.value || !selectedStudent.value.plan) return 0;
  return selectedStudent.value.plan.price * paymentData.value.periodMonths;
});

const discount = computed(() => {
  if (!selectedStudent.value || !selectedStudent.value.plan) return 0;
  
  // Get discount based on the payment period
  const plan = selectedStudent.value.plan;
  switch (parseInt(paymentData.value.periodMonths)) {
    case 3: return plan.discountQuarterly || 0;
    case 6: return plan.discountSemiannual || 0;
    case 12: return plan.discountAnnual || 0;
    default: return 0;
  }
});

const discountAmount = computed(() => {
  return (originalAmount.value * discount.value) / 100;
});

const finalAmount = computed(() => {
  return originalAmount.value - discountAmount.value;
});

const commissionPercent = computed(() => {
  if (!selectedStudent.value || !selectedStudent.value.plan) return 0;
  return selectedStudent.value.plan.commission || 40; // Default commission is 40%
});

const commissionAmount = computed(() => {
  // Calculate commission based on the FINAL amount (after discount)
  return (finalAmount.value * commissionPercent.value) / 100;
});

const canSubmit = computed(() => {
  return selectedStudent.value && 
         selectedStudent.value.plan && 
         paymentData.value.paymentDate && 
         finalAmount.value > 0;
});

// Methods
const fetchStudents = async () => {
  loading.value = true;
  try {
    // Get current admin's company ID from auth store
    const authStore = useAuthStore();
    const companyId = authStore.companyId;
    
    // Validate company ID - prevent loading students from other companies
    if (!companyId) {
      console.error('No company ID found for current user');
      students.value = [];
      return;
    }
    
    // Direct Firestore query to get student users filtered by company
    const studentsQuery = query(
      collection(db, 'users'),
      where('role', '==', 'student'),
      where('companyId', '==', companyId)
    );
    
    
    const snapshot = await getDocs(studentsQuery);
    
    
    // Map the documents to our data structure
    const fetchedStudents = snapshot.docs.map(doc => {
      const data = doc.data();
      return {
        id: doc.id,
        ...data,
      };
    });
    
    // Filter active students
    students.value = fetchedStudents.filter(student => student.isActive !== false);
    
    // For each student with a plan, fetch the plan details
    const studentsWithPlans = await Promise.all(students.value.map(async (student) => {
      if (student.planId) {
        try {
          const planDoc = await getDoc(doc(db, 'plans', student.planId));
          if (planDoc.exists()) {
            return {
              ...student,
              plan: { id: planDoc.id, ...planDoc.data() }
            };
          }
        } catch (err) {
          console.error(`Error fetching plan for student ${student.id}:`, err);
        }
      }
      return student;
    }));
    
    students.value = studentsWithPlans;
  } catch (error) {
    console.error('Error in fetchStudents:', error);
    students.value = [];
  } finally {
    loading.value = false;
  }
};

const fetchProfessors = async () => {
  loading.value = true;
  try {
    const fetchedProfessors = await professorsStore.fetchProfessors();
    professors.value = fetchedProfessors;
  } catch (error) {
    console.error('Error fetching professors:', error);
  } finally {
    loading.value = false;
  }
};

const loadStudentDetails = async () => {
  if (!selectedStudentId.value) {
    selectedStudent.value = null;
    return;
  }
  
  loading.value = true;
  try {
    // Get the student's full details
    const studentDetails = await studentsStore.getStudentDetails(selectedStudentId.value);
    
    // If student has a plan, fetch the plan details
    if (studentDetails.planId) {
      const planDetails = await studentsStore.fetchPlanById(studentDetails.planId);
      studentDetails.plan = planDetails;
    }
    
    selectedStudent.value = studentDetails;
    
    // Update payment data with student info
    paymentData.value.studentId = studentDetails.id;
    paymentData.value.professorId = studentDetails.professorId || '';
    paymentData.value.planId = studentDetails.planId || '';
    
    // Get professor name if assigned
    if (studentDetails.professorId) {
      try {
        // First try to find in the local array
        const professor = professors.value.find(p => p.id === studentDetails.professorId);
        
        if (professor) {
          // Use local data if found
          professorName.value = professor.name || `${professor.firstName || ''} ${professor.lastName || ''}`.trim();
        } else {
          // Directly query Firestore for the professor
          const professorDoc = await getDoc(doc(db, 'users', studentDetails.professorId));
          
          if (professorDoc.exists()) {
            const professorData = professorDoc.data();
            professorName.value = professorData.name || 
              `${professorData.firstName || ''} ${professorData.lastName || ''}`.trim();
          } else {
            professorName.value = 'Professor não encontrado';
          }
        }
      } catch (error) {
        console.error('Error fetching professor details:', error);
        professorName.value = 'Erro ao carregar professor';
      }
    } else {
      professorName.value = '';
    }
    
    // Calculate payment amount
    calculatePaymentAmount();
  } catch (error) {
    console.error('Error loading student details:', error);
  } finally {
    loading.value = false;
  }
};

const calculatePaymentAmount = () => {
  if (!selectedStudent.value || !selectedStudent.value.plan) return;
  
  // Update payment data calculations
  paymentData.value.originalAmount = originalAmount.value;
  paymentData.value.discountPercent = discount.value;
  paymentData.value.discountAmount = discountAmount.value;
  paymentData.value.finalAmount = finalAmount.value;
  paymentData.value.commissionPercent = commissionPercent.value;
  paymentData.value.commissionAmount = commissionAmount.value;
};

const getMonthRangeText = () => {
  const startDate = new Date(paymentData.value.paymentDate);
  const monthNames = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];
  
  let text = monthNames[startDate.getMonth()] + '/' + startDate.getFullYear();
  
  if (paymentData.value.periodMonths > 1) {
    const endDate = new Date(startDate);
    endDate.setMonth(endDate.getMonth() + parseInt(paymentData.value.periodMonths) - 1);
    text += ' até ' + monthNames[endDate.getMonth()] + '/' + endDate.getFullYear();
  }
  
  return text;
};

const registerPayment = async () => {
  if (!canSubmit.value) return;
  
  loading.value = true;
  try {
    // Prepare payment data
    const paymentToSave = {
      ...paymentData.value,
      paymentPeriod: getMonthRangeText()
    };
    
    // Register student payment
    await paymentsStore.addStudentPayment(paymentToSave);
    
    // Also create a professor payment record if there's a professor assigned
    if (selectedStudent.value.professorId && commissionAmount.value > 0) {
      const professorPayment = {
        professorId: selectedStudent.value.professorId,
        studentId: selectedStudent.value.id,
        planId: selectedStudent.value.planId,
        paymentDate: paymentData.value.paymentDate,
        amount: paymentData.value.commissionAmount,
        originalStudentPayment: paymentData.value.finalAmount,
        notes: `Comissão referente ao pagamento de ${paymentData.value.periodMonths} ${paymentData.value.periodMonths === 1 ? 'mês' : 'meses'} do aluno ${selectedStudent.value.firstName} ${selectedStudent.value.lastName}`
      };
      
      await paymentsStore.addProfessorPayment(professorPayment);
    }
    
    // Redirect back to admin dashboard with success message
    router.push({ path: '/admin', query: { paymentSuccess: 'true' } });
  } catch (error) {
    console.error('Error registering payment:', error);
    alert('Erro ao registrar pagamento: ' + error.message);
  } finally {
    loading.value = false;
  }
};

// Lifecycle hooks
onMounted(async () => {
  try {
    await fetchStudents();
    await fetchProfessors();
    
  } catch (error) {
    console.error('Error during component initialization:', error);
  }
});
</script>
