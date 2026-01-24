<template>
  <div class="min-h-screen bg-gradient-to-b from-gray-50 to-white py-16 px-4 sm:px-6 lg:px-8">
    <MetaTags
      title="Entre em Contato - SaúdePilates | Sistema de Gestão para Pilates"
      description="Entre em contato com a equipe SaúdePilates. Tire suas dúvidas, solicite uma demonstração ou saiba mais sobre nosso sistema de gestão para estúdios de Pilates."
      keywords="contato pilates, demonstração software pilates, suporte pilates, atendimento estudio pilates"
    />
    <div class="max-w-lg w-full mx-auto space-y-10">
      <div>
        <h2 class="mt-6 text-center text-4xl font-extrabold text-gray-900 tracking-tight">
          {{ $t('contact.contactUs') }}
        </h2>
        <p class="mt-3 text-center text-lg text-gray-600">
          {{ $t('contact.sendMessage') }}
        </p>
      </div>
      
      <div v-if="messageSent" class="rounded-xl bg-green-50 p-6 shadow-sm border border-green-100">
        <div class="flex">
          <div class="flex-shrink-0">
            <svg class="h-8 w-8 text-green-400" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
            </svg>
          </div>
          <div class="ml-4">
            <h3 class="text-lg font-medium text-green-800">
              {{ $t('contact.messageSent') }}
            </h3>
            <div class="mt-2 text-base text-green-700">
              <p>
                {{ $t('contact.thankYou') }}
              </p>
            </div>
            <div class="mt-6">
              <button @click="resetForm" class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500">
                <svg class="-ml-1 mr-2 h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v3.586L7.707 9.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 10.586V7z" clip-rule="evenodd" />
                </svg>
                {{ $t('contact.sendAnother') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <form v-else class="mt-8 bg-white p-8 shadow-lg rounded-xl" @submit.prevent="sendEzmail" ref="contactForm">
        <div class="space-y-6">
          <div>
            <label for="name" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('contact.fullName') }}</label>
            <input id="name" name="name" type="text" v-model="formData.name" required class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" :placeholder="$t('contact.fullNamePlaceholder')" />
          </div>
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('contact.email') }}</label>
            <input id="email" name="email" type="email" v-model="formData.email" required class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" :placeholder="$t('contact.emailPlaceholder')" />
          </div>
          <div>
            <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('contact.phone') }}</label>
            <input id="phone" name="phone" type="tel" v-model="formData.phone" class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" :placeholder="$t('contact.phonePlaceholder')" />
          </div>
          <div>
            <label for="subject" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('contact.subject') }}</label>
            <select id="subject" name="subject" v-model="formData.subject" required class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm">
              <option value="" disabled selected>{{ $t('contact.selectSubject') }}</option>
              <option value="Demonstração">{{ $t('contact.requestDemo') }}</option>
              <option value="Preços">{{ $t('contact.pricingInfo') }}</option>
              <option value="Recursos">{{ $t('contact.featureQuestions') }}</option>
              <option value="Outros">{{ $t('contact.other') }}</option>
            </select>
          </div>
          <div>
            <label for="message" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('contact.message') }}</label>
            <textarea id="message" name="message" v-model="formData.message" required rows="5" class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm" :placeholder="$t('contact.messagePlaceholder')"></textarea>
          </div>
        </div>

        <div class="mt-8">
          <button type="submit" :disabled="sending" class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 shadow-sm transition-all duration-200 ease-in-out">
            <span v-if="sending" class="absolute left-0 inset-y-0 flex items-center pl-3">
              <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            </span>
            <span v-else class="absolute left-0 inset-y-0 flex items-center pl-3">
              <svg class="h-5 w-5 text-indigo-500 group-hover:text-indigo-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
              </svg>
            </span>
            {{ sending ? $t('contact.sending') : $t('contact.sendMessageButton') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import emailjs from '@emailjs/browser';
import MetaTags from '@/components/MetaTags.vue';
import { EmailService } from '@/services/email';

const { t } = useI18n();

const formData = reactive({
  name: '',
  email: '',
  phone: '',
  subject: '',
  message: ''
});

const contactForm = ref(null);
const sending = ref(false);
const messageSent = ref(false);

const sendEzmail = async () => {
  sending.value = true;
  
  try {
    // These values would come from your EmailJS account
    const serviceId = 'service_6tlvlos';
    const templateId = 'template_gfvy1ts';
    const publicKey = '_DcuCvNtpYC2WN3Ia';
    
    // Initialize EmailJS if not already initialized
    if (!emailjs.init) {
      emailjs.init(publicKey);
    }
    
    // Parameters for notification email to admin
    const templateParams = {
      to_email: 'evertonbuzzi@gmail.com',
      from_name: formData.name,
      from_email: formData.email,
      email: formData.email, // Adding this parameter as well for consistency
      phone: formData.phone,
      subject: formData.subject,
      message: formData.message
    };
    
    // 1. Send notification email to admin
    await emailjs.send(serviceId, templateId, templateParams, publicKey);
    
    // 2. Send auto-reply email to the user
    // Company information for the auto-reply email
    const companyInfo = {
      name: 'Saúde Pilates',
      logo: window.location.origin + '/lotusb2.png', // Adjust path as needed
      phone: '(47) 99914-1313', // Replace with your actual phone
      whatsapp: '+351912351771', // Replace with your actual WhatsApp
      website: window.location.origin,
      instagram: 'https://instagram.com/saudepilatesapp', // Replace with your social media
      facebook: 'https://facebook.com/saudepilatesapp' // Replace with your social media
    };
    
    // Send auto-reply using our service
    if (!formData.email) {
      throw new Error('Recipient email is empty. Cannot send auto-reply.');
    }
    
    // Try sending directly with emailjs instead of using the service
    const autoReplyParams = {
      to_name: formData.name || 'Cliente',
      from_name: 'Saúde Pilates',
      user_email: formData.email,
      email: formData.email,
      recipient: formData.email,
      to_email: formData.email,
      reply_to: formData.email,
      subject: 'Recebemos sua mensagem - Saúde Pilates',
      message: formData.message,
      company_logo: companyInfo.logo,
      company_phone: companyInfo.phone,
      company_whatsapp: companyInfo.whatsapp,
      website_url: companyInfo.website,
      instagram_url: companyInfo.instagram,
      facebook_url: companyInfo.facebook,
      whatsapp_url: companyInfo.whatsapp ? `https://wa.me/${companyInfo.whatsapp.replace(/\D/g, '')}` : '',
      current_year: new Date().getFullYear().toString()
    };
    
    
    // Send auto-reply directly using EmailJS
    await emailjs.send('service_6tlvlos', 'template_p043u8o', autoReplyParams, publicKey);
    
    // Mark message as sent
    messageSent.value = true;
  } catch (error) {
    console.error('Failed to send email:', error);
    console.error('Error details:', error.status, error.text);
    window.showErrorToast?.(t('contact.errorSending', { message: error.text || t('common.tryAgain') }));
  } finally {
    sending.value = false;
  }
};

const resetForm = () => {
  formData.name = '';
  formData.email = '';
  formData.phone = '';
  formData.subject = '';
  formData.message = '';
  messageSent.value = false;
};
</script>
