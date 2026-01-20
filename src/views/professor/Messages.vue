<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">{{ $t('professor.studentMessages') }}</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="px-4 py-6 sm:px-0">
        <Breadcrumb :items="breadcrumbItems" />
      </div>

      <div class="px-4 py-6 sm:px-0">
        <!-- Search and Filter Section -->
        <div class="bg-white shadow rounded-lg mb-6">
          <div class="px-4 py-5 sm:p-6">
            <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div class="space-y-2">
                <label for="search" class="block text-sm font-medium text-gray-700">{{ $t('professor.searchMessages') }}</label>
                <div class="relative rounded-md shadow-sm">
                  <input
                    type="text"
                    id="search"
                    v-model="searchQuery"
                    class="block w-full px-4 py-3 rounded-md border-gray-300 shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                    :placeholder="$t('professor.searchMessagesPlaceholder')"
                  />
                  <div class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                    </svg>
                  </div>
                </div>
              </div>
              <div class="space-y-2">
                <label for="status" class="block text-sm font-medium text-gray-700">{{ $t('common.status') }}</label>
                <select
                  id="status"
                  v-model="filterStatus"
                  class="block w-full px-4 py-3 rounded-md border-gray-300 shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                >
                  <option value="all">{{ $t('professor.allMessages') }}</option>
                  <option value="unread">{{ $t('professor.unreadMessages') }}</option>
                  <option value="read">{{ $t('professor.readMessages') }}</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        <!-- Messages List -->
        <div class="bg-white shadow rounded-lg">
          <div v-if="loading" class="flex justify-center items-center py-12">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
          </div>
          
          <div v-else-if="filteredMessages.length === 0" class="py-12 text-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-400 mx-auto mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
            <p class="text-gray-500">{{ $t('professor.noMessagesFound') }}</p>
          </div>

          <ul v-else class="divide-y divide-gray-200">
            <li v-for="message in filteredMessages" :key="message.id" class="hover:bg-gray-50">
              <div class="px-4 py-4 sm:px-6">
                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <div :class="[message.isRead ? 'bg-gray-200' : 'bg-indigo-600']" class="w-2 h-2 rounded-full mr-3"></div>
                    <p class="text-sm font-medium text-indigo-600 truncate">
                      {{ message.studentName }}
                    </p>
                  </div>
                  <div class="ml-2 flex-shrink-0 flex">
                    <p class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                       :class="message.isRead ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'">
                      {{ message.isRead ? $t('professor.read') : $t('professor.unread') }}
                    </p>
                  </div>
                </div>
                <div class="mt-2 sm:flex sm:justify-between">
                  <div class="sm:flex">
                    <p class="flex items-center text-sm text-gray-700">
                      {{ message.text }}
                    </p>
                  </div>
                  <div class="mt-2 flex items-center text-sm text-gray-500 sm:mt-0">
                    <svg class="flex-shrink-0 mr-1.5 h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <p>
                      {{ formatDateTime(message.createdAt) }}
                    </p>
                  </div>
                </div>
                <div class="mt-2 flex space-x-2">
                  <button
                    v-if="!message.isRead"
                    @click="markAsRead(message.id)"
                    class="inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded text-indigo-700 bg-indigo-100 hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    {{ $t('professor.markAsRead') }}
                  </button>
                  <button
                    @click="replyToStudent(message)"
                    class="inline-flex items-center px-2.5 py-1.5 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    {{ $t('professor.replyViaWhatsApp') }}
                  </button>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '@/stores/auth';
import { collection, query, where, getDocs, doc, updateDoc, orderBy } from 'firebase/firestore';
import { db } from '@/firebase/config';
import Breadcrumb from '@/components/Breadcrumb.vue';
import { Timestamp } from 'firebase/firestore';
import { format, isValid } from 'date-fns';
import { ptBR, enUS, es as esLocale, fr as frLocale } from 'date-fns/locale';

const { t, locale } = useI18n();
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// State
const loading = ref(true);
const messages = ref([]);
const searchQuery = ref('');
const filterStatus = ref('all');

// Breadcrumb items
const breadcrumbItems = computed(() => {
  return [
    { name: t('professor.dashboard'), path: '/professor' },
    { name: t('professor.messages'), path: '/professor/messages' }
  ];
});

// Filtered messages based on search and status
const filteredMessages = computed(() => {
  let filtered = messages.value;

  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(message => 
      message.studentName.toLowerCase().includes(query) ||
      message.text.toLowerCase().includes(query)
    );
  }

  // Filter by status
  if (filterStatus.value !== 'all') {
    filtered = filtered.filter(message => 
      filterStatus.value === 'read' ? message.isRead : !message.isRead
    );
  }

  return filtered;
});

// Fetch messages
const fetchMessages = async () => {
  try {
    loading.value = true;
    const messagesQuery = query(
      collection(db, 'studentMessages'),
      where('professorId', '==', authStore.userId),
      orderBy('createdAt', 'desc')
    );

    const snapshot = await getDocs(messagesQuery);
    messages.value = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
  } catch (error) {
    console.error('Error fetching messages:', error);
  } finally {
    loading.value = false;
  }
};

// Mark message as read
const markAsRead = async (messageId) => {
  try {
    await updateDoc(doc(db, 'studentMessages', messageId), {
      isRead: true
    });
    
    // Update local state
    const index = messages.value.findIndex(m => m.id === messageId);
    if (index !== -1) {
      messages.value[index].isRead = true;
    }
  } catch (error) {
    console.error('Error marking message as read:', error);
  }
};

// Reply to student via WhatsApp
const replyToStudent = (message) => {
  if (!message.studentPhone) {
    alert(t('professor.studentPhoneNotAvailable'));
    return;
  }

  const whatsappUrl = `https://wa.me/${message.studentPhone}`;
  window.open(whatsappUrl, '_blank');
};

// Get date-fns locale based on i18n locale
const dateFnsLocale = computed(() => {
  const localeMap = {
    'pt': ptBR,
    'en': enUS,
    'es': esLocale,
    'fr': frLocale
  };
  return localeMap[locale.value] || ptBR;
});

// Format date and time
const formatDateTime = (timestamp) => {
  if (!timestamp) return '';
  
  // Handle Firestore Timestamp objects
  const date = timestamp instanceof Timestamp 
    ? timestamp.toDate() 
    : (timestamp.seconds ? new Date(timestamp.seconds * 1000) : new Date(timestamp));

  if (!isValid(date)) {
    return t('professor.invalidDate');
  }

  // Use locale-specific format
  const formatString = locale.value === 'en' 
    ? "MM/dd/yyyy 'at' HH:mm"
    : locale.value === 'es'
    ? "dd/MM/yyyy 'a las' HH:mm"
    : locale.value === 'fr'
    ? "dd/MM/yyyy 'à' HH:mm"
    : "dd/MM/yyyy 'às' HH:mm";

  return format(date, formatString, { locale: dateFnsLocale.value });
};

onMounted(async () => {
  if (!authStore.isProfessor) {
    router.push('/login');
    return;
  }

  await fetchMessages();
});
</script> 