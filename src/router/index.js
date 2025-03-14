import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/Home.vue'
import Pricing from '../components/Pricing.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Contact from '../views/Contact.vue'
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
    path: '/student',
    name: 'StudentDashboard',
    component: StudentDashboard,
    meta: { requiresAuth: true, role: 'student' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for authenticated routes and role-based access
router.beforeEach((to, from, next) => {
  // Only check authentication for protected routes
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // Lazy load auth store ONLY for protected routes
    import('../stores/auth').then(module => {
      const authStore = module.useAuthStore();
      
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
      
      // User is authenticated and has correct role
      next();
    });
  } else {
    // For public routes, don't check authentication at all
    next();
  }
});

export default router
