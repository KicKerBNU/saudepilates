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
import { startOfDay, endOfDay } from 'date-fns';
import { db } from '../firebase/config';
import { useAuthStore } from './auth';

export const useAttendanceStore = defineStore('attendance', {
  state: () => ({
    attendanceRecords: [],
    loading: false,
    error: null
  }),
  
  getters: {
    getStudentAttendance: (state) => (studentId) => {
      return state.attendanceRecords.filter(record => record.studentId === studentId);
    },
    
    getAttendanceByDate: (state) => (date) => {
      const targetDate = new Date(date);
      return state.attendanceRecords.filter(record => {
        const recordDate = new Date(record.date);
        return (
          recordDate.getDate() === targetDate.getDate() &&
          recordDate.getMonth() === targetDate.getMonth() &&
          recordDate.getFullYear() === targetDate.getFullYear()
        );
      });
    },
    
    getStudentAttendanceByMonth: (state) => (studentId, month, year) => {
      return state.attendanceRecords.filter(record => {
        const recordDate = new Date(record.date);
        return (
          record.studentId === studentId &&
          recordDate.getMonth() === month &&
          recordDate.getFullYear() === year
        );
      });
    },
    
    getAttendanceForProfessor: (state) => (professorId) => {
      return state.attendanceRecords.filter(record => record.professorId === professorId);
    }
  },
  
  actions: {
    async fetchAttendanceRecords(studentId = null, professorId = null, startDate = null, endDate = null) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        let attendanceQuery = collection(db, 'attendance');
        let constraints = [];
        
        // Filter by student if provided
        if (studentId) {
          constraints.push(where('studentId', '==', studentId));
        }
        
        // Filter by professor if provided
        if (professorId) {
          constraints.push(where('professorId', '==', professorId));
        }
        
        // Filter by date range if provided
        if (startDate && endDate) {
          constraints.push(where('date', '>=', Timestamp.fromDate(new Date(startDate))));
          constraints.push(where('date', '<=', Timestamp.fromDate(new Date(endDate))));
        }
        
        // Add role-based filtering
        if (authStore.isProfessor && !professorId) {
          constraints.push(where('professorId', '==', authStore.userId));
        }
        
        if (authStore.isStudent && !studentId) {
          constraints.push(where('studentId', '==', authStore.userId));
        }
        
        // Always order by date, descending
        constraints.push(orderBy('date', 'desc'));
        
        // Apply all constraints to the query
        const finalQuery = query(attendanceQuery, ...constraints);
        
        const snapshot = await getDocs(finalQuery);
        this.attendanceRecords = snapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data(),
          date: doc.data().date.toDate().toISOString()
        }));
        
        return this.attendanceRecords;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async addAttendanceRecord(attendanceData) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Only professors can mark attendance
        if (!authStore.isProfessor && !authStore.isAdmin) {
          throw new Error('Only professors and admins can mark attendance');
        }
        
        // Add timestamps and professor ID
        const newAttendanceData = {
          ...attendanceData,
          date: Timestamp.fromDate(new Date(attendanceData.date || new Date())),
          createdAt: Timestamp.now(),
          professorId: attendanceData.professorId || authStore.userId
        };
        
        const docRef = await addDoc(collection(db, 'attendance'), newAttendanceData);
        
        // Add to local state
        const newRecord = {
          id: docRef.id,
          ...newAttendanceData,
          date: newAttendanceData.date.toDate().toISOString()
        };
        
        this.attendanceRecords.unshift(newRecord);
        
        return newRecord;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateAttendanceRecord(recordId, updatedData) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Only professors and admins can update attendance
        if (!authStore.isProfessor && !authStore.isAdmin) {
          throw new Error('Only professors and admins can update attendance');
        }
        
        const recordRef = doc(db, 'attendance', recordId);
        
        // Handle date conversion if present
        const dataToUpdate = { ...updatedData };
        if (dataToUpdate.date) {
          dataToUpdate.date = Timestamp.fromDate(new Date(dataToUpdate.date));
        }
        
        await updateDoc(recordRef, dataToUpdate);
        
        // Update in local state
        const index = this.attendanceRecords.findIndex(record => record.id === recordId);
        if (index !== -1) {
          this.attendanceRecords[index] = {
            ...this.attendanceRecords[index],
            ...updatedData,
            date: dataToUpdate.date ? 
              dataToUpdate.date.toDate().toISOString() : 
              this.attendanceRecords[index].date
          };
        }
        
        return this.attendanceRecords[index];
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteAttendanceRecord(recordId) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      
      try {
        // Only professors and admins can delete attendance records
        if (!authStore.isProfessor && !authStore.isAdmin) {
          throw new Error('Only professors and admins can delete attendance records');
        }
        
        await deleteDoc(doc(db, 'attendance', recordId));
        
        // Remove from local state
        this.attendanceRecords = this.attendanceRecords.filter(record => record.id !== recordId);
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});
