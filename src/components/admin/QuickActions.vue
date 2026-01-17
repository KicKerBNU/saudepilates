<template>
  <div class="mt-8 px-4 sm:px-0">
    <h2 class="text-lg font-medium text-gray-900 mb-4">{{ $t('admin.quickActions') }}</h2>
    <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      <router-link :to="{name: 'PaymentRegistration'}" class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0 bg-yellow-500 rounded-md p-3">
              <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <div class="ml-5 w-0 flex-1">
              <h3 class="text-lg font-medium text-gray-900">{{ $t('admin.registerPayment') }}</h3>
              <p class="mt-1 text-sm text-gray-500">
                {{ $t('admin.registerPaymentDesc') }}
              </p>
            </div>
          </div>
        </div>
      </router-link>

      <router-link :to="{name: 'PaymentVisualization'}" class="bg-white overflow-hidden shadow rounded-lg hover:bg-gray-50">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0 bg-purple-500 rounded-md p-3">
              <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
            </div>
            <div class="ml-5 w-0 flex-1">
              <h3 class="text-lg font-medium text-gray-900">{{ $t('admin.paymentVisualization') }}</h3>
              <p class="mt-1 text-sm text-gray-500">
                {{ $t('admin.paymentVisualizationDesc') }}
              </p>
            </div>
          </div>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
</script> 