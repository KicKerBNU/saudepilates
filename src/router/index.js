import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/Home.vue'
import Pricing from '../components/Pricing.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

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
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/contact',
    name: 'Contact',
    component: () => import('../views/Contact.vue')
  },
  {
    path: '/guia',
    name: 'Guide',
    component: () => import('../views/GuideView.vue')
  },
  {
    path: '/privacy',
    name: 'PrivacyPolicy',
    component: () => import('../views/PrivacyPolicy.vue')
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/professors',
    name: 'ProfessorsManagement',
    component: () => import('../views/admin/ProfessorsManagement.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/students',
    name: 'StudentsManagement',
    component: () => import('../views/admin/StudentsManagement.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/plans',
    name: 'PlansManagement',
    component: () => import('../views/admin/PlansManagement.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/payments/new',
    name: 'PaymentRegistration',
    component: () => import('../views/admin/PaymentRegistration.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/payments/monthly',
    name: 'MonthlyPayments',
    component: () => import('../views/admin/MonthlyPayments.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/subscription',
    name: 'SubscriptionPayment',
    component: () => import('../views/admin/SubscriptionPayment.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/payments/visualization',
    name: 'PaymentVisualization',
    component: () => import('../views/admin/PaymentVisualization.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/settings',
    name: 'CompanySettings',
    component: () => import('../views/admin/CompanySettings.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/schedule',
    name: 'AdminSchedule',
    component: () => import('../views/admin/Schedule.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/anamnesis',
    name: 'AdminAnamnesis',
    component: () => import('../views/Anamnesis.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/professor',
    name: 'ProfessorDashboard',
    component: () => import('../views/professor/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/students',
    name: 'ProfessorStudents',
    component: () => import('../views/professor/Students.vue'),
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/earnings',
    name: 'ProfessorEarnings',
    component: () => import('../views/professor/EarningsHistory.vue'),
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/attendance-control',
    name: 'ProfessorAttendanceControl',
    component: () => import('../views/professor/AttendanceControl.vue'),
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/evolution',
    name: 'ProfessorEvolution',
    component: () => import('../views/professor/Evolution.vue'),
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/schedule',
    name: 'ProfessorSchedule',
    component: () => import('../views/professor/Schedule.vue'),
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/messages',
    name: 'ProfessorMessages',
    component: () => import('../views/professor/Messages.vue'),
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/anamnesis',
    name: 'ProfessorAnamnesis',
    component: () => import('../views/Anamnesis.vue'),
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/student',
    name: 'StudentDashboard',
    component: () => import('../views/student/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for authenticated routes and role-based access
router.beforeEach(async (to, from, next) => {
  try {
    // Import and initialize the auth store
    const authModule = await import('../stores/auth');
    const authStore = authModule.useAuthStore();
    
    // Initialize the auth store if not already initialized
    // This ensures the Firebase auth listener is set up
    if (!authStore._initialized) {
      await authStore.init();
    }
    
    // Only check authentication for protected routes
    if (to.matched.some(record => record.meta.requiresAuth)) {
      // Check if the user is authenticated
      if (!authStore.isAuthenticated) {
        // Redirect to login
        next({ name: 'Login' });
        return;
      }
      
      // Check role-based access
      if (to.meta.role) {
        if (to.meta.role === 'admin' && !authStore.isAdmin) {
          next({ name: 'Home' });
          return;
        }
        
        if (to.meta.role === 'professor' && !authStore.isProfessor) {
          next({ name: 'Home' });
          return;
        }
        
        if (to.meta.role === 'student' && !authStore.isStudent) {
          next({ name: 'Home' });
          return;
        }
      }
      
      // Check subscription status for admin users
      if (authStore.isAdmin && to.name !== 'SubscriptionPayment') {
        try {
          // Import subscription store
          const subscriptionModule = await import('../stores/subscription');
          const subscriptionStore = subscriptionModule.useSubscriptionStore();
          
          // Fetch subscription status
          await subscriptionStore.fetchSubscription();
          
          // If subscription is not valid, redirect to subscription payment page
          if (!subscriptionStore.isValid) {
            next({ name: 'SubscriptionPayment' });
            return;
          }
        } catch (error) {
          console.error('Subscription check error:', error);
        }
      }
    }
    
    // Continue navigation
    next();
  } catch (error) {
    console.error('Authentication error:', error);
    next({ name: 'Home' });
  }
});

export default router
