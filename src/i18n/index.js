import { createI18n } from 'vue-i18n'
import pt from './locales/pt.json'

const supportedLanguages = ['en', 'fr', 'pt', 'es']

function getBrowserLanguage() {
  const browserLang = navigator.language || navigator.userLanguage
  const langCode = browserLang.split('-')[0].toLowerCase()
  if (supportedLanguages.includes(langCode)) {
    return langCode
  }
  return 'pt'
}

function getSavedLanguage() {
  const savedLang = localStorage.getItem('userLanguage')
  if (savedLang && supportedLanguages.includes(savedLang)) {
    return savedLang
  }
  return getBrowserLanguage()
}

const localeLoaders = {
  en: () => import('./locales/en.json'),
  es: () => import('./locales/es.json'),
  fr: () => import('./locales/fr.json'),
}

const loadedLocales = new Set(['pt'])

const i18n = createI18n({
  legacy: false,
  locale: 'pt',
  fallbackLocale: 'pt',
  messages: { pt }
})

export async function loadLocale(locale) {
  if (loadedLocales.has(locale) || !localeLoaders[locale]) return
  const messages = await localeLoaders[locale]()
  i18n.global.setLocaleMessage(locale, messages.default || messages)
  loadedLocales.add(locale)
}

const detectedLocale = getSavedLanguage()
if (detectedLocale !== 'pt') {
  loadLocale(detectedLocale).then(() => {
    i18n.global.locale.value = detectedLocale
  })
} 

export default i18n
