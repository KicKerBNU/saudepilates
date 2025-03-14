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

export const useEvolutionStore = defineStore('evolution', {
  state: () => ({
    evolutions: [],
    loading: false,
    error: null
  }),
  
  getters: {
    getEvolutionsByStudentId: (state) => (studentId) => {
      if (!studentId) return [];
      return state.evolutions.filter(evolution => evolution.studentId === studentId);
    }
  },
  
  actions: {
    // Temporary solution while the Firebase index is building
    async fetchStudentEvolutions(studentId) {
      if (!studentId) return [];
      
      this.loading = true;
      this.error = null;
      
      try {
        // Use a simple query without orderBy while index is building
        const evolutionsRef = collection(db, 'evolutions');
        const simpleQuery = query(
          evolutionsRef, 
          where('studentId', '==', studentId)
        );
        
        const snapshot = await getDocs(simpleQuery);
        if (snapshot.empty) {
          this.evolutions = [];
          return [];
        }
        
        // Map the docs to objects and include the id
        const evolutions = snapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data()
        }));
        
        // Sort on the client side while waiting for the index
        const sortedEvolutions = [...evolutions].sort((a, b) => {
          // Use string comparison if the dates are strings
          if (typeof a.date === 'string' && typeof b.date === 'string') {
            return b.date.localeCompare(a.date);
          }
          // Use timestamp comparison if dates are Date objects or timestamps
          return new Date(b.date).getTime() - new Date(a.date).getTime();
        });
        
        this.evolutions = sortedEvolutions;
        return sortedEvolutions;
      } catch (error) {
        console.error('Error fetching evolutions:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // Add a new evolution record
    async addEvolution(evolutionData) {
      this.loading = true;
      this.error = null;
      
      try {
        const authStore = useAuthStore();
        
        // Ensure the professor ID is set
        if (!evolutionData.professorId) {
          evolutionData.professorId = authStore.userId;
        }
        
        // Add timestamps
        evolutionData.createdAt = new Date().toISOString();
        
        const docRef = await addDoc(collection(db, 'evolutions'), evolutionData);
        
        const newEvolution = {
          id: docRef.id,
          ...evolutionData
        };
        
        // Update local state
        this.evolutions.push(newEvolution);
        
        return newEvolution;
      } catch (error) {
        console.error('Error adding evolution:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // Update an existing evolution record
    async updateEvolution(evolutionId, updatedData) {
      if (!evolutionId) throw new Error('Evolution ID is required');
      
      this.loading = true;
      this.error = null;
      
      try {
        await updateDoc(doc(db, 'evolutions', evolutionId), updatedData);
        
        // Update local state
        const index = this.evolutions.findIndex(e => e.id === evolutionId);
        if (index !== -1) {
          this.evolutions[index] = {
            ...this.evolutions[index],
            ...updatedData
          };
        }
        
        return { id: evolutionId, ...updatedData };
      } catch (error) {
        console.error('Error updating evolution:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    // Delete an evolution record
    async deleteEvolution(evolutionId) {
      if (!evolutionId) throw new Error('Evolution ID is required');
      
      this.loading = true;
      this.error = null;
      
      try {
        await deleteDoc(doc(db, 'evolutions', evolutionId));
        
        // Update local state
        this.evolutions = this.evolutions.filter(e => e.id !== evolutionId);
        
        return true;
      } catch (error) {
        console.error('Error deleting evolution:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});
