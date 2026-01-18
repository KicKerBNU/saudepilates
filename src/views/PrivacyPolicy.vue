<template>
  <MetaTags
    title="Política de Privacidade - SaúdePilates | Sistema de Gestão para Pilates"
    description="Política de Privacidade do SaúdePilates. Saiba como coletamos, usamos e protegemos suas informações pessoais."
    keywords="política de privacidade, privacidade pilates, proteção de dados, LGPD, privacidade software pilates"
  />
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
    <div class="bg-white shadow-sm rounded-lg p-8">
      <h1 class="text-3xl font-bold text-gray-900 mb-8">{{ $t('privacy.title') }}</h1>
      
      <div class="space-y-6 text-gray-600">
        <section>
          <h2 class="text-xl font-semibold text-gray-900 mb-4">{{ $t('privacy.dataCollectionTitle') }}</h2>
          <p>{{ $t('privacy.dataCollectionText') }}</p>
          <ul class="list-disc ml-6 mt-2">
            <li>{{ $t('privacy.fullName') }}</li>
            <li>{{ $t('privacy.emailAddress') }}</li>
            <li>{{ $t('privacy.paymentInfo') }}</li>
            <li>{{ $t('privacy.healthData') }}</li>
          </ul>
        </section>

        <section>
          <h2 class="text-xl font-semibold text-gray-900 mb-4">{{ $t('privacy.dataUsageTitle') }}</h2>
          <p>{{ $t('privacy.dataUsageText') }}</p>
          <ul class="list-disc ml-6 mt-2">
            <li>{{ $t('privacy.processPayments') }}</li>
            <li>{{ $t('privacy.manageClasses') }}</li>
            <li>{{ $t('privacy.sendCommunications') }}</li>
            <li>{{ $t('privacy.improveServices') }}</li>
          </ul>
        </section>

        <section>
          <h2 class="text-xl font-semibold text-gray-900 mb-4">{{ $t('privacy.dataProtectionTitle') }}</h2>
          <p>{{ $t('privacy.dataProtectionText') }}</p>
          <ul class="list-disc ml-6 mt-2">
            <li>{{ $t('privacy.dataEncryption') }}</li>
            <li>{{ $t('privacy.firewalls') }}</li>
            <li>{{ $t('privacy.restrictedAccess') }}</li>
            <li>{{ $t('privacy.regularBackups') }}</li>
          </ul>
        </section>

        <section>
          <h2 class="text-xl font-semibold text-gray-900 mb-4">{{ $t('privacy.dataSharingTitle') }}</h2>
          <p>{{ $t('privacy.dataSharingText') }}</p>
          <ul class="list-disc ml-6 mt-2">
            <li>{{ $t('privacy.requiredByLaw') }}</li>
            <li>{{ $t('privacy.paymentProcessing') }}</li>
            <li>{{ $t('privacy.authorizedByYou') }}</li>
          </ul>
        </section>

        <section>
          <h2 class="text-xl font-semibold text-gray-900 mb-4">{{ $t('privacy.yourRightsTitle') }}</h2>
          <p>{{ $t('privacy.yourRightsText') }}</p>
          <ul class="list-disc ml-6 mt-2">
            <li>{{ $t('privacy.accessInfo') }}</li>
            <li>{{ $t('privacy.correctData') }}</li>
            <li>{{ $t('privacy.requestDeletion') }}</li>
            <li>{{ $t('privacy.withdrawConsent') }}</li>
          </ul>
        </section>
        
        <section>
          <h2 class="text-xl font-semibold text-gray-900 mb-4">{{ $t('privacy.contactTitle') }}</h2>
          <p>{{ $t('privacy.contactText', { email: 'saudepilatess@gmail.com' }) }}</p>
        </section>

        <section>
          <h2 class="text-xl font-semibold text-gray-900 mb-4">{{ $t('privacy.policyUpdatesTitle') }}</h2>
          <p>{{ $t('privacy.policyUpdatesText') }}</p>
        </section>
      </div>

      <div class="mt-8 pt-8 border-t border-gray-200">
        <p class="text-sm text-gray-500">{{ $t('privacy.lastUpdate') }} {{ new Date().toLocaleDateString() }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n';
import MetaTags from '@/components/MetaTags.vue';

const { t } = useI18n();
</script>