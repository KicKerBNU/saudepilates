<template>
  <Teleport to="body">
    <Transition name="lead-fade">
      <div
        v-if="visible"
        class="lead-modal-overlay"
        style="z-index: 2147483646; position: fixed;"
        role="dialog"
        aria-labelledby="lead-modal-title"
        aria-modal="true"
      >
        <div class="lead-modal-backdrop" @click="close"></div>
        <div class="lead-modal-box">
          <button
            type="button"
            class="lead-modal-close"
            :aria-label="$t('common.close')"
            @click="close"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>

          <h2 id="lead-modal-title" class="lead-modal-title">{{ $t('leadModal.title') }}</h2>
          <p class="lead-modal-subtitle">{{ $t('leadModal.subtitle') }}</p>

          <form class="lead-modal-form" @submit.prevent="submit">
            <div class="lead-field">
              <input
                v-model="form.name"
                type="text"
                :placeholder="$t('leadModal.namePlaceholder')"
                required
                class="lead-input"
              />
            </div>
            <div class="lead-field">
              <label class="lead-label">{{ $t('leadModal.phoneWithDDD') }}</label>
              <div class="lead-phone-row">
                <select
                  v-model="form.phoneCountry"
                  class="lead-input lead-phone-code"
                  :title="$t('leadModal.countryCode')"
                >
                  <option v-for="c in countryCodes" :key="c.code" :value="c.code">
                    {{ c.code }} {{ c.label }}
                  </option>
                  <option value="">{{ $t('leadModal.other') }}</option>
                </select>
                <input
                  v-if="form.phoneCountry === ''"
                  v-model="form.phoneCountryCustom"
                  type="text"
                  :placeholder="$t('leadModal.otherCodePlaceholder')"
                  class="lead-input lead-phone-code-custom"
                  maxlength="5"
                />
                <input
                  v-model="form.phone"
                  type="tel"
                  :placeholder="$t('leadModal.phonePlaceholder')"
                  class="lead-input lead-phone-number"
                />
              </div>
            </div>
            <div class="lead-field">
              <input
                v-model="form.email"
                type="email"
                :placeholder="$t('leadModal.emailPlaceholder')"
                required
                class="lead-input"
              />
            </div>
            <div class="lead-field">
              <input
                v-model="form.businessType"
                type="text"
                :placeholder="$t('leadModal.businessTypePlaceholder')"
                class="lead-input"
              />
            </div>
            <div class="lead-field">
              <input
                v-model="form.instagram"
                type="text"
                :placeholder="$t('leadModal.instagramPlaceholder')"
                class="lead-input"
              />
            </div>
            <div class="lead-field lead-checkbox-field">
              <label class="lead-checkbox-label">
                <input v-model="form.privacyAccepted" type="checkbox" required class="lead-checkbox" />
                <span class="lead-checkbox-text">
                  {{ $t('leadModal.privacyPrefix') }}
                  <router-link to="/privacy" class="lead-privacy-link" @click="close">
                    {{ $t('leadModal.privacyLink') }}
                  </router-link>
                  {{ $t('leadModal.privacySuffix') }}
                </span>
              </label>
            </div>
            <button type="submit" :disabled="sending" class="lead-submit">
              {{ sending ? $t('leadModal.sending') : $t('leadModal.submit') }}
            </button>
          </form>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import emailjs from '@emailjs/browser';

const { t } = useI18n();

const VISIT_COUNT_KEY = 'saudepilates_lead_visit_count';
const LEAD_SUBMITTED_KEY = 'saudepilates_lead_submitted';
const MAX_VISITS_TO_SHOW = 3;
const EMAILJS_SERVICE = import.meta.env.VITE_EMAILJS_SERVICE_ID || 'service_6tlvlos';
const EMAILJS_TEMPLATE = import.meta.env.VITE_EMAILJS_TEMPLATE_ID || 'template_gfvy1ts';
const EMAILJS_PUBLIC_KEY = import.meta.env.VITE_EMAILJS_PUBLIC_KEY || '_DcuCvNtpYC2WN3Ia';
const LEAD_EMAIL = 'saudepilatess@gmail.com';

const visible = ref(false);
const sending = ref(false);

const countryCodes = [
  { code: '+55', label: 'Brasil' },
  { code: '+351', label: 'Portugal' },
  { code: '+34', label: 'Espanha' },
  { code: '+33', label: 'França' },
  { code: '+1', label: 'EUA/Canadá' },
  { code: '+44', label: 'Reino Unido' },
  { code: '+49', label: 'Alemanha' },
  { code: '+39', label: 'Itália' },
  { code: '+54', label: 'Argentina' },
  { code: '+52', label: 'México' },
  { code: '+57', label: 'Colômbia' },
  { code: '+56', label: 'Chile' },
  { code: '+31', label: 'Holanda' },
  { code: '+32', label: 'Bélgica' },
  { code: '+61', label: 'Austrália' },
  { code: '+353', label: 'Irlanda' },
  { code: '+41', label: 'Suíça' },
  { code: '+43', label: 'Áustria' },
  { code: '+45', label: 'Dinamarca' },
  { code: '+46', label: 'Suécia' },
  { code: '+47', label: 'Noruega' },
  { code: '+358', label: 'Finlândia' },
  { code: '+48', label: 'Polônia' },
  { code: '+30', label: 'Grécia' },
  { code: '+90', label: 'Turquia' },
  { code: '+27', label: 'África do Sul' },
  { code: '+971', label: 'Emirados Árabes' },
  { code: '+972', label: 'Israel' },
  { code: '+81', label: 'Japão' },
  { code: '+86', label: 'China' },
  { code: '+91', label: 'Índia' },
  { code: '+593', label: 'Equador' },
  { code: '+51', label: 'Peru' },
  { code: '+58', label: 'Venezuela' },
  { code: '+598', label: 'Uruguai' },
  { code: '+595', label: 'Paraguai' },
  { code: '+591', label: 'Bolívia' }
];

