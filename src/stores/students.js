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
  orderBy
} from 'firebase/firestore';
import { db } from '../firebase/config';
import { useAuthStore } from './auth';

export const useStudentsStore = defineStore('students', {
  state: () => ({
    students: [],
    activeStudent: null,
    loading: false,
    error: null
  }),
  
  getters: {
    activeStudents: (state) => state.students.filter(student => student.isActive),
    inactiveStudents: (state) => state.students.filter(student => !student.isActive),
    professorStudents: (state) => {
      const authStore = useAuthStore();
      return state.students.filter(student => 
        student.professorId === authStore.userId
      );
    },
    getStudentById: (state) => (id) => {
      return state.students.find(student => student.id === id);
    }
  },
  
  actions: {
    // Fetch a single student by ID
    fetchStudentById(studentId) {
      if (!studentId) return Promise.resolve(null);
      
      this.loading = true;
      return getDoc(doc(db, 'users', studentId))
        .then(docSnap => {
          if (docSnap.exists()) {
            return { id: docSnap.id, ...docSnap.data() };
          }
          return null;
        })
        .catch(error => {
          console.error('Error fetching student:', error);
          this.error = error.message;
          throw error;
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // Fetch a single plan by ID
    fetchPlanById(planId) {
      if (!planId) return Promise.resolve(null);
      
      return getDoc(doc(db, 'plans', planId))
        .then(docSnap => {
          if (docSnap.exists()) {
            return { id: docSnap.id, ...docSnap.data() };
          }
          return null;
        })
        .catch(error => {
          console.error('Error fetching plan:', error);
          throw error;
        });
    },
    
    // Fetch all plans from database
    async fetchStudentPlans() {
      this.loading = true;
      this.error = null;
      
      try {
        console.log('Fetching all plans for earnings calculation');
        const plansQuery = query(collection(db, 'plans'));
        const snapshot = await getDocs(plansQuery);
        
        const plans = snapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data()
        }));
        
        console.log('Plans fetched:', plans.length);
        return plans;
      } catch (error) {
        console.error('Error fetching plans:', error);
        this.error = error.message;
        return [];
      } finally {
        this.loading = false;
      }
    },
    async fetchStudents() {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        let studentsQuery;
        
        if (authStore.isAdmin) {
          // Admin can see all students
          studentsQuery = query(
            collection(db, 'users'),
            where('role', '==', 'student')
          );
        } else if (authStore.isProfessor) {
          // Professor can only see assigned students
          studentsQuery = query(
            collection(db, 'users'),
            where('role', '==', 'student'),
            where('professorId', '==', authStore.userId)
          );
        } else {
          // Students should not access this store but for safety
          return [];
        }
        
        const snapshot = await getDocs(studentsQuery);
        const students = snapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data()
        }));
        
        // Get all unique plan IDs
        const planIds = [...new Set(students.filter(s => s.planId).map(s => s.planId))];
        
        // Fetch all plans at once
        const plansData = await Promise.all(
          planIds.map(async (planId) => {
            const planDoc = await getDoc(doc(db, 'plans', planId));
            return planDoc.exists() ? { id: planDoc.id, ...planDoc.data() } : null;
          })
        );
        
        // Create a map of plan data
        const plansMap = plansData.reduce((acc, plan) => {
          if (plan) acc[plan.id] = plan;
          return acc;
        }, {});
        
        // Add plan data to students
        const studentsWithPlans = students.map(student => ({
          ...student,
          plan: student.planId ? plansMap[student.planId] || null : null
        }));
        
        this.students = studentsWithPlans;
        return this.students;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async getStudentDetails(studentId) {
      this.loading = true;
      this.error = null;
      
      try {
        const docRef = doc(db, 'users', studentId);
        const docSnap = await getDoc(docRef);
        
        if (docSnap.exists()) {
          this.activeStudent = {
            id: docSnap.id,
            ...docSnap.data()
          };
          return this.activeStudent;
        } else {
          throw new Error('Student not found');
        }
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async addStudent(studentData) {
      this.loading = true;
      this.error = null;
      
      try {
        // Add created timestamp and set active by default
        const newStudentData = {
          ...studentData,
          isActive: true,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        };
        
        const docRef = await addDoc(collection(db, 'students'), newStudentData);
        
        // Add to local state
        const newStudent = {
          id: docRef.id,
          ...newStudentData
        };
        
        this.students.unshift(newStudent);
        
        return newStudent;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateStudent(studentId, updatedData) {
      this.loading = true;
      this.error = null;
      
      try {
        const studentRef = doc(db, 'students', studentId);
        
        // Add updated timestamp
        const dataToUpdate = {
          ...updatedData,
          updatedAt: new Date().toISOString()
        };
        
        await updateDoc(studentRef, dataToUpdate);
        
        // Update in local state
        const index = this.students.findIndex(student => student.id === studentId);
        if (index !== -1) {
          this.students[index] = {
            ...this.students[index],
            ...dataToUpdate
          };
        }
        
        // Update active student if it's the one being edited
        if (this.activeStudent && this.activeStudent.id === studentId) {
          this.activeStudent = {
            ...this.activeStudent,
            ...dataToUpdate
          };
        }
        
        return this.students[index];
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteStudent(studentId) {
      this.loading = true;
      this.error = null;
      
      try {
        await deleteDoc(doc(db, 'students', studentId));
        
        // Remove from local state
        this.students = this.students.filter(student => student.id !== studentId);
        
        if (this.activeStudent && this.activeStudent.id === studentId) {
          this.activeStudent = null;
        }
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async toggleStudentStatus(studentId, isActive) {
      return this.updateStudent(studentId, { isActive });
    }
  }
});
