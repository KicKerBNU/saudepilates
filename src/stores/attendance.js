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
    error: null,
    monthlyEarnings: 0
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
    },

    getCurrentMonthEarnings: (state) => {
      return state.monthlyEarnings;
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
    
    async calculateMonthlyEarnings(professorId) {
      this.loading = true;
      this.error = null;
      
      try {
        // Get current month's attendance records
        const now = new Date();
        const startDate = new Date(now.getFullYear(), now.getMonth(), 1);
        const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
        
        // Fetch attendance records for this month
        await this.fetchAttendanceRecords(null, professorId, startDate.toISOString(), endDate.toISOString());
        
        // Get professor's commission rate
        const authStore = useAuthStore();
        const professorDoc = await getDoc(doc(db, 'users', professorId));
        const professor = professorDoc.data();
        const commissionRate = (professor?.commission || 0) / 100; // Default to 0% if not set
        
        // Get all students with attendance
        const studentIds = [...new Set(this.attendanceRecords
          .filter(record => record.present)
          .map(record => record.studentId))];

        // Fetch all students at once
        const studentsData = await Promise.all(
          studentIds.map(id => getDoc(doc(db, 'users', id)))
        );

        // Get all unique plan IDs
        const planIds = [...new Set(studentsData
          .map(doc => doc.data())
          .filter(student => student?.planId)
          .map(student => student.planId))];

        // Fetch all plans at once
        const plansData = await Promise.all(
          planIds.map(id => getDoc(doc(db, 'plans', id)))
        );

        // Create maps for quick lookups
        const plansMap = plansData.reduce((acc, doc) => {
          if (doc.exists()) {
            acc[doc.id] = { id: doc.id, ...doc.data() };
          }
          return acc;
        }, {});

        const studentsMap = studentsData.reduce((acc, doc) => {
          if (doc.exists()) {
            acc[doc.id] = { id: doc.id, ...doc.data() };
          }
          return acc;
        }, {});

        // Count attendance per student
        const studentAttendance = this.attendanceRecords
          .filter(record => record.present)
          .reduce((acc, record) => {
            acc[record.studentId] = (acc[record.studentId] || 0) + 1;
            return acc;
          }, {});

        // Calculate total earnings
        let total = 0;
        for (const [studentId, attendanceCount] of Object.entries(studentAttendance)) {
          const student = studentsMap[studentId];
          if (student?.planId) {
            const plan = plansMap[student.planId];
            if (plan) {
              const classesPerMonth = plan.sessionsPerWeek * 4;
              const pricePerClass = plan.price / classesPerMonth;
              total += pricePerClass * commissionRate * attendanceCount;
            }
          }
        }
        
        this.monthlyEarnings = total;
        return total;
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

        // If presence status was updated, recalculate earnings
        if (updatedData.present !== undefined) {
          await this.calculateMonthlyEarnings(this.attendanceRecords[index].professorId);
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
