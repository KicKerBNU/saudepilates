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
        console.log('Fetching attendance records with params:', { studentId, professorId, startDate, endDate });
        
        // Use a simpler query approach that doesn't require compound indexes
        // First query by professorId only, then filter in JS
        let attendanceQuery = collection(db, 'attendanceRecords');
        let mainConstraint;
        
        // Determine the main filter (we'll use just one in the query to avoid index issues)
        if (professorId) {
          console.log('Filtering primarily by professorId:', professorId);
          mainConstraint = where('professorId', '==', professorId);
        } else if (studentId) {
          console.log('Filtering primarily by studentId:', studentId);
          mainConstraint = where('studentId', '==', studentId);
        } else if (authStore.isProfessor) {
          console.log('Filtering by current professor:', authStore.userId);
          mainConstraint = where('professorId', '==', authStore.userId);
        } else if (authStore.isStudent) {
          console.log('Filtering by current student:', authStore.userId);
          mainConstraint = where('studentId', '==', authStore.userId);
        } else {
          // No specific filter, might return all records (use carefully)
          console.log('No specific filter applied');
        }
        
        // Create the query with the single constraint
        const finalQuery = mainConstraint ? query(attendanceQuery, mainConstraint) : attendanceQuery;
        console.log('Executing Firestore query with single constraint');
        
        const snapshot = await getDocs(finalQuery);
        console.log('Raw records fetched:', snapshot.docs.length);
        
        // Process the results - apply remaining filters in JavaScript
        let results = snapshot.docs.map(doc => {
          const data = doc.data();
          return {
            id: doc.id,
            ...data,
            date: data.date.toDate().toISOString(),
            // Keep the original Timestamp for filtering
            rawDate: data.date
          };
        });
        
        // Apply additional filters in JavaScript
        if (studentId && (!mainConstraint || mainConstraint.toString().indexOf('studentId') === -1)) {
          console.log('Filtering results by studentId in JS');
          results = results.filter(record => record.studentId === studentId);
        }
        
        if (professorId && (!mainConstraint || mainConstraint.toString().indexOf('professorId') === -1)) {
          console.log('Filtering results by professorId in JS');
          results = results.filter(record => record.professorId === professorId);
        }
        
        // Apply date filtering in JavaScript
        if (startDate && endDate) {
          console.log('Filtering by date range in JS:', startDate, 'to', endDate);
          const startTs = new Date(startDate).getTime();
          const endTs = new Date(endDate).getTime();
          
          results = results.filter(record => {
            const recordDate = new Date(record.date).getTime();
            return recordDate >= startTs && recordDate <= endTs;
          });
        }
        
        console.log('Filtered records:', results.length);
        this.attendanceRecords = results;
        
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
        
        const docRef = await addDoc(collection(db, 'attendanceRecords'), newAttendanceData);
        
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
        
        // Try to get attendance records directly from Firestore to avoid any issues with the store's state
        const attendanceQuery = query(
          collection(db, 'attendanceRecords'),
          where('professorId', '==', professorId)
        );
        
        const snapshot = await getDocs(attendanceQuery);
        
        // Filter to only include present records from this month
        const attendanceRecords = snapshot.docs
          .map(doc => ({ id: doc.id, ...doc.data() }))
          .filter(record => {
            if (!record.present) return false;
            
            // Convert Firestore timestamp to JS Date and check if in current month
            const recordDate = record.date && typeof record.date.toDate === 'function' ? record.date.toDate() : new Date(record.date);
            return recordDate >= startDate && recordDate <= endDate;
          });
        
        // Get professor's commission rate
        const professorDoc = await getDoc(doc(db, 'users', professorId));
        if (!professorDoc.exists()) {
          throw new Error('Professor not found');
        }
        
        const professor = professorDoc.data();
        const commissionRate = (professor && professor.commission ? professor.commission : 0) / 100; // Default to 0% if not set
        
        // Count attendance per student
        const studentAttendance = attendanceRecords.reduce((acc, record) => {
          acc[record.studentId] = (acc[record.studentId] || 0) + 1;
          return acc;
        }, {});
        
        // Get all unique student IDs
        const studentIds = Object.keys(studentAttendance);
        
        if (studentIds.length === 0) {
          this.monthlyEarnings = 0;
          return 0;
        }

        // Fetch all students at once
        const studentsData = await Promise.all(
          studentIds.map(id => getDoc(doc(db, 'users', id)))
        );

        // Get all unique plan IDs
        const planIds = studentsData
          .filter(doc => doc.exists())
          .map(doc => doc.data())
          .filter(student => student && student.planId)
          .map(student => student.planId);

        if (planIds.length === 0) {
          this.monthlyEarnings = 0;
          return 0;
        }

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

        // Calculate total earnings
        let total = 0;
        for (const [studentId, attendanceCount] of Object.entries(studentAttendance)) {
          const student = studentsMap[studentId];
          
          if (student && student.planId) {
            const plan = plansMap[student.planId];
            
            if (plan && plan.price && plan.sessionsPerWeek) {
              // Use safe Number conversions to prevent NaN
              const price = Number(plan.price) || 0;
              const sessionsPerWeek = Number(plan.sessionsPerWeek) || 1;
              
              const classesPerMonth = sessionsPerWeek * 4;
              const pricePerClass = price / classesPerMonth;
              const studentEarning = pricePerClass * commissionRate * attendanceCount;
              
              // Only add valid numbers to the total
              if (!isNaN(studentEarning)) {
                total += studentEarning;
              }
            }
          }
        }
        
        // Store the result and return it
        this.monthlyEarnings = Number(total) || 0;
        return this.monthlyEarnings;
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
