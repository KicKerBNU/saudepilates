<template>
  <Teleport to="body">
    <Transition name="cookie-fade">
      <div
        v-if="visible"
        class="cookie-consent-overlay"
        style="z-index: 2147483647; position: fixed;"
        role="dialog"
        aria-labelledby="cookie-consent-title"
        aria-modal="true"
      >
        <div class="cookie-consent-backdrop" @click="close"></div>
        <div class="cookie-consent-modal">
          <button
            type="button"
            class="cookie-consent-close"
            :aria-label="$t('common.close')"
            @click="accept"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>
          <p id="cookie-consent-title" class="cookie-consent-text">
            {{ $t('cookieConsent.message') }}
            <router-link to="/privacy" class="cookie-consent-link" @click="accept">
              {{ $t('cookieConsent.privacyPolicy') }}
            </router-link>.
          </p>
          <div class="cookie-consent-actions">
            <router-link
              to="/privacy"
              class="cookie-consent-preferences"
              @click="goToPreferences"
            >
              {{ $t('cookieConsent.configurePreferences') }}
            </router-link>
            <button type="button" class="cookie-consent-accept" @click="accept">
              {{ $t('cookieConsent.accept') }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const STORAGE_KEY = 'saudepilates_cookie_consent';
const visible = ref(false);

function getConsent() {
  try {
    return localStorage.getItem(STORAGE_KEY);
  } catch {
    return null;
  }
}

function setConsent(value) {
  try {
    localStorage.setItem(STORAGE_KEY, value);
  } catch (_) {}
}

function accept() {
  setConsent('accepted');
  visible.value = false;
}

function goToPreferences() {
  setConsent('preferences');
  visible.value = false;
}

function close() {
  visible.value = false;
}

onMounted(() => {
  if (!getConsent()) {
    visible.value = true;
  }
});
</script>

<style scoped>
.cookie-consent-overlay {
  position: fixed !important;
  inset: 0;
  z-index: 2147483647 !important;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  isolation: isolate;
}

.cookie-consent-backdrop {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 0;
}

.cookie-consent-modal {
  position: relative;
  z-index: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  max-width: 480px;
  width: 100%;
  padding: 1.5rem 2rem 1.5rem 1.5rem;
}

.cookie-consent-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  padding: 0.25rem;
  color: #6b7280;
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 6px;
  line-height: 0;
}

.cookie-consent-close:hover {
  color: #374151;
  background: #f3f4f6;
}

.cookie-consent-text {
  margin: 0 0 1.25rem;
  padding-right: 1.5rem;
  font-size: 0.9375rem;
  line-height: 1.6;
  color: #374151;
}

.cookie-consent-link {
  color: #6366f1;
  text-decoration: underline;
  font-weight: 500;
}

.cookie-consent-link:hover {
  color: #4f46e5;
}

.cookie-consent-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.cookie-consent-preferences {
  color: #6366f1;
  text-decoration: underline;
  font-size: 0.875rem;
  font-weight: 500;
}

.cookie-consent-preferences:hover {
  color: #4f46e5;
}

.cookie-consent-accept {
  padding: 0.5rem 1.25rem;
  background: #6366f1;
  color: white;
  font-size: 0.875rem;
  font-weight: 500;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.cookie-consent-accept:hover {
  background: #4f46e5;
}

/* Transition */
.cookie-fade-enter-active,
.cookie-fade-leave-active {
  transition: opacity 0.2s ease;
}

.cookie-fade-enter-from,
.cookie-fade-leave-to {
  opacity: 0;
}

.cookie-fade-enter-active .cookie-consent-modal,
.cookie-fade-leave-active .cookie-consent-modal {
  transition: transform 0.2s ease;
}

.cookie-fade-enter-from .cookie-consent-modal,
.cookie-fade-leave-to .cookie-consent-modal {
  transform: scale(0.96);
}
</style>
