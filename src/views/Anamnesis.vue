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

      <!-- List of anamneses (by date) -->
      <div class="mb-6 bg-white shadow overflow-hidden sm:rounded-lg">
        <div class="px-4 py-4 sm:px-6">
          <h2 class="text-lg font-medium text-gray-900 mb-3">{{ $t('anamnesis.listTitle') }}</h2>
          <p v-if="anamnesisListSorted.length === 0" class="text-sm text-gray-500">{{ $t('anamnesis.noAnamnesisYet') }}</p>
          <div v-else class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th scope="col" class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">{{ $t('anamnesis.performedAt') }}</th>
                  <th scope="col" class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">{{ $t('anamnesis.student') }}</th>
                  <th scope="col" class="px-4 py-2 text-right text-xs font-medium text-gray-500 uppercase">{{ $t('common.actions') }}</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr
                  v-for="item in anamnesisListSorted"
                  :key="item.id"
                  class="hover:bg-gray-50 cursor-pointer"
                  @click="selectAnamnesis(item)"
                >
                  <td class="px-4 py-3 text-sm text-gray-900 whitespace-nowrap">{{ formatAnamnesisDate(item.performedAt || item.updatedAt) }}</td>
                  <td class="px-4 py-3 text-sm text-gray-900">{{ item.studentName }}</td>
                  <td class="px-4 py-3 text-sm text-right">
                    <span class="text-indigo-600 hover:text-indigo-800 font-medium">{{ $t('anamnesis.viewEdit') }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
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
            <!-- List of anamneses for this student + New anamnese -->
            <div class="mb-6">
              <h3 class="text-sm font-medium text-gray-700 mb-2">{{ $t('anamnesis.listThisStudent') }}</h3>
              <div class="flex flex-wrap items-center gap-2">
                <button
                  type="button"
                  @click="startNewAnamnesis"
                  class="inline-flex items-center rounded-md border border-indigo-600 bg-white py-2 px-3 text-sm font-medium text-indigo-600 shadow-sm hover:bg-indigo-50"
                >
                  {{ $t('anamnesis.newAnamnesis') }}
                </button>
                <template v-for="a in studentAnamnesesList" :key="a.id">
                  <button
                    type="button"
                    @click="selectStudentAnamnesis(a)"
                    :class="[
                      'inline-flex items-center rounded-md py-2 px-3 text-sm font-medium',
                      selectedAnamnesisId === a.id
                        ? 'border border-indigo-600 bg-indigo-600 text-white'
                        : 'border border-gray-300 bg-white text-gray-700 hover:bg-gray-50'
                    ]"
                  >
                    {{ formatAnamnesisDate(a.performedAt || a.updatedAt) }}
                  </button>
                </template>
              </div>
            </div>

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
                  <label for="performedAt" class="block text-sm font-medium text-gray-700">{{ $t('anamnesis.performedAt') }}</label>
                  <input
                    id="performedAt"
                    v-model="form.performedAt"
                    type="date"
                    required
                    class="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                  />
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

                <div>
                  <label for="treatment" class="block text-sm font-medium text-gray-700">{{ $t('anamnesis.treatment') }}</label>
                  <textarea
                    id="treatment"
                    v-model="form.treatment"
                    rows="4"
                    class="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                    :placeholder="$t('anamnesis.treatmentPlaceholder')"
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

      <!-- Fallback print area (only used when popup is blocked) -->
      <div v-if="printData" id="anamnesis-print-fallback" class="anamnesis-print-document">
        <h1>{{ printData.title }}</h1>
        <p class="student-line">{{ printData.studentLabel }}: {{ printData.studentName }}</p>
        <div v-for="(section, i) in printData.sections" :key="i" class="section-block">
          <h2 class="section-title">{{ section.title }}</h2>
          <p class="section-content">{{ section.content }}</p>
        </div>
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
const selectedAnamnesisId = ref(null);
const studentAnamnesesList = ref([]);
const saveError = ref('');
const saveSuccess = ref(false);
const loadingAllPdf = ref(false);
const allAnamnesis = ref([]);
const printData = ref(null);

