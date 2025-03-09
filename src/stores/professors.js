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

export const useProfessorsStore = defineStore('professors', {
  state: () => ({
    professors: [],
    activeProfessor: null,
    loading: false,
    error: null
  }),
  
  getters: {
    activeProfessors: (state) => state.professors.filter(professor => professor.isActive),
    getProfessorById: (state) => (id) => {
      return state.professors.find(professor => professor.id === id);
    }
  },
  
  actions: {
    async fetchProfessors() {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Check if user is admin, only admins can access all professors
        if (!authStore.isAdmin) {
          this.error = 'Unauthorized access';
          return [];
        }
        
        const professorsQuery = query(
          collection(db, 'professors'),
          orderBy('createdAt', 'desc')
        );
        
        const snapshot = await getDocs(professorsQuery);
        this.professors = snapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data()
        }));
        
        return this.professors;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async getProfessorDetails(professorId) {
      this.loading = true;
      this.error = null;
      
      try {
        const docRef = doc(db, 'professors', professorId);
        const docSnap = await getDoc(docRef);
        
        if (docSnap.exists()) {
          this.activeProfessor = {
            id: docSnap.id,
            ...docSnap.data()
          };
          return this.activeProfessor;
        } else {
          throw new Error('Professor not found');
        }
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async addProfessor(professorData) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Check if user is admin
        if (!authStore.isAdmin) {
          throw new Error('Only admins can add professors');
        }
        
        // Add created timestamp and set active by default
        const newProfessorData = {
          ...professorData,
          isActive: true,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        };
        
        const docRef = await addDoc(collection(db, 'professors'), newProfessorData);
        
        // Add to local state
        const newProfessor = {
          id: docRef.id,
          ...newProfessorData
        };
        
        this.professors.unshift(newProfessor);
        
        return newProfessor;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateProfessor(professorId, updatedData) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Check if user is admin
        if (!authStore.isAdmin) {
          throw new Error('Only admins can update professors');
        }
        
        const professorRef = doc(db, 'professors', professorId);
        
        // Add updated timestamp
        const dataToUpdate = {
          ...updatedData,
          updatedAt: new Date().toISOString()
        };
        
        await updateDoc(professorRef, dataToUpdate);
        
        // Update in local state
        const index = this.professors.findIndex(professor => professor.id === professorId);
        if (index !== -1) {
          this.professors[index] = {
            ...this.professors[index],
            ...dataToUpdate
          };
        }
        
        // Update active professor if it's the one being edited
        if (this.activeProfessor && this.activeProfessor.id === professorId) {
          this.activeProfessor = {
            ...this.activeProfessor,
            ...dataToUpdate
          };
        }
        
        return this.professors[index];
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteProfessor(professorId) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Check if user is admin
        if (!authStore.isAdmin) {
          throw new Error('Only admins can delete professors');
        }
        
        await deleteDoc(doc(db, 'professors', professorId));
        
        // Remove from local state
        this.professors = this.professors.filter(professor => professor.id !== professorId);
        
        if (this.activeProfessor && this.activeProfessor.id === professorId) {
          this.activeProfessor = null;
        }
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async toggleProfessorStatus(professorId, isActive) {
      return this.updateProfessor(professorId, { isActive });
    }
  }
});
