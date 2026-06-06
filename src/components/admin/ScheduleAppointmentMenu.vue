<template>
  <Teleport to="body">
    <div
      v-if="open"
      data-dropdown-menu
      class="fixed w-48 rounded-lg bg-white z-[9999]"
      :style="{
        ...dropdownStyle,
        boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04), 0 0 0 1px rgba(0, 0, 0, 0.05)'
      }"
    >
      <div class="py-1" role="menu" @click.stop>
        <button
          type="button"
          class="flex w-full items-center px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
          role="menuitem"
          @click.stop="$emit('register', appointment)"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="mr-2 h-4 w-4 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
          </svg>
          {{ $t('professor.registerAttendance') }}
        </button>
        <button
          type="button"
          class="flex w-full items-center px-4 py-2 text-left text-sm text-red-700 hover:bg-red-50"
          role="menuitem"
          @click.stop="$emit('delete', appointment)"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="mr-2 h-4 w-4 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
          </svg>
          {{ $t('common.delete') }}
        </button>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
defineProps({
  appointment: { type: Object, required: true },
  open: { type: Boolean, default: false },
  dropdownStyle: { type: Object, default: () => ({}) }
});

defineEmits(['register', 'delete']);
</script>
