import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/Home.vue'
import Pricing from '../components/Pricing.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Contact from '../views/Contact.vue'
import GuideView from '../views/GuideView.vue'
import AdminDashboard from '../views/admin/Dashboard.vue'
import ProfessorsManagement from '../views/admin/ProfessorsManagement.vue'
import StudentsManagement from '../views/admin/StudentsManagement.vue'
import ProfessorDashboard from '../views/professor/Dashboard.vue'
import ProfessorStudents from '../views/professor/Students.vue'
import ProfessorEarnings from '../views/professor/EarningsHistory.vue'
import ProfessorAttendanceControl from '../views/professor/AttendanceControl.vue'
import ProfessorEvolution from '../views/professor/Evolution.vue'
import ProfessorSchedule from '../views/professor/Schedule.vue'
import StudentDashboard from '../views/student/Dashboard.vue'
import PlansManagement from '../views/admin/PlansManagement.vue'
import PaymentRegistration from '../views/admin/PaymentRegistration.vue'
import MonthlyPayments from '../views/admin/MonthlyPayments.vue'
import SubscriptionPayment from '../views/admin/SubscriptionPayment.vue'
import PaymentVisualization from '../views/admin/PaymentVisualization.vue'
import ProfessorMessages from '../views/professor/Messages.vue'
import NotFound from '../views/NotFound.vue'
import PrivacyPolicy from '../views/PrivacyPolicy.vue'

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
    component: Contact
  },
  {
    path: '/guia',
    name: 'Guide',
    component: GuideView
  },
  {
    path: '/privacy',
    name: 'PrivacyPolicy',
    component: PrivacyPolicy
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/professors',
    name: 'ProfessorsManagement',
    component: ProfessorsManagement,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/students',
    name: 'StudentsManagement',
    component: StudentsManagement,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/plans',
    name: 'PlansManagement',
    component: PlansManagement,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/payments/new',
    name: 'PaymentRegistration',
    component: PaymentRegistration,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/payments/monthly',
    name: 'MonthlyPayments',
    component: MonthlyPayments,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/subscription',
    name: 'SubscriptionPayment',
    component: SubscriptionPayment,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/payments/visualization',
    name: 'PaymentVisualization',
    component: PaymentVisualization,
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/professor',
    name: 'ProfessorDashboard',
    component: ProfessorDashboard,
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/students',
    name: 'ProfessorStudents',
    component: ProfessorStudents,
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/earnings',
    name: 'ProfessorEarnings',
    component: ProfessorEarnings,
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/attendance-control',
    name: 'ProfessorAttendanceControl',
    component: ProfessorAttendanceControl,
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/evolution',
    name: 'ProfessorEvolution',
    component: ProfessorEvolution,
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/schedule',
    name: 'ProfessorSchedule',
    component: ProfessorSchedule,
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/professor/messages',
    name: 'ProfessorMessages',
    component: ProfessorMessages,
    meta: { requiresAuth: true, role: 'professor' }
  },
  {
    path: '/student',
    name: 'StudentDashboard',
    component: StudentDashboard,
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
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