const form = reactive({
  name: '',
  phoneCountry: '+55',
  phoneCountryCustom: '',
  phone: '',
  email: '',
  businessType: '',
  instagram: '',
  privacyAccepted: false
});

function getVisitCount() {
  try {
    const n = parseInt(localStorage.getItem(VISIT_COUNT_KEY) || '0', 10);
    return isNaN(n) ? 0 : n;
  } catch {
    return 0;
  }
}

function incrementVisitCount() {
  try {
    const count = getVisitCount() + 1;
    localStorage.setItem(VISIT_COUNT_KEY, String(count));
    return count;
  } catch {
    return 0;
  }
}

function hasAlreadySubmitted() {
  try {
    return localStorage.getItem(LEAD_SUBMITTED_KEY) === 'true';
  } catch {
    return false;
  }
}

function close() {
  visible.value = false;
}

async function submit() {
  sending.value = true;
  try {
    let phonePrefix = form.phoneCountry || (form.phoneCountryCustom && form.phoneCountryCustom.trim()) || '';
    if (phonePrefix && !phonePrefix.startsWith('+')) phonePrefix = '+' + phonePrefix;
    const fullPhone = phonePrefix && form.phone ? `${phonePrefix} ${form.phone}` : form.phone || '';

    const message = [
      `Nome: ${form.name}`,
      `E-mail: ${form.email}`,
      `Telefone: ${fullPhone || '-'}`,
      `Tipo de negócio: ${form.businessType || '-'}`,
      `Instagram: ${form.instagram || '-'}`
    ].join('\n');

    const templateParams = {
      to_email: LEAD_EMAIL,
      from_name: form.name,
      from_email: form.email,
      email: form.email,
      phone: fullPhone,
      subject: `Novo lead - SaúdePilates - ${form.name}`,
      message
    };

    if (typeof emailjs.init === 'function') {
      emailjs.init(EMAILJS_PUBLIC_KEY);
    }
    await emailjs.send(EMAILJS_SERVICE, EMAILJS_TEMPLATE, templateParams, EMAILJS_PUBLIC_KEY);

    try {
      localStorage.setItem(LEAD_SUBMITTED_KEY, 'true');
    } catch (_) {}
    visible.value = false;
    form.name = '';
    form.phone = '';
    form.phoneCountryCustom = '';
    form.email = '';
    form.businessType = '';
    form.instagram = '';
    form.privacyAccepted = false;
  } catch (err) {
    console.error('Lead form send error:', err);
    const msg = (err && (err.text || err.message)) || '';
    const isGmailReconnect = /Invalid grant|reconnect your Gmail|Gmail_API/i.test(msg);
    if (typeof window !== 'undefined' && window.showErrorToast) {
      window.showErrorToast(isGmailReconnect ? t('leadModal.errorGmailReconnect') : (msg || t('leadModal.errorSending')));
    }
  } finally {
    sending.value = false;
  }
}

onMounted(() => {
  if (hasAlreadySubmitted()) {
    return;
  }
  const count = incrementVisitCount();
  if (count <= MAX_VISITS_TO_SHOW) {
    visible.value = true;
  }
});
</script>

<style scoped>
.lead-modal-overlay {
  position: fixed !important;
  inset: 0;
  z-index: 2147483646 !important;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  isolation: isolate;
}

.lead-modal-backdrop {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 0;
}

.lead-modal-box {
  position: relative;
  z-index: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  max-width: 440px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  padding: 1.75rem 1.5rem 1.5rem;
}

.lead-modal-close {
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

.lead-modal-close:hover {
  color: #374151;
  background: #f3f4f6;
}

.lead-modal-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #111;
  margin: 0 0 0.5rem;
}

.lead-modal-subtitle {
  font-size: 0.9375rem;
  color: #374151;
  margin: 0 0 1.25rem;
  line-height: 1.5;
}

.lead-modal-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.lead-field {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.lead-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.lead-input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.9375rem;
}

.lead-input:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
}

.lead-phone-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.lead-phone-code {
  min-width: 7rem;
  width: auto;
  flex-shrink: 0;
  cursor: pointer;
}

.lead-phone-code-custom {
  width: 4.5rem;
  flex-shrink: 0;
}

.lead-phone-number {
  flex: 1;
  min-width: 0;
}

.lead-checkbox-field {
  margin-top: 0.25rem;
}

.lead-checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  cursor: pointer;
  font-size: 0.8125rem;
  color: #374151;
}

.lead-checkbox {
  margin-top: 0.2rem;
  flex-shrink: 0;
}

.lead-checkbox-text {
  line-height: 1.4;
}

.lead-privacy-link {
  color: #6366f1;
  text-decoration: underline;
  font-weight: 500;
}

.lead-privacy-link:hover {
  color: #4f46e5;
}

.lead-submit {
  margin-top: 0.5rem;
  padding: 0.75rem 1.25rem;
  background: #6366f1;
  color: white;
  font-size: 0.9375rem;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.lead-submit:hover:not(:disabled) {
  background: #4f46e5;
}

.lead-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.lead-fade-enter-active,
.lead-fade-leave-active {
  transition: opacity 0.2s ease;
}

.lead-fade-enter-from,
.lead-fade-leave-to {
  opacity: 0;
}

.lead-fade-enter-from .lead-modal-box,
.lead-fade-leave-to .lead-modal-box {
  transform: scale(0.96);
}
</style>
