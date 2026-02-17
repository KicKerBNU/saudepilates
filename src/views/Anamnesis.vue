<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-4 px-4 sm:py-6 sm:px-6 lg:px-8">
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-900">{{ $t('anamnesis.title') }}</h1>
        <p class="mt-1 text-sm text-gray-500">{{ $t('anamnesis.subtitle') }}</p>
      </div>
    </header>

    <main class="max-w-7xl mx-auto py-4 sm:py-6 px-4 sm:px-6 lg:px-8">
      <div class="mb-4">
        <Breadcrumb :items="breadcrumbItems" />
      </div>

      <div class="bg-white shadow overflow-hidden sm:rounded-lg">
        <div class="px-4 py-4 sm:p-6">
          <!-- Student selection -->
          <div class="mb-6">
            <label for="student-select" class="block text-sm font-medium text-gray-700">{{ $t('anamnesis.selectStudent') }} *</label>
            <select
              id="student-select"
              v-model="selectedStudentId"
              @change="onStudentChange"
              class="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
              :disabled="loadingStudents"
            >
              <option value="">{{ $t('anamnesis.selectStudentPlaceholder') }}</option>
              <option v-for="s in studentsList" :key="s.id" :value="s.id">{{ s.name }}</option>
            </select>
          </div>

          <template v-if="selectedStudentId">
            <div v-if="anamnesisStore.loading" class="py-8 text-center text-gray-500">
              {{ $t('common.loading') }}
            </div>

            <template v-else>
              <form @submit.prevent="save" class="space-y-6">
                <div v-if="saveError" class="rounded-md bg-red-50 p-4 text-sm text-red-700">
                  {{ saveError }}
                </div>
                <div v-if="saveSuccess" class="rounded-md bg-green-50 p-4 text-sm text-green-700">
                  {{ $t('anamnesis.savedSuccess') }}
                </div>

                <div>
                  <label for="patientIdentification" class="block text-sm font-medium text-gray-700">{{ $t('anamnesis.patientIdentification') }}</label>
                  <textarea
                    id="patientIdentification"
                    v-model="form.patientIdentification"
                    rows="4"
                    class="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                    :placeholder="$t('anamnesis.patientIdentificationPlaceholder')"
                  />
                </div>

                <div>
                  <label for="mainComplaint" class="block text-sm font-medium text-gray-700">{{ $t('anamnesis.mainComplaint') }}</label>
                  <textarea
                    id="mainComplaint"
                    v-model="form.mainComplaint"
                    rows="4"
                    class="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                    :placeholder="$t('anamnesis.mainComplaintPlaceholder')"
                  />
                </div>

                <div>
                  <label for="currentDiseaseHistory" class="block text-sm font-medium text-gray-700">{{ $t('anamnesis.currentDiseaseHistory') }}</label>
                  <textarea
                    id="currentDiseaseHistory"
                    v-model="form.currentDiseaseHistory"
                    rows="4"
                    class="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                    :placeholder="$t('anamnesis.currentDiseaseHistoryPlaceholder')"
                  />
                </div>

                <div>
                  <label for="socialHistory" class="block text-sm font-medium text-gray-700">{{ $t('anamnesis.socialHistory') }}</label>
                  <textarea
                    id="socialHistory"
                    v-model="form.socialHistory"
                    rows="4"
                    class="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                    :placeholder="$t('anamnesis.socialHistoryPlaceholder')"
                  />
                </div>

                <div>
                  <label for="perimeterData" class="block text-sm font-medium text-gray-700">{{ $t('anamnesis.perimeterData') }}</label>
                  <textarea
                    id="perimeterData"
                    v-model="form.perimeterData"
                    rows="4"
                    class="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                    :placeholder="$t('anamnesis.perimeterDataPlaceholder')"
                  />
                </div>

                <div>
                  <label for="posturalAssessment" class="block text-sm font-medium text-gray-700">{{ $t('anamnesis.posturalAssessment') }}</label>
                  <textarea
                    id="posturalAssessment"
                    v-model="form.posturalAssessment"
                    rows="4"
                    class="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                    :placeholder="$t('anamnesis.posturalAssessmentPlaceholder')"
                  />
                </div>

                <div class="flex flex-wrap gap-3 pt-4">
                  <button
                    type="submit"
                    :disabled="anamnesisStore.loading"
                    class="inline-flex justify-center rounded-md border border-transparent bg-indigo-600 py-2 px-4 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
                  >
                    {{ anamnesisStore.loading ? $t('common.saving') : $t('common.save') }}
                  </button>
                  <button
                    type="button"
                    @click="downloadPdf"
                    :disabled="!hasAnamnesisData"
                    class="inline-flex justify-center rounded-md border border-gray-300 bg-white py-2 px-4 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
                  >
                    {{ $t('anamnesis.downloadPdf') }}
                  </button>
                  <button
                    type="button"
                    @click="printAnamnesis"
                    :disabled="!hasAnamnesisData"
                    class="inline-flex justify-center rounded-md border border-gray-300 bg-white py-2 px-4 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
                  >
                    {{ $t('anamnesis.print') }}
                  </button>
                </div>
              </form>
            </template>
          </template>
        </div>
      </div>

      <!-- Download all PDF -->
      <div class="mt-6 bg-white shadow overflow-hidden sm:rounded-lg">
        <div class="px-4 py-4 sm:p-6">
          <h2 class="text-lg font-medium text-gray-900 mb-2">{{ $t('anamnesis.downloadAllPdf') }}</h2>
          <p class="text-sm text-gray-500 mb-3">{{ $t('anamnesis.downloadAllPdfDesc') }}</p>
          <button
            type="button"
            @click="downloadAllPdf"
            :disabled="loadingAllPdf || allAnamnesis.length === 0"
            class="inline-flex justify-center rounded-md border border-transparent bg-indigo-600 py-2 px-4 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {{ loadingAllPdf ? $t('common.loading') : $t('anamnesis.downloadAllPdf') }}
          </button>
          <span v-if="allAnamnesis.length === 0 && !loadingAllPdf" class="ml-3 text-sm text-gray-500">{{ $t('anamnesis.noAnamnesisToExport') }}</span>
        </div>
      </div>

      <!-- Print-only content (off-screen until print) -->
      <div id="anamnesis-print-area" v-if="printData" ref="printArea" class="fixed -left-[9999px] top-0 w-full bg-white p-8 print:static print:left-0">
        <template v-if="printData">
          <h1 class="text-2xl font-bold mb-4">{{ printData.title }}</h1>
          <p class="font-semibold mb-6">{{ $t('anamnesis.student') }}: {{ printData.studentName }}</p>
          <div v-for="(section, i) in printData.sections" :key="i" class="mb-6">
            <h2 class="text-lg font-semibold mb-2">{{ section.title }}</h2>
            <p class="whitespace-pre-wrap text-sm">{{ section.content }}</p>
          </div>
        </template>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '@/stores/auth';
