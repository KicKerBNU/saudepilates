import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/Home.vue'
import Pricing from '../components/Pricing.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/pricing',
    name: 'Pricing',
    component: Pricing
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
