import { createApp } from 'vue'
import { createHead } from '@vueuse/head'
import router from './router'
import pinia from './stores'
import './style.css'
import App from './App.vue'

const app = createApp(App)
const head = createHead()

app.use(router)
app.use(pinia)
app.use(head)
app.mount('#app')
