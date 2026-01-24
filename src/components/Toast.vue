<template>
  <Teleport to="body">
    <div class="fixed top-4 right-4 z-[10000] space-y-2">
      <TransitionGroup name="toast" tag="div">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          :class="[
            'min-w-[300px] max-w-md rounded-lg shadow-lg p-4 flex items-start gap-3',
            toast.type === 'success' ? 'bg-green-50 border border-green-200' : '',
            toast.type === 'error' ? 'bg-red-50 border border-red-200' : '',
            toast.type === 'warning' ? 'bg-yellow-50 border border-yellow-200' : '',
            toast.type === 'info' ? 'bg-blue-50 border border-blue-200' : ''
          ]"
          style="box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04), 0 0 0 1px rgba(0, 0, 0, 0.05);"
        >
          <!-- Icon -->
          <div :class="[
            'flex-shrink-0',
            toast.type === 'success' ? 'text-green-600' : '',
            toast.type === 'error' ? 'text-red-600' : '',
            toast.type === 'warning' ? 'text-yellow-600' : '',
            toast.type === 'info' ? 'text-blue-600' : ''
          ]">
            <svg v-if="toast.type === 'success'" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <svg v-else-if="toast.type === 'error'" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <svg v-else-if="toast.type === 'warning'" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <svg v-else class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          
          <!-- Content -->
          <div class="flex-1 min-w-0">
            <p :class="[
              'text-sm font-medium',
              toast.type === 'success' ? 'text-green-800' : '',
              toast.type === 'error' ? 'text-red-800' : '',
              toast.type === 'warning' ? 'text-yellow-800' : '',
              toast.type === 'info' ? 'text-blue-800' : ''
            ]">
              {{ toast.message }}
            </p>
          </div>
          
          <!-- Close button -->
          <button
            @click="removeToast(toast.id)"
            :class="[
              'flex-shrink-0 text-gray-400 hover:text-gray-600 focus:outline-none',
              toast.type === 'success' ? 'hover:text-green-600' : '',
              toast.type === 'error' ? 'hover:text-red-600' : '',
              toast.type === 'warning' ? 'hover:text-yellow-600' : '',
              toast.type === 'info' ? 'hover:text-blue-600' : ''
            ]"
          >
            <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { Teleport } from 'vue';
import { registerToastInstance } from '../composables/useToast';

const toasts = ref([]);
let toastIdCounter = 0;

const addToast = (message, type = 'info', duration = 5000) => {
  const id = ++toastIdCounter;
  const toast = {
    id,
    message,
    type,
    duration
  };
  
  toasts.value.push(toast);
  
  // Auto remove after duration
  if (duration > 0) {
    setTimeout(() => {
      removeToast(id);
    }, duration);
  }
  
  return id;
};

const removeToast = (id) => {
  const index = toasts.value.findIndex(t => t.id === id);
  if (index > -1) {
    toasts.value.splice(index, 1);
  }
};

// Register this instance with the composable
onMounted(() => {
  registerToastInstance({ addToast });
  
  // Also expose methods globally for backward compatibility
  if (typeof window !== 'undefined') {
    window.showToast = addToast;
    window.showSuccessToast = (message, duration) => addToast(message, 'success', duration);
    window.showErrorToast = (message, duration) => addToast(message, 'error', duration);
    window.showWarningToast = (message, duration) => addToast(message, 'warning', duration);
    window.showInfoToast = (message, duration) => addToast(message, 'info', duration);
  }
});

onUnmounted(() => {
  if (typeof window !== 'undefined') {
    delete window.showToast;
    delete window.showSuccessToast;
    delete window.showErrorToast;
    delete window.showWarningToast;
    delete window.showInfoToast;
  }
  registerToastInstance(null);
});
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.toast-move {
  transition: transform 0.3s ease;
}
</style>