import { useStudentsStore } from '@/stores/students';
import { useAnamnesisStore } from '@/stores/anamnesis';
import { generateSingleAnamnesisPdf, generateAllAnamnesisPdf, getPrintContent } from '@/composables/useAnamnesisPdf';
import Breadcrumb from '@/components/Breadcrumb.vue';

const { t } = useI18n();
const route = useRoute();
const authStore = useAuthStore();
const studentsStore = useStudentsStore();
const anamnesisStore = useAnamnesisStore();

const studentsList = ref([]);
const loadingStudents = ref(true);
const selectedStudentId = ref('');
const saveError = ref('');
const saveSuccess = ref(false);
const loadingAllPdf = ref(false);
const allAnamnesis = ref([]);
const printArea = ref(null);
const printData = ref(null);

const form = reactive({
  patientIdentification: '',
  mainComplaint: '',
  currentDiseaseHistory: '',
  socialHistory: '',
  perimeterData: '',
  posturalAssessment: ''
});

const breadcrumbItems = computed(() => {
  const path = route.path;
  const segments = path.split('/').filter(Boolean);
  return segments.map((segment, index) => {
    const p = '/' + segments.slice(0, index + 1).join('/');
    const name = segment === 'admin' || segment === 'professor' ? (segment === 'admin' ? t('nav.adminDashboard') : t('nav.professorDashboard')) : t('anamnesis.title');
    return { name, path: p };
  });
});

