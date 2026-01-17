import { createI18n } from 'vue-i18n'
import en from './locales/en.json'
import fr from './locales/fr.json'
import pt from './locales/pt.json'
import es from './locales/es.json'

// Function to detect browser language
function getBrowserLanguage() {
  const browserLang = navigator.language || navigator.userLanguage
  const langCode = browserLang.split('-')[0].toLowerCase()
  
  // Supported languages
  const supportedLanguages = ['en', 'fr', 'pt', 'es']
  
  // Check if browser language is supported
  if (supportedLanguages.includes(langCode)) {
    return langCode
  }
  
  // Fallback to English
  return 'en'
}

// Get saved language from localStorage or detect from browser
function getSavedLanguage() {
  const savedLang = localStorage.getItem('userLanguage')
  if (savedLang && ['en', 'fr', 'pt', 'es'].includes(savedLang)) {
    return savedLang
  }
  return getBrowserLanguage()
}

const i18n = createI18n({
  legacy: false,
  locale: getSavedLanguage(),
  fallbackLocale: 'en',
  messages: {
    en,
    fr,
    pt,
    es
  }
})

export default i18n
