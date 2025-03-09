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
    async fetchStudents() {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        let studentsQuery;
        
        if (authStore.isAdmin) {
          // Admin can see all students
          studentsQuery = query(
            collection(db, 'students'),
            orderBy('createdAt', 'desc')
          );
        } else if (authStore.isProfessor) {
          // Professor can only see assigned students
          studentsQuery = query(
            collection(db, 'students'),
            where('professorId', '==', authStore.userId),
            orderBy('createdAt', 'desc')
          );
        } else {
          // Students should not access this store but for safety
          return [];
        }
        
        const snapshot = await getDocs(studentsQuery);
        this.students = snapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data()
        }));
        
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
        const docRef = doc(db, 'students', studentId);
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