const selectedStudentName = computed(() => {
  if (!selectedStudentId.value) return '';
  return studentsList.value.find(s => s.id === selectedStudentId.value)?.name || '';
});

const hasAnamnesisData = computed(() => {
  return form.patientIdentification || form.mainComplaint || form.currentDiseaseHistory || form.socialHistory || form.perimeterData || form.posturalAssessment;
});

async function loadStudents() {
  loadingStudents.value = true;
  try {
    if (authStore.isAdmin) {
      studentsList.value = await authStore.getUsersByCompany('student');
    } else {
      await studentsStore.fetchStudents();
      studentsList.value = studentsStore.students || [];
    }
  } catch (err) {
    console.error('Load students:', err);
  } finally {
    loadingStudents.value = false;
  }
}

async function onStudentChange() {
  saveError.value = '';
  saveSuccess.value = false;
  if (!selectedStudentId.value) {
    form.patientIdentification = '';
    form.mainComplaint = '';
    form.currentDiseaseHistory = '';
    form.socialHistory = '';
    form.perimeterData = '';
    form.posturalAssessment = '';
    return;
  }
  const data = await anamnesisStore.getByStudentId(selectedStudentId.value);
  if (data) {
    form.patientIdentification = data.patientIdentification || '';
    form.mainComplaint = data.mainComplaint || '';
    form.currentDiseaseHistory = data.currentDiseaseHistory || '';
    form.socialHistory = data.socialHistory || '';
    form.perimeterData = data.perimeterData || '';
    form.posturalAssessment = data.posturalAssessment || '';
  } else {
    form.patientIdentification = '';
    form.mainComplaint = '';
    form.currentDiseaseHistory = '';
    form.socialHistory = '';
    form.perimeterData = '';
    form.posturalAssessment = '';
  }
}

async function save() {
  if (!selectedStudentId.value) return;
  saveError.value = '';
  saveSuccess.value = false;
  try {
    await anamnesisStore.save(selectedStudentId.value, {
      patientIdentification: form.patientIdentification,
      mainComplaint: form.mainComplaint,
      currentDiseaseHistory: form.currentDiseaseHistory,
      socialHistory: form.socialHistory,
      perimeterData: form.perimeterData,
      posturalAssessment: form.posturalAssessment
    });
    saveSuccess.value = true;
    setTimeout(() => { saveSuccess.value = false; }, 3000);
  } catch (err) {
    saveError.value = err.message || t('common.tryAgain');
  }
}

function downloadPdf() {
  generateSingleAnamnesisPdf(selectedStudentName.value, form, t);
}

function printAnamnesis() {
  printData.value = getPrintContent(selectedStudentName.value, form, t);
  setTimeout(() => {
    window.print();
    printData.value = null;
  }, 100);
}

async function downloadAllPdf() {
  loadingAllPdf.value = true;
  try {
    const list = await anamnesisStore.getAllForCompany();
    allAnamnesis.value = list;
    const studentsMap = {};
    studentsList.value.forEach(s => { studentsMap[s.id] = s; });
    if (list.length === 0) return;
    generateAllAnamnesisPdf(list, studentsMap, t);
  } catch (err) {
    console.error('Download all PDF:', err);
  } finally {
    loadingAllPdf.value = false;
  }
}

onMounted(async () => {
  await loadStudents();
  const list = await anamnesisStore.getAllForCompany();
  allAnamnesis.value = list;
});
</script>

<style scoped>
@media print {
  body * { visibility: hidden; }
  #anamnesis-print-area,
  #anamnesis-print-area * { visibility: visible; }
  #anamnesis-print-area {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    background: white;
  }
}
</style>
