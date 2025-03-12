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
        // Use a simpler query approach that doesn't require compound indexes
        // First query by professorId only, then filter in JS
        let attendanceQuery = collection(db, 'attendanceRecords');
        let mainConstraint;
        
        // Determine the main filter (we'll use just one in the query to avoid index issues)
        if (professorId) {
          mainConstraint = where('professorId', '==', professorId);
        } else if (studentId) {
          mainConstraint = where('studentId', '==', studentId);
        } else if (authStore.isProfessor) {
          mainConstraint = where('professorId', '==', authStore.userId);
        } else if (authStore.isStudent) {
          mainConstraint = where('studentId', '==', authStore.userId);
        } else {
          // No specific filter, might return all records (use carefully)
        }
        
        // Create the query with the single constraint
        const finalQuery = mainConstraint ? query(attendanceQuery, mainConstraint) : attendanceQuery;
        
        const snapshot = await getDocs(finalQuery);
        
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
          results = results.filter(record => record.studentId === studentId);
        }
        
        if (professorId && (!mainConstraint || mainConstraint.toString().indexOf('professorId') === -1)) {
          results = results.filter(record => record.professorId === professorId);
        }
        
        // Apply date filtering in JavaScript
        if (startDate && endDate) {
          const startTs = new Date(startDate).getTime();
          const endTs = new Date(endDate).getTime();
          
          results = results.filter(record => {
            const recordDate = new Date(record.date).getTime();
            return recordDate >= startTs && recordDate <= endTs;
          });
        }
        
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
        // Get current month's start and end dates
        const now = new Date();
        const startDate = new Date(now.getFullYear(), now.getMonth(), 1);
        const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
        
        // Query scheduledClasses collection (note: with a 'd') for the professor's classes this month
        const classesQuery = query(
          collection(db, 'scheduledClasses'),
          where('professorId', '==', professorId)
        );

        const classesSnapshot = await getDocs(classesQuery);
        
        // Map and filter classes to only include those from this month
        const scheduledClasses = classesSnapshot.docs
          .map(doc => ({
            id: doc.id,
            ...doc.data()
          }))
          .filter(classItem => {
            try {
              // Convert Firestore timestamp to JS Date
              const classDate = classItem.date && typeof classItem.date.toDate === 'function' 
                ? classItem.date.toDate() 
                : new Date(classItem.date);
              
              // Check if class date is in the current month
              return classDate >= startDate && classDate <= endDate;
            } catch (err) {
              return false;
            }
          });
        
        // Get professor's commission rate
        const professorDoc = await getDoc(doc(db, 'users', professorId));
        if (!professorDoc.exists()) {
          throw new Error('Professor not found');
        }
        
        const professor = professorDoc.data();
        const commissionRate = (professor && professor.commission ? professor.commission : 0) / 100;
        
        // Group classes by student to count attendances
        const studentAttendance = scheduledClasses.reduce((acc, classItem) => {
          if (classItem.studentId) {
            acc[classItem.studentId] = (acc[classItem.studentId] || 0) + 1;
          }
          return acc;
        }, {});
        
        // Get all unique student IDs
        const studentIds = Object.keys(studentAttendance);
        
        if (studentIds.length === 0) {
          this.monthlyEarnings = 0;
          return 0;
        }

        // Fetch all students data at once
        const studentsData = await Promise.all(
          studentIds.map(id => getDoc(doc(db, 'users', id)))
        );

        // Extract students with plans
        const studentsWithPlans = studentsData
          .filter(doc => doc.exists())
          .map(doc => ({
            id: doc.id,
            ...doc.data()
          }))
          .filter(student => student && student.planId);
          
        if (studentsWithPlans.length === 0) {
          this.monthlyEarnings = 0;
          return 0;
        }

        // Get unique plan IDs and create student map
        const planIds = [...new Set(studentsWithPlans.map(student => student.planId))];
        const studentsMap = Object.fromEntries(studentsWithPlans.map(student => [student.id, student]));
        
        // Fetch all plans at once
        const plansData = await Promise.all(
          planIds.map(id => getDoc(doc(db, 'plans', id)))
        );

        // Create plan map for quick lookups
        const plansMap = {};
        plansData.forEach(doc => {
          if (doc.exists()) {
            plansMap[doc.id] = { id: doc.id, ...doc.data() };
          }
        });

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
              
              // Calculate price per class according to the formula:
              // Plan price / sessions per week / 4
              const pricePerClass = price / sessionsPerWeek / 4;
              const studentEarning = pricePerClass * attendanceCount * commissionRate;
              
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
