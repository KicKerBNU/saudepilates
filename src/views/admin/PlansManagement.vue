<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-4 px-4 sm:py-6 sm:px-6 lg:px-8">
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-900">{{ $t('admin.plansManagement') }}</h1>
      </div>
    </header>
    
    <main class="max-w-7xl mx-auto py-4 px-4 sm:py-6 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <div class="mb-4 sm:mb-6">
        <Breadcrumb :items="breadcrumbItems" />
      </div>
      
      <!-- Company Info -->
      <div class="bg-white shadow overflow-hidden sm:rounded-lg mb-4 sm:mb-6">
        <div class="px-4 py-4 sm:px-6 sm:py-5">
          <h3 class="text-base sm:text-lg font-medium text-gray-900">
            {{ $t('admin.companyDetails') }}
          </h3>
          <p class="mt-1 max-w-2xl text-sm text-gray-500">
            {{ $t('admin.plansAssociated') }}
          </p>
        </div>
        <div class="border-t border-gray-200">
          <dl>
            <div class="bg-gray-50 px-4 py-4 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt class="text-sm font-medium text-gray-500">
                {{ $t('admin.totalPlans') }}
              </dt>
              <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                {{ plansList.length }}
              </dd>
            </div>
          </dl>
        </div>
      </div>

      <!-- Plans List -->
      <div class="bg-white shadow overflow-hidden sm:rounded-lg">
        <div class="px-4 py-4 sm:px-6 sm:py-5 border-b border-gray-200 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <h3 class="text-base sm:text-lg font-medium text-gray-900">{{ $t('admin.planList') }}</h3>
          <button 
            @click="openAddPlanModal"
            class="w-full sm:w-auto inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <svg class="h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            {{ $t('admin.addPlan') }}
          </button>
        </div>
        <ul role="list" class="divide-y divide-gray-200">
          <li v-for="plan in plansList" :key="plan.id" class="px-4 py-4 sm:px-6 hover:bg-gray-50">
            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
              <div class="min-w-0 flex-1">
                <div class="flex items-start">
                  <div class="flex-shrink-0 h-10 w-10 sm:h-12 sm:w-12 bg-indigo-100 rounded-lg flex items-center justify-center">
                    <svg class="h-6 w-6 sm:h-8 sm:w-8 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                    </svg>
                  </div>
                  <div class="ml-3 sm:ml-4">
                    <div class="flex flex-wrap items-center gap-2">
                      <h4 class="text-base sm:text-lg font-medium text-gray-900">{{ plan.title }}</h4>
                      <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium" 
                        :class="{
                          'bg-green-100 text-green-800': plan.sessionsPerWeek === 3,
                          'bg-yellow-100 text-yellow-800': plan.sessionsPerWeek === 2,
                          'bg-blue-100 text-blue-800': plan.sessionsPerWeek === 1
                        }">
                        {{ plan.sessionsPerWeek }} {{ plan.sessionsPerWeek === 1 ? $t('admin.session') : $t('admin.sessions') }}/{{ $t('admin.week') }}
                      </span>
                    </div>
                    <div class="mt-1 text-sm text-gray-500">{{ plan.description || $t('admin.noDescriptionAvailable') }}</div>
                    <div class="mt-2 text-sm font-medium text-gray-900">{{ $t('common.currency') }} {{ plan.price.toFixed(2) }}/{{ $t('admin.month') }}</div>
                    <div class="mt-2 flex flex-wrap gap-2">
                      <span v-if="plan.discountQuarterly > 0" class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-green-100 text-green-800">
                        {{ $t('admin.quarterly') }}: {{ plan.discountQuarterly }}% {{ $t('admin.off') }}
                      </span>
                      <span v-if="plan.discountSemiannual > 0" class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-800">
                        {{ $t('admin.semiannual') }}: {{ plan.discountSemiannual }}% {{ $t('admin.off') }}
                      </span>
                      <span v-if="plan.discountAnnual > 0" class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-purple-100 text-purple-800">
                        {{ $t('admin.annual') }}: {{ plan.discountAnnual }}% {{ $t('admin.off') }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex flex-col sm:flex-row gap-2 sm:ml-6">
                <button 
                  @click="openEditPlanModal(plan)"
                  class="w-full sm:w-auto inline-flex items-center justify-center px-3 py-2 border border-transparent text-sm font-medium rounded-md text-indigo-700 bg-indigo-100 hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                  <svg class="-ml-1 mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                  {{ $t('common.edit') }}
                </button>
                <button 
                  @click="confirmDeletePlan(plan)"
                  class="w-full sm:w-auto inline-flex items-center justify-center px-3 py-2 border border-transparent text-sm font-medium rounded-md text-red-700 bg-red-100 hover:bg-red-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                >
                  <svg class="-ml-1 mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                  {{ $t('common.remove') }}
                </button>
              </div>
            </div>
          </li>
          <li v-if="plansList.length === 0" class="px-4 py-6 sm:px-6 text-center text-gray-500">
            {{ $t('admin.noPlansYet') }}
          </li>
        </ul>
      </div>
    </main>
    
    <!-- Add Plan Modal -->
    <div v-if="showAddPlanModal" class="fixed z-10 inset-0 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" aria-hidden="true" @click="closeAddPlanModal"></div>

        <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <!-- Close button -->
          <div class="absolute top-0 right-0 pt-4 pr-4">
            <button 
              type="button" 
              @click="closeAddPlanModal"
              class="rounded-md bg-white text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            >
              <span class="sr-only">{{ $t('common.close') }}</span>
              <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div class="sm:flex sm:items-start">
            <div class="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-lg bg-indigo-100 sm:mx-0 sm:h-10 sm:w-10">
              <svg class="h-6 w-6 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m6 0H6m6 6V6m0 0H6m6 0h6M6 12h6m6 0h-6v6h6-6" />
              </svg>
            </div>
            <div class="text-center sm:mt-0 sm:ml-4 sm:text-left w-full">
              <h3 class="text-lg leading-6 font-medium text-gray-900" id="modal-title">
                {{ $t('admin.addNewPlan') }}
              </h3>
              <div>
                <p class="text-sm text-gray-500">
                  {{ $t('admin.fillPlanDetails') }}
                </p>
              </div>
              <div class="mt-4">
                <form @submit.prevent="addPlan" class="space-y-4">
                  <div>
                    <label for="title" class="block text-sm font-medium text-gray-900">
                      {{ $t('admin.planTitle') }} *
                      <span v-if="formErrors.title" class="text-red-500 text-xs ml-1">({{ formErrors.title }})</span>
                    </label>
                    <div class="mt-1">
                      <input 
                        type="text" 
                        id="title" 
                        v-model="newPlan.title"
                        :class="{
                          'border-red-300 focus:ring-red-500 focus:border-red-500': formErrors.title,
                          'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500': !formErrors.title
                        }"
                        class="block w-full rounded-md shadow-sm sm:text-sm px-4 py-3 border"
                        :placeholder="$t('admin.planTitlePlaceholder')" 
                        required 
                        @input="validateField('title')"
                      />
                    </div>
                  </div>

                  <div>
                    <label for="sessionsPerWeek" class="block text-sm font-medium text-gray-900">
                      {{ $t('admin.sessionsPerWeek') }} *
                    </label>
                    <div class="mt-1">
                      <div class="grid grid-cols-3 gap-3">
                        <button 
                          type="button"
                          v-for="count in [1, 2, 3]" 
                          :key="count"
                          @click="newPlan.sessionsPerWeek = count"
                          :class="[
                            newPlan.sessionsPerWeek === count
                              ? 'ring-2 ring-offset-2 ring-indigo-500 bg-indigo-600 text-white'
                              : 'bg-white text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50',
                            'rounded-md py-3 px-3 text-sm font-semibold shadow-sm'
                          ]"
                        >
                          {{ count }} {{ count === 1 ? $t('admin.session') : $t('admin.sessions') }}
                        </button>
                      </div>
                      <p class="mt-2 text-sm text-gray-500">{{ $t('admin.chooseWeeklySessions') }}</p>
                    </div>
                  </div>

                  <div>
                    <label for="price" class="block text-sm font-medium text-gray-900">
                      {{ $t('admin.planPrice') }} *
                      <span v-if="formErrors.price" class="text-red-500 text-xs ml-1">({{ formErrors.price }})</span>
                    </label>
                    <div class="mt-1 relative rounded-md shadow-sm">
                      <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                        <span class="text-gray-500 sm:text-sm">{{ $t('common.currency') }}</span>
                      </div>
                      <input 
                        type="number" 
                        id="price" 
                        v-model="newPlan.price"
                        :class="{
                          'border-red-300 focus:ring-red-500 focus:border-red-500': formErrors.price,
                          'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500': !formErrors.price
                        }"
                        class="block w-full rounded-md pl-10 pr-3 py-3 border sm:text-sm"
                        placeholder="0.00" 
                        step="0.01" 
                        min="0" 
                        required 
                        @input="validateField('price')"
                      />
                    </div>
                  </div>

                  <div>
                    <label for="description" class="block text-sm font-medium text-gray-900">
                      {{ $t('admin.description') }}
                      <span class="text-gray-500">({{ $t('common.optional') }})</span>
                    </label>
                    <div class="mt-1">
                      <textarea 
                        id="description" 
                        v-model="newPlan.description" 
                        rows="4"
                        class="block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                        :placeholder="$t('admin.describePlanBenefits')"
                      ></textarea>
                      <p class="mt-2 text-sm text-gray-500">{{ $t('admin.briefPlanDescription') }}</p>
                    </div>
                  </div>

                  <div v-if="error" class="mt-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded relative">
                    <span class="block sm:inline">{{ error }}</span>
                  </div>

                  <div class="mt-6 flex flex-col sm:flex-row sm:justify-end gap-3">
                    <button 
                      type="button" 
                      @click="closeAddPlanModal"
                      :disabled="isAdding"
                      class="w-full sm:w-auto inline-flex items-center justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {{ $t('common.cancel') }}
                    </button>
                    <button 
                      type="submit"
                      :disabled="isAdding || hasFormErrors"
                      class="w-full sm:w-auto inline-flex items-center justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      <svg 
                        v-if="isAdding" 
                        class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" 
                        xmlns="http://www.w3.org/2000/svg" 
                        fill="none" 
                        viewBox="0 0 24 24"
                      >
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      {{ isAdding ? $t('common.loading') : $t('admin.addPlan') }}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Plan Modal -->
    <div v-if="showEditPlanModal" class="fixed z-10 inset-0 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" aria-hidden="true" @click="closeEditPlanModal"></div>

        <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <div class="sm:flex sm:items-start">
            <div class="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-indigo-100 sm:mx-0 sm:h-10 sm:w-10">
              <svg class="h-6 w-6 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
            <div class="mt-2 text-center sm:mt-0 sm:ml-4 sm:text-left w-full">
              <h3 class="text-lg leading-6 font-medium text-gray-900" id="modal-title">
                {{ $t('admin.editPlan') }}
              </h3>
              <div class="mt-4">
                <form @submit.prevent="updatePlan" class="space-y-4">
                  <div>
                    <label for="editTitle" class="block text-sm font-medium text-gray-900">{{ $t('admin.planTitle') }}</label>
                    <div class="mt-1">
                      <input type="text" id="editTitle" v-model="editingPlan.title"
                        class="block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                        :placeholder="$t('admin.planTitlePlaceholder')" required />
                    </div>
                  </div>

                  <div>
                    <label for="editSessionsPerWeek" class="block text-sm font-medium text-gray-900">{{ $t('admin.sessionsPerWeek') }}</label>
                    <div class="mt-1">
                      <select id="editSessionsPerWeek" v-model="editingPlan.sessionsPerWeek"
                        class="block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3">
                        <option value="1">1 {{ $t('admin.session') }} {{ $t('admin.perWeek') }}</option>
                        <option value="2">2 {{ $t('admin.sessions') }} {{ $t('admin.perWeek') }}</option>
                        <option value="3">3 {{ $t('admin.sessions') }} {{ $t('admin.perWeek') }}</option>
                      </select>
                    </div>
                    <p class="mt-2 text-sm text-gray-500">{{ $t('admin.chooseBetweenSessions') }}</p>
                  </div>

                  <div>
                    <label for="editPrice" class="block text-sm font-medium text-gray-900">{{ $t('admin.monthlyPrice') }}</label>
                    <div class="mt-1 relative rounded-md shadow-sm">
                      <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                        <span class="text-gray-500 sm:text-sm">{{ $t('common.currency') }}</span>
                      </div>
                      <input type="number" id="editPrice" v-model="editingPlan.price"
                        class="block w-full rounded-md border border-gray-300 pl-10 pr-3 py-3 focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        placeholder="0.00" step="0.01" min="0" required />
                    </div>
                  </div>

                  <div>
                    <label for="editDescription" class="block text-sm font-medium text-gray-900">
                      Descrição
                      <span class="text-gray-500">({{ $t('common.optional') }})</span>
                    </label>
                    <div class="mt-1">
                      <textarea id="editDescription" v-model="editingPlan.description" rows="4"
                        class="block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-4 py-3"
                        :placeholder="$t('admin.describePlanBenefits')"></textarea>
                    </div>
                  </div>

                  <div>
                    <label for="editCommission" class="block text-sm font-medium text-gray-900">
                      {{ $t('admin.professorCommission') }}
                    </label>
                    <div class="mt-1">
                      <input 
                        type="number" 
                        id="editCommission" 
                        v-model.number="editingPlan.commission"
                        min="0"
                        max="100"
                        step="1"
                        class="block w-full rounded-md shadow-sm sm:text-sm px-4 py-3 border-gray-300 focus:ring-indigo-500 focus:border-indigo-500"
                        placeholder="40"
                      />
                    </div>
                    <p class="mt-1 text-xs text-gray-500">{{ $t('admin.commissionDescription') }}</p>
                  </div>

                  <div class="mt-4 mb-2">
                    <h4 class="text-sm font-medium text-gray-900 mb-2">{{ $t('admin.advancePaymentDiscounts') }}</h4>
                    <p class="text-xs text-gray-500 mb-2">{{ $t('admin.configureDiscountsDescription') }}</p>
                    
                    <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
                      <!-- Quarterly Discount -->
                      <div>
                        <label for="editDiscountQuarterly" class="block text-sm font-medium text-gray-900">
                          {{ $t('admin.quarterly') }} (%)
                        </label>
                        <div class="mt-1">
                          <input 
                            type="number" 
                            id="editDiscountQuarterly" 
                            v-model.number="editingPlan.discountQuarterly"
                            min="0"
                            max="100"
                            step="1"
                            class="block w-full rounded-md shadow-sm sm:text-sm px-4 py-3 border border-gray-300 focus:ring-indigo-500 focus:border-indigo-500"
                            placeholder="5"
                          />
                        </div>
                      </div>
                      
                      <!-- Semi-annual Discount -->
                      <div>
                        <label for="editDiscountSemiannual" class="block text-sm font-medium text-gray-900">
                          {{ $t('admin.semiannual') }} (%)
                        </label>
                        <div class="mt-1">
                          <input 
                            type="number" 
                            id="editDiscountSemiannual" 
                            v-model.number="editingPlan.discountSemiannual"
                            min="0"
                            max="100"
                            step="1"
                            class="block w-full rounded-md shadow-sm sm:text-sm px-4 py-3 border border-gray-300 focus:ring-indigo-500 focus:border-indigo-500"
                            placeholder="10"
                          />
                        </div>
                      </div>
                      
                      <!-- Annual Discount -->
                      <div>
                        <label for="editDiscountAnnual" class="block text-sm font-medium text-gray-900">
                          {{ $t('admin.annual') }} (%)
                        </label>
                        <div class="mt-1">
                          <input 
                            type="number" 
                            id="editDiscountAnnual" 
                            v-model.number="editingPlan.discountAnnual"
                            min="0"
                            max="100"
                            step="1"
                            class="block w-full rounded-md shadow-sm sm:text-sm px-4 py-3 border border-gray-300 focus:ring-indigo-500 focus:border-indigo-500"
                            placeholder="20"
                          />
                        </div>
                      </div>
                    </div>
                  </div>

                  <div v-if="error" class="mt-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded relative">
                    <span class="block sm:inline">{{ error }}</span>
                  </div>

                  <div class="mt-6 flex flex-col sm:flex-row sm:justify-end gap-3">
                    <button 
                      type="button" 
                      @click="closeEditPlanModal"
                      class="w-full sm:w-auto inline-flex items-center justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
                    >
                      {{ $t('common.cancel') }}
                    </button>
                    <button 
                      type="submit"
                      class="w-full sm:w-auto inline-flex items-center justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
                    >
                      {{ $t('admin.saveChanges') }}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteConfirmModal" class="fixed z-10 inset-0 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" aria-hidden="true" @click="cancelDelete"></div>

        <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6">
          <div class="sm:flex sm:items-start">
            <div class="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10">
              <svg class="h-6 w-6 text-red-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
            <div class="mt-2 text-center sm:mt-0 sm:ml-4 sm:text-left">
              <h3 class="text-lg leading-6 font-medium text-gray-900" id="modal-title">
                {{ $t('admin.deletePlan') }}
              </h3>
              <div class="mt-1">
                <p class="text-sm text-gray-500">
                  {{ $t('admin.confirmDeletePlan', { plan: planToDelete?.title }) }}
                </p>
              </div>
            </div>
            
            <div v-if="error" class="mt-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {{ error }}
            </div>
          </div>
          <div class="mt-5 sm:mt-6 sm:grid sm:grid-cols-2 sm:gap-3 sm:grid-flow-row-dense">
            <button
              type="button"
              @click="deletePlan"
              :disabled="isDeleting"
              class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:col-start-2 sm:text-sm"
            >
              <svg v-if="isDeleting" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ isDeleting ? $t('common.loading') : $t('common.remove') }}
            </button>
            <button
              type="button"
              @click="cancelDelete"
              class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:col-start-1 sm:text-sm"
            >
              {{ $t('common.cancel') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../../stores/auth';
import Breadcrumb from '@/components/Breadcrumb.vue';

const { t } = useI18n();

const router = useRouter();
const authStore = useAuthStore();
const route = useRoute();

// Add breadcrumb items
const breadcrumbItems = computed(() => {
  const path = route.path;
  const segments = path.split('/').filter(Boolean);
  
  return segments.map((segment, index) => {
    const path = '/' + segments.slice(0, index + 1).join('/');
    let name = segment.charAt(0).toUpperCase() + segment.slice(1);
    
    // Special handling for specific segments
    if (segment === 'plans') {
      name = t('admin.plansManagement');
    }
    
    return { name, path };
  });
});

const plansList = ref([]);

async function fetchPlans() {
  try {
    plansList.value = await authStore.getPlans();
  } catch (error) {
    error.value = t('admin.errorLoadingPlans') + ': ' + error.message;
  }
}

onMounted(async () => {
  await fetchPlans();
});
const newPlan = ref({ 
  title: '', 
  sessionsPerWeek: 1, 
  price: 0, 
  description: '',
  commission: 40, // Default commission percentage
  discountQuarterly: 5, // Default 5% discount for 3 months
  discountSemiannual: 10, // Default 10% discount for 6 months
  discountAnnual: 20 // Default 20% discount for 12 months
});
const editingPlan = ref(null);
const planToDelete = ref(null);
const showAddPlanModal = ref(false);
const showEditPlanModal = ref(false);
const showDeleteConfirmModal = ref(false);
const isDeleting = ref(false);
const isAdding = ref(false);
const error = ref('');

// Form validation
const formErrors = ref({
  title: '',
  price: ''
});

const hasFormErrors = computed(() => {
  return Object.values(formErrors.value).some(error => error !== '');
});

function validateField(field) {
  switch (field) {
    case 'title':
      formErrors.value.title = !newPlan.value.title.trim() ? t('admin.planTitleRequired') : '';
      break;
    case 'price':
      formErrors.value.price = newPlan.value.price <= 0 ? t('admin.priceMustBeGreaterThanZero') : '';
      break;
  }
}

function openAddPlanModal() {
  newPlan.value = { title: '', sessionsPerWeek: 1, price: 0, description: '' };
  formErrors.value = { title: '', price: '' };
  error.value = '';
  showAddPlanModal.value = true;
}

function closeAddPlanModal() {
  showAddPlanModal.value = false;
}

function openEditPlanModal(plan) {
  // Create a copy of the plan with default values for new fields if they don't exist
  editingPlan.value = { 
    ...plan,
    commission: plan.commission || 40,
    discountQuarterly: plan.discountQuarterly || 0,
    discountSemiannual: plan.discountSemiannual || 0,
    discountAnnual: plan.discountAnnual || 0
  };
  showEditPlanModal.value = true;
}

function closeEditPlanModal() {
  showEditPlanModal.value = false;
}

function confirmDeletePlan(plan) {
  planToDelete.value = plan;
  showDeleteConfirmModal.value = true;
}

function cancelDelete() {
  showDeleteConfirmModal.value = false;
}

async function addPlan() {
  if (hasFormErrors.value) return;

  try {
    isAdding.value = true;
    error.value = '';

    await authStore.addPlan({
      title: newPlan.value.title.trim(),
      sessionsPerWeek: parseInt(newPlan.value.sessionsPerWeek),
      price: parseFloat(newPlan.value.price),
      description: newPlan.value.description.trim()
    });
    
    await fetchPlans();
    closeAddPlanModal();
  } catch (err) {
    error.value = err.message || t('admin.errorAddingPlan');
  } finally {
    isAdding.value = false;
  }
}

async function updatePlan() {
  try {
    await authStore.updatePlan(editingPlan.value.id, {
      title: editingPlan.value.title,
      sessionsPerWeek: parseInt(editingPlan.value.sessionsPerWeek),
      price: parseFloat(editingPlan.value.price),
      description: editingPlan.value.description
    });
    await fetchPlans();
    closeEditPlanModal();
  } catch (error) {
    error.value = t('admin.errorUpdatingPlan') + ': ' + error.message;
    error.value = error.message;
  }
}

async function deletePlan() {
  if (!planToDelete.value) return;
  
  isDeleting.value = true;
  try {
    await authStore.deletePlan(planToDelete.value.id);
    await fetchPlans();
    cancelDelete();
  } catch (error) {
    error.value = t('admin.errorRemovingPlan') + ': ' + error.message;
    error.value = error.message;
  } finally {
    isDeleting.value = false;
  }
}
</script>

<style scoped>
/* Add any component-specific styles here */
</style>