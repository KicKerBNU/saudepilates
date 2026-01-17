import { createApp } from 'vue'
import { createHead } from '@vueuse/head'
import router from './router'
import pinia from './stores'
import i18n from './i18n'
import './style.css'
import App from './App.vue'

const app = createApp(App)
const head = createHead()

app.use(router)
app.use(pinia)
app.use(i18n)
app.use(head)
app.mount('#app')
