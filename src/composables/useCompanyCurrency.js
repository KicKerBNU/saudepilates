import { computed } from 'vue';
import { useAuthStore } from '@/stores/auth';

/**
 * Composable to get company currency based on company language
 * Returns currency symbol and locale for formatting
 */
export function useCompanyCurrency() {
  const authStore = useAuthStore();
  
  const companyLanguage = computed(() => authStore.companyLanguage || 'pt');
  
  const currency = computed(() => {
    const currencyMap = {
      'en': '$',  // USD
      'es': '€',  // EUR
      'fr': '€',  // EUR
      'pt': 'R$'  // BRL
    };
    return currencyMap[companyLanguage.value] || 'R$';
  });
  
  const currencyLocale = computed(() => {
    const localeMap = {
      'en': 'en-US',
      'es': 'es-ES',
      'fr': 'fr-FR',
      'pt': 'pt-BR'
    };
    return localeMap[companyLanguage.value] || 'pt-BR';
  });
  
  const formatCurrency = (value) => {
    return value.toLocaleString(currencyLocale.value, { 
      minimumFractionDigits: 2, 
      maximumFractionDigits: 2 
    });
  };
  
  const formatCurrencyWithSymbol = (value) => {
    return `${currency.value} ${formatCurrency(value)}`;
  };
  
  return {
    currency,
    currencyLocale,
    formatCurrency,
    formatCurrencyWithSymbol,
    companyLanguage
  };
}
