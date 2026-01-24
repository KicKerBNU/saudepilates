<template>
  <div class="min-h-screen bg-gray-100" 
       @touchstart="handleTouchStart" 
       @touchmove="handleTouchMove" 
       @touchend="handleTouchEnd">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900">{{ $t('student.dashboard') }}</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <!-- Breadcrumb -->
        <div class="px-4 py-6 sm:px-0">
          <Breadcrumb :items="breadcrumbItems" />
        </div>

        <!-- Enhanced Student Welcome Card -->
        <div class="bg-gradient-to-r from-indigo-600 to-blue-500 shadow overflow-hidden sm:rounded-lg mb-6 text-white">
          <div class="px-4 py-6 sm:px-6 flex items-center">
            <div class="bg-white p-3 rounded-full mr-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <div>
              <h2 class="text-xl leading-6 font-bold">{{ $t('student.welcome') }}, {{ userProfile?.name || $t('student.student') }}</h2>
              <p class="mt-1 max-w-2xl text-sm text-indigo-100">{{ $t('student.welcomeMessage') }}</p>
            </div>
          </div>
        </div>

        <!-- Student Schedule Section - Made More Prominent -->
        <div class="bg-white shadow-lg border-l-4 border-indigo-600 overflow-hidden sm:rounded-lg mb-6">
          <div class="px-4 py-5 sm:px-6 flex justify-between items-center bg-gray-50">
            <div>
              <h3 class="text-lg font-medium leading-6 text-gray-900 flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                {{ $t('student.mySchedule') }}
              </h3>
              <p class="mt-1 max-w-2xl text-sm text-gray-500">{{ $t('student.yourScheduledClasses') }}</p>
            </div>
          </div>

          <!-- Loading State -->
          <div v-if="loadingSchedule" class="flex justify-center items-center h-36 py-4">
            <svg class="animate-spin h-8 w-8 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>

          <!-- No Schedule -->
          <div v-else-if="upcomingAppointments.length === 0" class="px-4 py-8 sm:px-6 text-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-400 mx-auto mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <p class="text-gray-500">{{ $t('student.noUpcomingAppointments') }}</p>
          </div>

          <!-- Schedule Display -->
          <div v-else class="px-4 py-5 sm:p-6">
            <div class="space-y-4">
              <div v-for="(groupedAppt, date) in groupedAppointments" :key="date" class="border-b border-gray-200 pb-4 last:border-b-0 last:pb-0">
                <h4 class="font-medium text-indigo-600 mb-2">{{ formatDateToDisplay(date) }}</h4>
                <div v-for="appt in groupedAppt" :key="appt.id" class="bg-gray-50 p-3 rounded-md mb-2 last:mb-0">
                  <div class="flex items-start">
                    <div class="flex-shrink-0 bg-indigo-100 rounded-md p-2">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                    </div>
                    <div class="ml-3">
                      <p class="text-sm font-medium text-gray-900">
                        {{ formatTime(appt.time) }} - {{ getEndTime(appt.time, appt.duration) }}
                      </p>
                      <p class="text-sm text-gray-500">
                        {{ $t('professor.professor') }}: {{ appt.professorName || $t('student.notInformed') }}
                      </p>
                      <p v-if="appt.notes" class="text-sm text-gray-500 mt-1">
                        {{ appt.notes }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Student Evolution Section -->
        <div class="bg-white shadow overflow-hidden sm:rounded-lg mb-6">
          <div class="px-4 py-5 sm:px-6">
            <h3 class="text-lg font-medium leading-6 text-gray-900 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 8v8m-4-5v5m-4-2v2m-2 4h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              {{ $t('student.myEvolution') }}
            </h3>
            <p class="mt-1 max-w-2xl text-sm text-gray-500">{{ $t('student.trackYourProgress') }}</p>
          </div>

          <!-- Loading State -->
          <div v-if="loadingEvolutions" class="flex justify-center items-center h-36 py-4">
            <svg class="animate-spin h-8 w-8 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>

          <!-- No Evolutions -->
          <div v-else-if="evolutions.length === 0" class="px-4 py-8 sm:px-6 text-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-400 mx-auto mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
            <p class="text-gray-500">{{ $t('student.noEvolutions') }}</p>
          </div>

          <!-- Evolution Display -->
          <div v-else class="px-4 py-5 sm:p-6">
            <div class="space-y-4">
              <div v-for="evolution in evolutions.slice(0, 3)" :key="evolution.id" class="border-b border-gray-200 pb-4 last:border-b-0 last:pb-0">
                <div class="flex justify-between items-start">
                  <h4 class="font-medium text-indigo-600">{{ formatDate(evolution.date) }}</h4>
                  <span class="text-sm text-gray-500">{{ $t('professor.professor') }}: {{ evolution.professorName || $t('student.notInformed') }}</span>
                </div>
                
                <!-- Added Evolution Type and Stars -->
                <div class="mt-2 mb-2 flex items-center justify-between">
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                    {{ evolution.category || $t('student.generalEvaluation') }}
                  </span>
                  <div class="flex items-center">
                    <span class="text-xs text-gray-500 mr-1">{{ $t('professor.rating') }}:</span>
                    <div class="flex">
                      <template v-for="i in 5" :key="i">
                        <svg 
                          v-if="i <= (parseInt(evolution.rating) || 0)"
                          xmlns="http://www.w3.org/2000/svg" 
                          class="h-4 w-4 text-yellow-400" 
                          viewBox="0 0 20 20" 
                          fill="currentColor"
                        >
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                        <svg 
                          v-else
                          xmlns="http://www.w3.org/2000/svg" 
                          class="h-4 w-4 text-gray-300" 
                          viewBox="0 0 20 20" 
                          fill="currentColor"
                        >
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                      </template>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="evolutions.length > 3" class="mt-4 text-center">
              <button @click="showAllEvolutions = !showAllEvolutions" class="text-sm text-indigo-600 hover:text-indigo-500">
                {{ showAllEvolutions ? $t('student.seeLess') : $t('student.seeMore') }}
              </button>
            </div>
            <div v-if="showAllEvolutions" class="mt-4 space-y-4">
              <div v-for="evolution in evolutions.slice(3)" :key="evolution.id" class="border-b border-gray-200 pb-4 last:border-b-0 last:pb-0">
                <div class="flex justify-between items-start">
                  <h4 class="font-medium text-indigo-600">{{ formatDate(evolution.date) }}</h4>
                  <span class="text-sm text-gray-500">{{ $t('professor.professor') }}: {{ evolution.professorName || $t('student.notInformed') }}</span>
                </div>
                
                <!-- Added Evolution Type and Stars for additional evolutions -->
                <div class="mt-2 mb-2 flex items-center justify-between">
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                    {{ evolution.category || $t('student.generalEvaluation') }}
                  </span>
                  <div class="flex items-center">
                    <span class="text-xs text-gray-500 mr-1">{{ $t('professor.rating') }}:</span>
                    <div class="flex">
                      <template v-for="i in 5" :key="i">
                        <svg 
                          v-if="i <= (parseInt(evolution.rating) || 0)"
                          xmlns="http://www.w3.org/2000/svg" 
                          class="h-4 w-4 text-yellow-400" 
                          viewBox="0 0 20 20" 
                          fill="currentColor"
                        >
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                        <svg 
                          v-else
                          xmlns="http://www.w3.org/2000/svg" 
                          class="h-4 w-4 text-gray-300" 
                          viewBox="0 0 20 20" 
                          fill="currentColor"
                        >
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                      </template>
                    </div>
                  </div>
                </div>
                
                <div class="mt-2">
                  <p class="text-sm text-gray-700 whitespace-pre-line">{{ evolution.notes }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Professor Information Card - Moved Above WhatsApp Message Section -->
        <div class="bg-white shadow overflow-hidden sm:rounded-lg mb-6">
          <div class="px-4 py-5 sm:px-6 flex justify-between items-center">
            <div>
              <h3 class="text-lg font-medium leading-6 text-gray-900 flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-600 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                {{ $t('student.professorInformation') }}
              </h3>
              <p class="mt-1 max-w-2xl text-sm text-gray-500">{{ $t('student.professorContactDetails') }}</p>
            </div>
          </div>
          
          <!-- Loading State -->
          <div v-if="loadingProfessorInfo" class="flex justify-center items-center h-24 py-4">
            <svg class="animate-spin h-6 w-6 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>
          
          <!-- Professor Not Found -->
          <div v-else-if="!professorInfo" class="px-4 py-5 sm:p-6 text-center text-gray-500">
            {{ $t('student.professorInformationNotAvailable') }}
          </div>
          
          <!-- Professor Info Display -->
          <div v-else class="border-t border-gray-200">
            <dl>
              <div class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt class="text-sm font-medium text-gray-500">{{ $t('common.name') }}</dt>
                <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                  {{ professorInfo.name || $t('student.notInformed') }}
                </dd>
              </div>
              <div class="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt class="text-sm font-medium text-gray-500">{{ $t('common.email') }}</dt>
                <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                  <a 
                    v-if="professorInfo.email" 
                    :href="'mailto:' + professorInfo.email" 
                    class="text-indigo-600 hover:text-indigo-900"
                  >
                    {{ professorInfo.email }}
                  </a>
                  <span v-else>{{ $t('student.notInformed') }}</span>
                </dd>
              </div>
              <div class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt class="text-sm font-medium text-gray-500">{{ $t('common.phone') }}</dt>
                <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                  <a 
                    v-if="professorInfo.phone" 
                    :href="'tel:' + professorInfo.phone" 
                    class="text-indigo-600 hover:text-indigo-900"
                  >
                    {{ formatPhone(professorInfo.phone) }}
                  </a>
                  <span v-else>{{ $t('student.notInformed') }}</span>
                </dd>
              </div>
              <div class="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt class="text-sm font-medium text-gray-500">WhatsApp</dt>
                <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                  <a 
                    v-if="professorInfo.whatsapp" 
                    :href="'https://wa.me/' + professorInfo.whatsapp.replace(/\D/g, '')" 
                    target="_blank"
                    class="inline-flex items-center text-green-600 hover:text-green-900"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654z"/>
                    </svg>
                    {{ formatPhone(professorInfo.whatsapp) }}
                  </a>
                  <span v-else>{{ $t('student.notInformed') }}</span>
                </dd>
              </div>
            </dl>
          </div>
        </div>

        <!-- WhatsApp Message Section -->
        <div class="bg-white shadow overflow-hidden sm:rounded-lg">
          <div class="px-4 py-5 sm:px-6">
            <h3 class="text-lg font-medium leading-6 text-gray-900 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-green-600 mr-2" fill="currentColor" viewBox="0 0 24 24">
                <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654z"/>
              </svg>
              {{ $t('student.contactProfessor') }}
            </h3>
            <p class="mt-1 max-w-2xl text-sm text-gray-500">{{ $t('student.sendMessageToProfessor') }}</p>
          </div>

          <div class="px-4 py-5 sm:p-6">
            <form @submit.prevent="sendMessage">
              <div class="mb-4">
                <label for="message" class="block text-sm font-medium text-gray-700 mb-1">{{ $t('student.message') }}</label>
                <textarea
                  id="message"
                  v-model="messageText"
                  rows="4"
                  class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md border-2 border-gray-200 p-3"
                  :placeholder="$t('student.messagePlaceholder')"
                  required
                ></textarea>
              </div>
              
              <!-- Default Message Chips -->
              <div class="mb-4 flex flex-wrap gap-2">
                <span class="text-xs text-gray-500 mr-1 self-center">{{ $t('student.quickMessages') }}</span>
                <button 
                  v-for="(message, index) in defaultMessages" 
                  :key="index"
                  @click.prevent="selectDefaultMessage(message)"
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-gray-100 text-gray-800 hover:bg-gray-200 border border-gray-300"
                >
                  {{ message.preview }}
                </button>
                
                <div class="w-full mt-2"></div>
                
                <span class="text-xs text-gray-500 mr-1 self-center">{{ $t('student.sendDirectly') }}</span>
                <button 
                  v-for="(message, index) in defaultMessages" 
                  :key="'direct-' + index"
                  @click.prevent="selectAndSendMessage(message)"
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-green-100 text-green-800 hover:bg-green-200 border border-green-300"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654z"/>
                  </svg>
                  {{ message.preview }}
                </button>
              </div>
              
              <div class="flex justify-end">
                <button
                  type="submit"
                  :disabled="sendingMessage"
                  class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                >
                  <svg v-if="sendingMessage" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  <svg xmlns="http://www.w3.org/2000/svg" class="-ml-1 mr-2 h-4 w-4 text-white" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654zm6.597-3.807c1.676.995 3.276 1.591 5.392 1.592 5.448 0 9.886-4.434 9.889-9.885.002-5.462-4.415-9.89-9.881-9.892-5.452 0-9.887 4.434-9.889 9.884-.001 2.225.651 3.891 1.746 5.634l-.999 3.648 3.742-.981zm11.387-5.464c-.074-.124-.272-.198-.57-.347-.297-.149-1.758-.868-2.031-.967-.272-.099-.47-.149-.669.149-.198.297-.768.967-.941 1.165-.173.198-.347.223-.644.074-.297-.149-1.255-.462-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.297-.347.446-.521.151-.172.2-.296.3-.495.099-.198.05-.372-.025-.521-.075-.148-.669-1.611-.916-2.206-.242-.579-.487-.501-.669-.51l-.57-.01c-.198 0-.52.074-.792.372s-1.04 1.016-1.04 2.479 1.065 2.876 1.213 3.074c.149.198 2.095 3.2 5.076 4.487.709.306 1.263.489 1.694.626.712.226 1.36.194 1.872.118.571-.085 1.758-.719 2.006-1.413.248-.695.248-1.29.173-1.414z"/>
                  </svg>
                  {{ $t('student.sendMessage') }} <span class="text-xs ml-1">({{ $t('student.willOpenApp') }})</span>
                </button>
              </div>
            </form>

            <!-- Message History -->
            <div class="mt-6">
              <h4 class="font-medium text-gray-900 mb-4">{{ $t('student.messageHistory') }}</h4>
              
              <!-- Loading State -->
              <div v-if="loadingMessages" class="flex justify-center items-center h-24">
                <svg class="animate-spin h-6 w-6 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              </div>

              <!-- No Messages -->
              <div v-else-if="messages.length === 0" class="text-center py-6 bg-gray-50 rounded-lg">
                <p class="text-gray-500">{{ $t('student.noMessagesSentYet') }}</p>
              </div>

              <!-- Message List -->
              <div v-else class="space-y-3">
                <div v-for="message in messages" :key="message.id" class="bg-gray-50 p-3 rounded-lg">
                  <div class="flex justify-between items-start">
                    <span class="text-sm font-medium text-gray-900">{{ formatDateTime(message.createdAt) }}</span>
                    <span class="text-xs px-2 py-1 rounded bg-green-100 text-green-800">
                      {{ message.status }}
                    </span>
                  </div>
                  <p class="mt-2 text-sm text-gray-700">{{ message.text }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';
import { useScheduleStore } from '../../stores/schedule';
import { useEvolutionStore } from '../../stores/evolution';
import { collection, addDoc, query, where, getDocs, orderBy, Timestamp, doc, getDoc } from 'firebase/firestore';
import { db } from '../../firebase/config';
import { formatISO, format, parseISO, add, startOfDay, isAfter, isToday, parse } from 'date-fns';
import { ptBR, enUS, es as esLocale, fr as frLocale } from 'date-fns/locale';
import { firebaseTimestampToLocalDate } from '@/utils/dateUtils';
import Breadcrumb from '@/components/Breadcrumb.vue';

export default {
  components: {
    Breadcrumb
  },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const { t, locale } = useI18n();
    const authStore = useAuthStore();
    const scheduleStore = useScheduleStore();
    const evolutionStore = useEvolutionStore();

    const userProfile = computed(() => authStore.userProfile);
    const upcomingAppointments = ref([]);
    const loadingSchedule = ref(false);
    const loadingEvolutions = ref(false);
    const loadingMessages = ref(false);
    const loadingProfessorInfo = ref(false);
    const sendingMessage = ref(false);
    const evolutions = ref([]);
    const showAllEvolutions = ref(false);
    const messageText = ref('');
    const messages = ref([]);
    const professorInfo = ref(null);
    const defaultMessages = computed(() => [
      { 
        preview: t('student.cannotAttend'), 
        text: t('student.defaultMessageCannotAttend')
      },
      { 
        preview: t('student.willBeLate'), 
        text: t('student.defaultMessageWillBeLate')
      },
      { 
        preview: t('student.questionAboutExercise'), 
        text: t('student.defaultMessageQuestionAboutExercise')
      },
      { 
        preview: t('student.scheduleEvaluation'), 
        text: t('student.defaultMessageScheduleEvaluation')
      }
    ]);

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


    // Group appointments by date for easier display
    const groupedAppointments = computed(() => {
      const grouped = {};
      
      for (const appointment of upcomingAppointments.value) {
        // Use startOfDay to ensure we're grouping by the correct local date
        const appointmentDate = appointment.date instanceof Date ? appointment.date : new Date(appointment.date);
        const normalizedDate = startOfDay(appointmentDate);
        const dateKey = format(normalizedDate, 'yyyy-MM-dd');
        if (!grouped[dateKey]) {
          grouped[dateKey] = [];
        }
        grouped[dateKey].push(appointment);
      }
      
      // Sort appointments by time for each day
      for (const date in grouped) {
        grouped[date].sort((a, b) => {
          return new Date(a.time) - new Date(b.time);
        });
      }
      
      return grouped;
    });

    // Format functions
    const formatDateToDisplay = (dateStr) => {
      // Parse the YYYY-MM-DD string properly to avoid timezone issues
      let date;
      if (typeof dateStr === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(dateStr)) {
        // Parse YYYY-MM-DD format without timezone conversion
        date = parse(dateStr, 'yyyy-MM-dd', new Date());
      } else {
        date = new Date(dateStr);
      }
      
      const formatString = locale.value === 'en' 
        ? "EEEE, MMMM dd"
        : locale.value === 'es'
        ? "EEEE, dd 'de' MMMM"
        : locale.value === 'fr'
        ? "EEEE, dd MMMM"
        : "EEEE, dd 'de' MMMM";
      return format(date, formatString, { locale: dateFnsLocale.value });
    };

    const formatTime = (timeStr) => {
      if (!timeStr) return '';
      
      try {
        // If timeStr is an ISO string, parse it directly
        if (typeof timeStr === 'string') {
          // Check if it's a time-only string (HH:mm)
          if (/^\d{1,2}:\d{2}$/.test(timeStr)) {
            // For time-only strings like "14:30", create a date at the epoch and set the hours and minutes
            const [hours, minutes] = timeStr.split(':').map(Number);
            const date = new Date(0);
            date.setHours(hours, minutes, 0, 0);
            return format(date, 'HH:mm');
          }
          
          // Otherwise, parse as a full date string
          const date = parseISO(timeStr);
          return format(date, 'HH:mm');
        }
        
        // If it's already a Date object
        if (timeStr instanceof Date) {
          return format(timeStr, 'HH:mm');
        }
        
        // If it's a timestamp object with toDate method
        if (timeStr.toDate && typeof timeStr.toDate === 'function') {
          return format(timeStr.toDate(), 'HH:mm');
        }
        
        // If it's a timestamp object without toDate
        if (timeStr.seconds && timeStr.nanoseconds) {
          return format(new Date(timeStr.seconds * 1000), 'HH:mm');
        }
        
        // Default fallback
        return format(new Date(timeStr), 'HH:mm');
      } catch (error) {
        console.warn('Error formatting time:', error, timeStr);
        return t('student.timeNotAvailable');
      }
    };

    const getEndTime = (startTimeStr, durationMinutes) => {
      if (!startTimeStr) return '';
      
      try {
        let startTime;
        
        // Handle different types of time inputs
        if (typeof startTimeStr === 'string') {
          // Check if it's a time-only string (HH:mm)
          if (/^\d{1,2}:\d{2}$/.test(startTimeStr)) {
            // For time-only strings like "14:30", create a date at the epoch and set the hours and minutes
            const [hours, minutes] = startTimeStr.split(':').map(Number);
            startTime = new Date(0);
            startTime.setHours(hours, minutes, 0, 0);
          } else {
            // Otherwise, parse as a full date string
            startTime = parseISO(startTimeStr);
          }
        } else if (startTimeStr instanceof Date) {
          // If it's already a Date object
          startTime = startTimeStr;
        } else if (startTimeStr.toDate && typeof startTimeStr.toDate === 'function') {
          // If it's a timestamp object with toDate method
          startTime = startTimeStr.toDate();
        } else if (startTimeStr.seconds && startTimeStr.nanoseconds) {
          // If it's a timestamp object without toDate
          startTime = new Date(startTimeStr.seconds * 1000);
        } else {
          // Default fallback
          startTime = new Date(startTimeStr);
        }
        
        const endTime = add(startTime, { minutes: durationMinutes || 60 });
        return format(endTime, 'HH:mm');
      } catch (error) {
        console.warn('Error calculating end time:', error, startTimeStr);
        return t('student.timeNotAvailable');
      }
    };

    const formatDate = (dateStr) => {
      if (!dateStr) return '';
      const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr.toDate();
      const formatString = locale.value === 'en' 
        ? "MM/dd/yyyy"
        : "dd/MM/yyyy";
      return format(date, formatString, { locale: dateFnsLocale.value });
    };

    const formatDateTime = (dateTimeStr) => {
      if (!dateTimeStr) return '';
      
      let date;
      
      // Handle different types of date objects
      if (typeof dateTimeStr === 'string') {
        // If it's a string, convert to Date
        date = new Date(dateTimeStr);
      } else if (dateTimeStr instanceof Date) {
        // If it's already a Date object, use directly
        date = dateTimeStr;
      } else if (dateTimeStr.toDate && typeof dateTimeStr.toDate === 'function') {
        // If it's a Firestore Timestamp, use toDate()
        date = dateTimeStr.toDate();
      } else if (dateTimeStr.seconds && dateTimeStr.nanoseconds) {
        // If it's a Firestore Timestamp-like object without toDate()
        date = new Date(dateTimeStr.seconds * 1000);
      } else {
        // Fallback
        date = new Date();
      }
      
      const formatString = locale.value === 'en' 
        ? "MM/dd/yyyy 'at' HH:mm"
        : locale.value === 'es'
        ? "dd/MM/yyyy 'a las' HH:mm"
        : locale.value === 'fr'
        ? "dd/MM/yyyy 'à' HH:mm"
        : "dd/MM/yyyy 'às' HH:mm";
      return format(date, formatString, { locale: dateFnsLocale.value });
    };


    // Fetch student's schedule - show all upcoming classes (today and future)
    const fetchStudentSchedule = async () => {
      if (!authStore.userId) return;
      
      loadingSchedule.value = true;
      try {
        // Fetch from scheduledClasses collection where studentId matches
        const classesQuery = query(
          collection(db, 'scheduledClasses'),
          where('studentId', '==', authStore.userId)
        );
        
        const classesSnapshot = await getDocs(classesQuery);
        
        // Get today's date (normalized to start of day for comparison)
        const today = startOfDay(new Date());
        
        // Transform into appointment objects
        const appointments = [];
        
        for (const doc of classesSnapshot.docs) {
          const data = doc.data();
          
          // Use date utility to handle timezone issues properly
          const appointmentDate = data.date instanceof Timestamp 
            ? firebaseTimestampToLocalDate(data.date) 
            : data.date instanceof Date 
            ? data.date 
            : new Date(data.date);
          
          // Normalize date for comparison (compare only date part, not time)
          const appointmentDateOnly = startOfDay(appointmentDate);
          
          // Only include appointments that are today or in the future
          if (isToday(appointmentDateOnly) || isAfter(appointmentDateOnly, today)) {
            appointments.push({
              id: doc.id,
              studentId: data.studentId,
              professorId: data.professorId,
              professorName: data.professorName || await getProfessorName(data.professorId),
              date: appointmentDate,
              time: data.startTime || data.time || appointmentDate.toISOString(),
              duration: data.duration || 60,
              type: data.type || 'scheduled',
              status: data.status || 'scheduled',
              notes: data.notes || ''
            });
          }
        }
        
        // Sort by date and time
        appointments.sort((a, b) => {
          const dateA = new Date(a.date);
          const dateB = new Date(b.date);
          if (dateA.getTime() === dateB.getTime()) {
            return new Date(a.time) - new Date(b.time);
          }
          return dateA - dateB;
        });
        
        // Limit to only the next 2 appointments
        upcomingAppointments.value = appointments.slice(0, 2);
      } catch (error) {
        console.error('Error fetching student schedule:', error);
      } finally {
        loadingSchedule.value = false;
      }
    };

    // Helper function to get professor name
    const getProfessorName = async (professorId) => {
      if (!professorId) return t('student.notInformed');
      
      try {
        // Get the professor document directly by its ID
        const professorRef = doc(db, 'users', professorId);
        const professorDoc = await getDoc(professorRef);
        
        if (professorDoc.exists()) {
          const userData = professorDoc.data();
          return userData.name || userData.displayName || t('professor.professor');
        }
        
        // Fallback to looking for a field match if direct lookup fails
        const usersQuery = query(
          collection(db, 'users'), 
          where('uid', '==', professorId)
        );
        const userSnapshot = await getDocs(usersQuery);
        
        if (!userSnapshot.empty) {
          const userData = userSnapshot.docs[0].data();
          return userData.name || userData.displayName || t('professor.professor');
        }
        
        return t('professor.professor');
      } catch (error) {
        console.error('Error fetching professor name:', error);
        return t('professor.professor');
      }
    };

    // Fetch student's evolutions
    const fetchEvolutions = async () => {
      if (!authStore.userId) return;
      
      loadingEvolutions.value = true;
      try {
        const evolutionsData = await evolutionStore.fetchStudentEvolutions(authStore.userId);
        
        // Enrich evolutions with professor names and ensure stars is a number
        const enrichedEvolutions = await Promise.all(evolutionsData.map(async (evolution) => {
          return {
            ...evolution,
            professorName: evolution.professorName || await getProfessorName(evolution.professorId),
          };
        }));
        
        evolutions.value = enrichedEvolutions;
      } catch (error) {
        console.error('Error fetching evolutions:', error);
      } finally {
        loadingEvolutions.value = false;
      }
    };

    // Fetch message history
    const fetchMessages = async () => {
      if (!authStore.userId) return;
      
      loadingMessages.value = true;
      try {
        const messagesQuery = query(
          collection(db, 'studentMessages'),
          where('studentId', '==', authStore.userId),
          orderBy('createdAt', 'desc')
        );
        
        const messagesSnapshot = await getDocs(messagesQuery);
        
        messages.value = messagesSnapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data()
        }));
      } catch (error) {
        console.error('Error fetching messages:', error);
      } finally {
        loadingMessages.value = false;
      }
    };

    // Send WhatsApp message
    const sendMessage = async () => {
      if (!messageText.value.trim() || !authStore.userId) return;
      
      sendingMessage.value = true;
      try {
        // Store the message in Firestore
        const messageData = {
          studentId: authStore.userId,
          studentName: userProfile.value.name || t('student.student'),
          professorId: userProfile.value.professorId,
          text: messageText.value,
          createdAt: new Date(),
          status: t('student.sent'),
          isRead: false
        };
        
        // Add to the studentMessages collection
        const docRef = await addDoc(collection(db, 'studentMessages'), messageData);
        
        // Update the local state
        messages.value.unshift({
          id: docRef.id,
          ...messageData
        });
        
        // Get the professor's phone number from the professorInfo object
        const professorPhone = professorInfo.value?.whatsapp || professorInfo.value?.phone || '';
        
        if (professorPhone) {
          // Format the WhatsApp Click-to-Chat URL with the message
          const encodedMessage = encodeURIComponent(messageData.text);
          const whatsappUrl = `https://wa.me/${professorPhone.replace(/\D/g, '')}?text=${encodedMessage}`;
          
          // Open WhatsApp in a new window
          window.open(whatsappUrl, '_blank');
          
          // Clear the message input after successful sending
          messageText.value = '';
        } else {
          window.showWarningToast?.(t('student.professorPhoneNotFound'));
        }
      } catch (error) {
        console.error('Error sending message:', error);
        window.showErrorToast?.(t('student.errorSendingMessage'));
      } finally {
        sendingMessage.value = false;
      }
    };

    // Function to select a default message and immediately send it if requested
    const selectDefaultMessage = (message) => {
      messageText.value = message.text;
    };

    // Function that selects a default message and immediately sends it via WhatsApp
    const selectAndSendMessage = async (message) => {
      if (!userProfile.value?.professorId) {
        window.showErrorToast?.(t('student.professorNotFound'));
        return;
      }
      
      sendingMessage.value = true;
      try {
        // Get the professor's phone from the professorInfo object
        const professorPhone = professorInfo.value?.whatsapp || professorInfo.value?.phone || '';
        
        if (!professorPhone) {
          window.showWarningToast?.(t('student.professorPhoneNotFound'));
          sendingMessage.value = false;
          return;
        }
        
        // Store the message in Firestore
        const messageData = {
          studentId: authStore.userId,
          studentName: userProfile.value.name || t('student.student'),
          professorId: userProfile.value.professorId,
          text: message.text,
          createdAt: new Date(),
          status: t('student.sent'),
          isRead: false
        };
        
        // Add to the studentMessages collection
        const docRef = await addDoc(collection(db, 'studentMessages'), messageData);
        
        // Update the local state
        messages.value.unshift({
          id: docRef.id,
          ...messageData
        });
        
        // Format the WhatsApp Click-to-Chat URL with the message
        const encodedMessage = encodeURIComponent(message.text);
        const whatsappUrl = `https://wa.me/${professorPhone.replace(/\D/g, '')}?text=${encodedMessage}`;
        
        // Open WhatsApp in a new window
        window.open(whatsappUrl, '_blank');
      } catch (error) {
        console.error('Error sending direct message:', error);
        window.showErrorToast?.(t('student.errorSendingMessage'));
      } finally {
        sendingMessage.value = false;
      }
    };

    // Function to format phone number as (XX) XXXXX-XXXX
    const formatPhone = (phone) => {
      if (!phone) return '';
      
      // Remove all non-digits
      const digits = phone.replace(/\D/g, '');
      
      // Format depending on length
      if (digits.length === 11) {
        // Mobile: (XX) XXXXX-XXXX
        return `(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}`;
      } else if (digits.length === 10) {
        // Landline: (XX) XXXX-XXXX
        return `(${digits.substring(0, 2)}) ${digits.substring(2, 6)}-${digits.substring(6)}`;
      } else {
        // Return as is if not standard format
        return phone;
      }
    };

    // Function to fetch professor information
    const fetchProfessorInfo = async () => {
      if (!userProfile.value?.professorId) return;
      
      loadingProfessorInfo.value = true;
      try {
        const professorId = userProfile.value.professorId;
        const userRef = doc(db, 'users', professorId);
        const userDoc = await getDoc(userRef);
        
        if (userDoc.exists()) {
          const userData = userDoc.data();
          professorInfo.value = {
            id: professorId,
            name: userData.displayName || userData.name || t('professor.professor'),
            email: userData.email || '',
            phone: userData.phone || '',
            whatsapp: userData.whatsapp || userData.phone || ''
          };
        }
      } catch (error) {
        console.error('Error fetching professor information:', error);
      } finally {
        loadingProfessorInfo.value = false;
      }
    };


    const breadcrumbItems = computed(() => {
      const path = route.path;
      const segments = path.split('/').filter(Boolean);
      
      return segments.map((segment, index) => {
        const path = '/' + segments.slice(0, index + 1).join('/');
        const name = segment.charAt(0).toUpperCase() + segment.slice(1);
        return { name, path };
      });
    });

    // Touch event variables
    const touchStartX = ref(0);
    const touchEndX = ref(0);
    const minSwipeDistance = 50; // Minimum distance for a swipe to be registered

    // Touch event handlers
    const handleTouchStart = (e) => {
      touchStartX.value = e.touches[0].clientX;
    };

    const handleTouchMove = (e) => {
      touchEndX.value = e.touches[0].clientX;
    };

    const handleTouchEnd = () => {
      const swipeDistance = touchEndX.value - touchStartX.value;
      
      // Check if it's a left swipe (negative distance) and meets minimum distance
      if (swipeDistance < -minSwipeDistance) {
        router.go(-1); // Go back one step in history
      }
    };

    onMounted(async () => {
      if (!authStore.isStudent) {
        router.push('/login');
        return;
      }
      
      // Fetch initial data
      await fetchProfessorInfo();
      await fetchStudentSchedule();
      await fetchEvolutions();
      await fetchMessages();
    });

    return {
      userProfile,
      upcomingAppointments,
      loadingSchedule,
      loadingEvolutions,
      loadingMessages,
      loadingProfessorInfo,
      sendingMessage,
      evolutions,
      showAllEvolutions,
      messageText,
      messages,
      professorInfo,
      defaultMessages,
      groupedAppointments,
      formatDateToDisplay,
      formatTime,
      getEndTime,
      formatDate,
      formatDateTime,
      sendMessage,
      selectDefaultMessage,
      selectAndSendMessage,
      formatPhone,
      breadcrumbItems
    };
  }
};
</script>