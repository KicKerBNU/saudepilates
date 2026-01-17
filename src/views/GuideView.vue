<script setup>
import { useI18n } from 'vue-i18n';
import SeoHead from '../components/SeoHead.vue';

const { t } = useI18n();
</script>

<template>
  <SeoHead 
    :title="$t('guide.title')"
    :description="$t('guide.description')"
    keywords="guia pilates, gestão estúdio pilates, administrador pilates, professor pilates, aluno pilates, gerenciamento pagamentos, agendamento aulas"
    ogType="article"
    canonicalPath="/guia"
  />
  
  <div class="guide-container max-w-5xl mx-auto px-4 py-8">
    <h1 class="text-3xl md:text-4xl font-bold mb-6 text-center">{{ $t('guide.completeGuide') }}</h1>
    
    <div class="mt-8">
      <div class="flex justify-center mb-8">
        <nav class="bg-gray-100 p-2 rounded-lg">
          <ul class="flex space-x-4">
            <li><a href="#admin" class="px-3 py-2 rounded hover:bg-gray-200">{{ $t('guide.admin') }}</a></li>
            <li><a href="#professor" class="px-3 py-2 rounded hover:bg-gray-200">{{ $t('guide.professor') }}</a></li>
            <li><a href="#student" class="px-3 py-2 rounded hover:bg-gray-200">{{ $t('guide.student') }}</a></li>
          </ul>
        </nav>
      </div>

      <!-- Introdução -->
      <section class="mb-12">
        <p class="mb-4">
          {{ $t('guide.introduction') }}
        </p>
      </section>

      <!-- Jornada do Administrador -->
      <section id="admin" class="mb-12">
        <h2 class="text-2xl md:text-3xl font-semibold mb-4 border-b pb-2">{{ $t('guide.adminJourney') }}</h2>
        
        <div class="space-y-6">
          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.adminDashboard') }}</h3>
            <p>
              {{ $t('guide.adminDashboardDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.plansManagement') }}</h3>
            <p>
              {{ $t('guide.plansManagementDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.professorsManagement') }}</h3>
            <p>
              {{ $t('guide.professorsManagementDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.studentsManagement') }}</h3>
            <p>
              {{ $t('guide.studentsManagementDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.paymentRegistration') }}</h3>
            <p>
              {{ $t('guide.paymentRegistrationDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.monthlyPayments') }}</h3>
            <p>
              {{ $t('guide.monthlyPaymentsDesc') }}
            </p>
          </div>
        </div>
      </section>

      <!-- Jornada do Professor -->
      <section id="professor" class="mb-12">
        <h2 class="text-2xl md:text-3xl font-semibold mb-4 border-b pb-2">{{ $t('guide.professorJourney') }}</h2>
        
        <div class="space-y-6">
          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.professorDashboard') }}</h3>
            <p>
              {{ $t('guide.professorDashboardDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.studentsManagementProf') }}</h3>
            <p>
              {{ $t('guide.studentsManagementProfDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.earningsHistory') }}</h3>
            <p>
              {{ $t('guide.earningsHistoryDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.attendanceControl') }}</h3>
            <p>
              {{ $t('guide.attendanceControlDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.studentEvolution') }}</h3>
            <p>
              {{ $t('guide.studentEvolutionDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.schedule') }}</h3>
            <p>
              {{ $t('guide.scheduleDesc') }}
            </p>
          </div>
        </div>
      </section>

      <!-- Jornada do Aluno -->
      <section id="student" class="mb-12">
        <h2 class="text-2xl md:text-3xl font-semibold mb-4 border-b pb-2">{{ $t('guide.studentJourney') }}</h2>
        
        <div class="space-y-6">
          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.studentDashboard') }}</h3>
            <p>
              {{ $t('guide.studentDashboardDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.classScheduling') }}</h3>
            <p>
              {{ $t('guide.classSchedulingDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.classHistory') }}</h3>
            <p>
              {{ $t('guide.classHistoryDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.progressEvolution') }}</h3>
            <p>
              {{ $t('guide.progressEvolutionDesc') }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-semibold mb-2">{{ $t('guide.payments') }}</h3>
            <p>
              {{ $t('guide.paymentsDesc') }}
            </p>
          </div>
        </div>
      </section>

      <!-- Conclusão -->
      <section class="mb-8">
        <h2 class="text-2xl font-semibold mb-4">{{ $t('guide.conclusion') }}</h2>
        <p>
          {{ $t('guide.conclusionText') }}
        </p>
        <p class="mt-4">
          {{ $t('guide.conclusionContact') }} <a href="/contact" class="text-blue-600 hover:underline">{{ $t('guide.contactPage') }}</a>.
        </p>
      </section>
    </div>
  </div>
</template>

<style scoped>
.guide-container h2 {
  color: #1e40af;
}

.guide-container h3 {
  color: #2563eb;
}

.guide-container a {
  color: #3b82f6;
  text-decoration: none;
}

.guide-container a:hover {
  text-decoration: underline;
}
</style> 