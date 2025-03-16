import { defineStore } from 'pinia';
import { 
  collection, 
  doc, 
  addDoc, 
  updateDoc, 
  deleteDoc, 
  getDocs, 
  getDoc, 
  query, 
  where,
  orderBy,
  Timestamp
} from 'firebase/firestore';
import { db } from '../firebase/config';
import { useAuthStore } from './auth';

export const usePaymentsStore = defineStore('payments', {
  state: () => ({
    studentPayments: [],
    professorPayments: [],
    loading: false,
    error: null
  }),
  
  getters: {
    // Student payment getters
    studentPaymentsByMonth: (state) => (month, year) => {
      return state.studentPayments.filter(payment => {
        const paymentDate = new Date(payment.paymentDate);
        return paymentDate.getMonth() === month && paymentDate.getFullYear() === year;
      });
    },
    
    // Get payments for a specific student
    getStudentPayments: (state) => (studentId) => {
      return state.studentPayments.filter(payment => payment.studentId === studentId);
    },
    
    // Professor payment getters
    professorPaymentsByMonth: (state) => (month, year) => {
      return state.professorPayments.filter(payment => {
        const paymentDate = new Date(payment.paymentDate);
        return paymentDate.getMonth() === month && paymentDate.getFullYear() === year;
      });
    },
    
    // Get payments for a specific professor
    getProfessorPayments: (state) => (professorId) => {
      return state.professorPayments.filter(payment => payment.professorId === professorId);
    },
    
    // Get current month's earnings for a professor
    currentMonthEarnings: (state) => (professorId) => {
      const now = new Date();
      const currentMonth = now.getMonth();
      const currentYear = now.getFullYear();
      
      const payments = state.professorPayments.filter(payment => {
        const paymentDate = new Date(payment.paymentDate);
        return (
          payment.professorId === professorId &&
          paymentDate.getMonth() === currentMonth &&
          paymentDate.getFullYear() === currentYear
        );
      });
      
      return payments.reduce((total, payment) => total + payment.amount, 0);
    }
  },
  
  actions: {
    // Student Payment Actions
    async fetchStudentPayments(studentId = null) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        let paymentsQuery;
        
        // Always filter by companyId first
        if (!authStore.companyId) {
          return [];
        }

        if (studentId) {
          // Fetch payments for a specific student in this company
          paymentsQuery = query(
            collection(db, 'studentPayments'),
            where('companyId', '==', authStore.companyId),
            where('studentId', '==', studentId),
            orderBy('paymentDate', 'desc')
          );
        } else if (authStore.isAdmin) {
          // Admin can see all payments for their company only
          paymentsQuery = query(
            collection(db, 'studentPayments'),
            where('companyId', '==', authStore.companyId),
            orderBy('paymentDate', 'desc')
          );
        } else if (authStore.isProfessor) {
          // Professor can only see payments for their students in their company
          paymentsQuery = query(
            collection(db, 'studentPayments'),
            where('companyId', '==', authStore.companyId),
            where('professorId', '==', authStore.userId),
            orderBy('paymentDate', 'desc')
          );
        } else if (authStore.isStudent) {
          // Students can only see their own payments within their company
          paymentsQuery = query(
            collection(db, 'studentPayments'),
            where('companyId', '==', authStore.companyId),
            where('studentId', '==', authStore.userId),
            orderBy('paymentDate', 'desc')
          );
        } else {
          return [];
        }
        
        const snapshot = await getDocs(paymentsQuery);
        this.studentPayments = snapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data(),
          paymentDate: doc.data().paymentDate.toDate().toISOString()
        }));
        
        return this.studentPayments;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async addStudentPayment(paymentData) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Only admins can add payments
        if (!authStore.isAdmin) {
          throw new Error('Only admins can add payments');
        }
        
        // Add created timestamp and company ID
        const newPaymentData = {
          ...paymentData,
          companyId: authStore.companyId, // Ensure payment is associated with admin's company
          paymentDate: Timestamp.fromDate(new Date(paymentData.paymentDate || new Date())),
          createdAt: Timestamp.now(),
          createdBy: authStore.userId
        };
        
        // Validate company ID exists
        if (!newPaymentData.companyId) {
          throw new Error('Payment must be associated with a company');
        }
        
        const docRef = await addDoc(collection(db, 'studentPayments'), newPaymentData);
        
        // Add to local state
        const newPayment = {
          id: docRef.id,
          ...newPaymentData,
          paymentDate: newPaymentData.paymentDate.toDate().toISOString()
        };
        
        this.studentPayments.unshift(newPayment);
        
        return newPayment;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateStudentPayment(paymentId, updatedData) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Only admins can update payments
        if (!authStore.isAdmin) {
          throw new Error('Only admins can update payments');
        }
        
        const paymentRef = doc(db, 'studentPayments', paymentId);
        
        // Handle date conversion if present
        const dataToUpdate = { ...updatedData };
        if (dataToUpdate.paymentDate) {
          dataToUpdate.paymentDate = Timestamp.fromDate(new Date(dataToUpdate.paymentDate));
        }
        
        await updateDoc(paymentRef, dataToUpdate);
        
        // Update in local state
        const index = this.studentPayments.findIndex(payment => payment.id === paymentId);
        if (index !== -1) {
          this.studentPayments[index] = {
            ...this.studentPayments[index],
            ...updatedData,
            paymentDate: dataToUpdate.paymentDate ? 
              dataToUpdate.paymentDate.toDate().toISOString() : 
              this.studentPayments[index].paymentDate
          };
        }
        
        return this.studentPayments[index];
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteStudentPayment(paymentId) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Only admins can delete payments
        if (!authStore.isAdmin) {
          throw new Error('Only admins can delete payments');
        }
        
        await deleteDoc(doc(db, 'studentPayments', paymentId));
        
        // Remove from local state
        this.studentPayments = this.studentPayments.filter(payment => payment.id !== paymentId);
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // Professor Payment Actions
    async fetchProfessorPayments(professorId = null) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        let paymentsQuery;
        
        if (professorId) {
          // Fetch payments for a specific professor
          paymentsQuery = query(
            collection(db, 'professorPayments'),
            where('professorId', '==', professorId),
            orderBy('paymentDate', 'desc')
          );
        } else if (authStore.isAdmin) {
          // Admin can see all payments
          paymentsQuery = query(
            collection(db, 'professorPayments'),
            orderBy('paymentDate', 'desc')
          );
        } else if (authStore.isProfessor) {
          // Professors can only see their own payments
          paymentsQuery = query(
            collection(db, 'professorPayments'),
            where('professorId', '==', authStore.userId),
            orderBy('paymentDate', 'desc')
          );
        } else {
          return [];
        }
        
        const snapshot = await getDocs(paymentsQuery);
        this.professorPayments = snapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data(),
          paymentDate: doc.data().paymentDate.toDate().toISOString()
        }));
        
        return this.professorPayments;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async addProfessorPayment(paymentData) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Only admins can add professor payments
        if (!authStore.isAdmin) {
          throw new Error('Only admins can add professor payments');
        }
        
        // Add created timestamp and company ID
        const newPaymentData = {
          ...paymentData,
          companyId: authStore.companyId, // Ensure payment is associated with admin's company
          paymentDate: Timestamp.fromDate(new Date(paymentData.paymentDate || new Date())),
          createdAt: Timestamp.now(),
          createdBy: authStore.userId
        };
        
        // Validate company ID exists
        if (!newPaymentData.companyId) {
          throw new Error('Payment must be associated with a company');
        }
        
        const docRef = await addDoc(collection(db, 'professorPayments'), newPaymentData);
        
        // Add to local state
        const newPayment = {
          id: docRef.id,
          ...newPaymentData,
          paymentDate: newPaymentData.paymentDate.toDate().toISOString()
        };
        
        this.professorPayments.unshift(newPayment);
        
        return newPayment;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateProfessorPayment(paymentId, updatedData) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Only admins can update professor payments
        if (!authStore.isAdmin) {
          throw new Error('Only admins can update professor payments');
        }
        
        const paymentRef = doc(db, 'professorPayments', paymentId);
        
        // Handle date conversion if present
        const dataToUpdate = { ...updatedData };
        if (dataToUpdate.paymentDate) {
          dataToUpdate.paymentDate = Timestamp.fromDate(new Date(dataToUpdate.paymentDate));
        }
        
        await updateDoc(paymentRef, dataToUpdate);
        
        // Update in local state
        const index = this.professorPayments.findIndex(payment => payment.id === paymentId);
        if (index !== -1) {
          this.professorPayments[index] = {
            ...this.professorPayments[index],
            ...updatedData,
            paymentDate: dataToUpdate.paymentDate ? 
              dataToUpdate.paymentDate.toDate().toISOString() : 
              this.professorPayments[index].paymentDate
          };
        }
        
        return this.professorPayments[index];
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteProfessorPayment(paymentId) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Only admins can delete professor payments
        if (!authStore.isAdmin) {
          throw new Error('Only admins can delete professor payments');
        }
        
        await deleteDoc(doc(db, 'professorPayments', paymentId));
        
        // Remove from local state
        this.professorPayments = this.professorPayments.filter(payment => payment.id !== paymentId);
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});