const form = reactive({
  performedAt: '',
  patientIdentification: '',
  mainComplaint: '',
  currentDiseaseHistory: '',
  socialHistory: '',
  perimeterData: '',
  posturalAssessment: '',
  treatment: ''
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
  return form.patientIdentification || form.mainComplaint || form.currentDiseaseHistory || form.socialHistory || form.perimeterData || form.posturalAssessment || form.treatment;
});

const anamnesisListSorted = computed(() => {
  const list = allAnamnesis.value
    .filter(a => studentsList.value.some(s => s.id === a.studentId))
    .map(a => ({
      ...a,
      studentName: studentsList.value.find(s => s.id === a.studentId)?.name || a.studentId
    }));
  list.sort((a, b) => {
    const dateA = a.performedAt || a.updatedAt || a.createdAt || '';
    const dateB = b.performedAt || b.updatedAt || b.createdAt || '';
    return dateB.localeCompare(dateA);
  });
  return list;
});

function formatAnamnesisDate(iso) {
  if (!iso) return '—';
  const d = iso.slice(0, 10);
  const [y, m, day] = d.split('-');
  return `${day}/${m}/${y}`;
}

function resetFormToDefault() {
  const today = new Date().toISOString().slice(0, 10);
  form.performedAt = today;
  form.patientIdentification = '';
  form.mainComplaint = '';
  form.currentDiseaseHistory = '';
  form.socialHistory = '';
  form.perimeterData = '';
  form.posturalAssessment = '';
  form.treatment = '';
}

function selectAnamnesis(item) {
  selectedStudentId.value = item.studentId;
  selectedAnamnesisId.value = item.id;
  loadAnamnesisIntoForm(item.id);
  anamnesisStore.getAnamnesesByStudentId(item.studentId).then(list => {
    studentAnamnesesList.value = list;
  });
}

async function loadAnamnesisIntoForm(anamnesisId) {
  const data = await anamnesisStore.getById(anamnesisId);
  if (!data) return;
  const today = new Date().toISOString().slice(0, 10);
  form.performedAt = (data.performedAt || (data.updatedAt || data.createdAt || '').slice(0, 10) || today).slice(0, 10);
  form.patientIdentification = data.patientIdentification || '';
  form.mainComplaint = data.mainComplaint || '';
  form.currentDiseaseHistory = data.currentDiseaseHistory || '';
  form.socialHistory = data.socialHistory || '';
  form.perimeterData = data.perimeterData || '';
  form.posturalAssessment = data.posturalAssessment || '';
  form.treatment = data.treatment || '';
}

function startNewAnamnesis() {
  selectedAnamnesisId.value = null;
  resetFormToDefault();
}

function selectStudentAnamnesis(a) {
  selectedAnamnesisId.value = a.id;
  loadAnamnesisIntoForm(a.id);
}

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
    studentAnamnesesList.value = [];
    selectedAnamnesisId.value = null;
    resetFormToDefault();
    return;
  }
  const list = await anamnesisStore.getAnamnesesByStudentId(selectedStudentId.value);
  studentAnamnesesList.value = list;
  selectedAnamnesisId.value = null;
  resetFormToDefault();
}

async function save() {
  if (!selectedStudentId.value) return;
  saveError.value = '';
  saveSuccess.value = false;
  try {
    const payload = {
      performedAt: form.performedAt,
      patientIdentification: form.patientIdentification,
      mainComplaint: form.mainComplaint,
      currentDiseaseHistory: form.currentDiseaseHistory,
      socialHistory: form.socialHistory,
      perimeterData: form.perimeterData,
      posturalAssessment: form.posturalAssessment,
      treatment: form.treatment
    };
    const result = await anamnesisStore.save(selectedStudentId.value, payload, selectedAnamnesisId.value);
    if (!selectedAnamnesisId.value) selectedAnamnesisId.value = result.id;
    saveSuccess.value = true;
    const list = await anamnesisStore.getAllForCompany();
    allAnamnesis.value = list;
    const studentList = await anamnesisStore.getAnamnesesByStudentId(selectedStudentId.value);
    studentAnamnesesList.value = studentList;
    setTimeout(() => { saveSuccess.value = false; }, 3000);
  } catch (err) {
    saveError.value = err.message || t('common.tryAgain');
  }
}

