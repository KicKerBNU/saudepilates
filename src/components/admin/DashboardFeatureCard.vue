<template>
  <router-link
    :to="linkTo"
    class="group flex h-full flex-col overflow-hidden rounded-lg bg-white shadow transition-shadow hover:shadow-md"
  >
    <div class="flex flex-grow items-start gap-4 px-5 py-5 sm:p-6">
      <div :class="['flex-shrink-0 rounded-lg p-3', iconBgColor]">
        <svg class="h-6 w-6 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="iconPath" />
        </svg>
      </div>
      <div class="min-w-0 flex-1">
        <h3 class="text-base font-semibold text-gray-900">{{ title }}</h3>
        <p v-if="description" class="mt-1 text-sm text-gray-500">{{ description }}</p>
        <div v-if="showValue" class="mt-3">
          <div v-if="loading" class="text-2xl font-bold text-gray-900">
            <svg class="h-6 w-6 animate-spin text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
            </svg>
          </div>
          <p v-else class="text-2xl font-bold text-gray-900">{{ value }}</p>
        </div>
      </div>
    </div>
    <div class="mt-auto border-t border-gray-100 bg-gray-50 px-5 py-3 sm:px-6">
      <span class="text-sm font-medium text-indigo-600 group-hover:text-indigo-500">
        {{ linkText }} <span aria-hidden="true">&rarr;</span>
      </span>
    </div>
  </router-link>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  title: { type: String, required: true },
  linkTo: { type: [String, Object], required: true },
  linkText: { type: String, required: true },
  iconPath: { type: String, required: true },
  iconBgColor: { type: String, default: 'bg-indigo-500' },
  description: { type: String, default: '' },
  value: { type: [String, Number], default: null },
  loading: { type: Boolean, default: false }
});

const showValue = computed(() => props.value !== null && props.value !== undefined && props.value !== '');
</script>