function downloadPdf() {
  generateSingleAnamnesisPdf(selectedStudentName.value, form, t);
}

function printAnamnesis() {
  const content = getPrintContent(selectedStudentName.value, form, t);
  const printWindow = window.open('', '_blank');
  if (!printWindow) {
    printData.value = content;
    requestAnimationFrame(() => {
      setTimeout(() => {
        window.print();
        setTimeout(() => { printData.value = null; }, 300);
      }, 200);
    });
    return;
  }
  const html = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>${escapeHtml(content.title)} - ${escapeHtml(content.studentName)}</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: system-ui, -apple-system, sans-serif; font-size: 14px; line-height: 1.5; color: #111; padding: 24px; max-width: 800px; margin: 0 auto; }
    h1 { font-size: 22px; font-weight: 700; margin-bottom: 8px; border-bottom: 2px solid #333; padding-bottom: 8px; }
    .student { font-size: 15px; font-weight: 600; margin-bottom: 24px; color: #374151; }
    .section { margin-bottom: 20px; page-break-inside: avoid; }
    .section-title { font-size: 14px; font-weight: 600; color: #1f2937; margin-bottom: 6px; }
    .section-content { font-size: 13px; color: #374151; white-space: pre-wrap; word-break: break-word; }
    .section-content:empty::before { content: "—"; color: #9ca3af; }
    @media print { body { padding: 16px; } }
  </style>
</head>
<body>
  <h1>${escapeHtml(content.title)}</h1>
  <p class="student">${escapeHtml(content.studentLabel || '')}: ${escapeHtml(content.studentName)}</p>
  ${content.sections.map(s => `
  <div class="section">
    <div class="section-title">${escapeHtml(s.title)}</div>
    <div class="section-content">${escapeHtml(s.content)}</div>
  </div>`).join('')}
</body>
</html>`;
  printWindow.document.write(html);
  printWindow.document.close();
  printWindow.focus();
  setTimeout(() => {
    printWindow.print();
    printWindow.close();
  }, 250);
}

function escapeHtml(text) {
  if (!text) return '';
  const el = document.createElement('div');
  el.textContent = text;
  return el.innerHTML;
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
/* Fallback print document: hidden off-screen when on screen */
.anamnesis-print-document {
  position: fixed;
  left: -9999px;
  top: 0;
  width: 210mm;
  max-width: 100%;
  padding: 20px;
  background: white;
  font-family: system-ui, -apple-system, sans-serif;
  font-size: 14px;
  line-height: 1.5;
  color: #111;
}
.anamnesis-print-document h1 {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 8px;
  border-bottom: 2px solid #333;
  padding-bottom: 8px;
}
.anamnesis-print-document .student-line {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #374151;
}
.anamnesis-print-document .section-block {
  margin-bottom: 20px;
}
.anamnesis-print-document .section-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 6px;
}
.anamnesis-print-document .section-content {
  font-size: 13px;
  color: #374151;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
<style>
/* Global print styles: hide page content, show only anamnesis when printing fallback */
@media print {
  body * {
    visibility: hidden !important;
  }
  .anamnesis-print-document,
  .anamnesis-print-document * {
    visibility: visible !important;
  }
  .anamnesis-print-document {
    position: absolute !important;
    left: 0 !important;
    top: 0 !important;
    width: 100% !important;
    max-width: none !important;
    padding: 16px !important;
  }
}
</style>
