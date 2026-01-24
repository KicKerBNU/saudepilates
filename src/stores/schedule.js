import { defineStore } from 'pinia';
import { ref } from 'vue';
import { db } from '../firebase/config';
import { 
  collection, 
  getDocs, 
  getDoc,
  addDoc, 
  updateDoc, 
  deleteDoc, 
  doc, 
  query, 
  where, 
  orderBy, 
  Timestamp 
} from 'firebase/firestore';
import { dateToFirebaseTimestamp, firebaseTimestampToLocalDate, formatDateYYYYMMDD, isSameDay } from '../utils/dateUtils';

export const useScheduleStore = defineStore('schedule', () => {
  const appointments = ref([]);
  const loading = ref(false);
  const error = ref(null);

  /**
   * Fetch all scheduled appointments for a professor within a date range
   * @param {string} professorId - ID of the professor
   * @param {Date} startDate - Start date for fetching appointments
   * @param {Date} endDate - End date for fetching appointments
   * @returns {Promise<Array>} - Array of appointment objects
   */
  const fetchProfessorSchedule = async (professorId, startDate, endDate) => {
    try {
      loading.value = true;
      error.value = null;
      
      // Convert dates to Firestore Timestamps using our utility functions
      // This ensures dates are stored consistently regardless of timezone
      const firestoreStartDate = dateToFirebaseTimestamp(startDate);
      const firestoreEndDate = dateToFirebaseTimestamp(endDate);
      
      // Only fetch from scheduledClasses collection
      // attendanceRecords are used only for earnings calculation, not for schedule display
      const classesQuery = query(
        collection(db, 'scheduledClasses'),
        where('professorId', '==', professorId)
      );
      
      const classesSnapshot = await getDocs(classesQuery);
      
      // Transform scheduled classes into appointment objects
      const appointmentsList = [];
      
      // Process scheduled classes
      for (const document of classesSnapshot.docs) {
        const data = document.data();
        
        // Use our date utilities to handle dates consistently
        const localAppointmentDate = firebaseTimestampToLocalDate(data.date);
        
        // Filter dates in memory - compare only the date part
        if (
          localAppointmentDate >= new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()) && 
          localAppointmentDate <= new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate())
        ) {
          // Fetch student name
          const studentDoc = await getStudentName(data.studentId);
          
          appointmentsList.push({
            id: document.id,
            studentId: data.studentId,
            studentName: studentDoc?.name || 'Aluno não encontrado',
            professorId: data.professorId,
            date: localAppointmentDate,
            time: data.startTime || data.time || data.date?.toDate().toISOString() || localAppointmentDate.toISOString(),
            duration: data.duration || 60,
            type: data.type || 'scheduled',
            status: data.status || 'scheduled',
            notes: data.notes || '',
            source: 'scheduled'
          });
        }
      }
      
      // Sort appointments by date
      appointmentsList.sort((a, b) => a.date - b.date);
      
      appointments.value = appointmentsList;
      return appointmentsList;
    } catch (err) {
      console.error('Error fetching professor schedule:', err);
      error.value = err.message;
      return [];
    } finally {
      loading.value = false;
    }
  };
  
  /**
   * Get a student's name by ID
   * @param {string} studentId - ID of the student (which is the user ID)
   * @returns {Promise<Object|null>} - Object with user data including name or null if not found
   */
  const getStudentName = async (studentId) => {
    try {
      // First check if the student exists in the users collection
      const userRef = doc(db, 'users', studentId);
      const userDoc = await getDoc(userRef);
      
      if (userDoc.exists()) {
        const userData = userDoc.data();
        // Return an object with at least the name property
        return {
          name: userData.displayName || userData.name || 'User ' + studentId.substring(0, 5),
          email: userData.email || '',
          userId: studentId
        };
      }
      
      // If not found in users, try the students collection as fallback
      const studentRef = doc(db, 'students', studentId);
      const studentDoc = await getDoc(studentRef);
      
      if (studentDoc.exists()) {
        return studentDoc.data();
      }
      
      return { name: 'Aluno ' + studentId.substring(0, 5) };
    } catch (err) {
      console.error('Error fetching student name:', err);
      return { name: 'Aluno (erro)' };
    }
  };
  
  /**
   * Fetch all scheduled appointments for a student within a date range
   * @param {string} studentId - ID of the student
   * @param {Date} startDate - Start date for fetching appointments
   * @param {Date} endDate - End date for fetching appointments
   * @returns {Promise<Array>} - Array of appointment objects
   */
  const fetchStudentSchedule = async (studentId, startDate, endDate) => {
    try {
      loading.value = true;
      error.value = null;
      
      // Convert dates to Firestore Timestamps using our utility functions
      const firestoreStartDate = dateToFirebaseTimestamp(startDate);
      const firestoreEndDate = dateToFirebaseTimestamp(endDate);
      
      // Fetch from scheduledClasses collection where studentId matches
      const classesQuery = query(
        collection(db, 'scheduledClasses'),
        where('studentId', '==', studentId)
      );
      
      const classesSnapshot = await getDocs(classesQuery);
      
      // Transform into appointment objects
      const appointmentsList = [];
      
      for (const document of classesSnapshot.docs) {
        const data = document.data();
        
        // Use our date utilities to handle dates consistently
        const localAppointmentDate = firebaseTimestampToLocalDate(data.date);
        
        // Filter dates in memory - compare only the date part
        if (
          localAppointmentDate >= new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()) && 
          localAppointmentDate <= new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate())
        ) {
          // Fetch professor name
          let professorName = data.professorName || '';
          if (data.professorId && !professorName) {
            const professorDoc = await getProfessorName(data.professorId);
            professorName = professorDoc?.name || 'Professor não encontrado';
          }
          
          appointmentsList.push({
            id: document.id,
            studentId: data.studentId,
            professorId: data.professorId,
            professorName: professorName,
            date: localAppointmentDate,
            time: data.startTime || data.time || data.date?.toDate().toISOString() || localAppointmentDate.toISOString(),
            duration: data.duration || 60,
            type: data.type || 'scheduled',
            status: data.status || 'scheduled',
            notes: data.notes || '',
            source: 'scheduled'
          });
        }
      }
      
      // Sort appointments by date and time
      appointmentsList.sort((a, b) => {
        const dateCompare = a.date - b.date;
        if (dateCompare === 0) {
          // If same date, compare by time
          return new Date(a.time) - new Date(b.time);
        }
        return dateCompare;
      });
      
      appointments.value = appointmentsList;
      return appointmentsList;
    } catch (err) {
      console.error('Error fetching student schedule:', err);
      error.value = err.message;
      return [];
    } finally {
      loading.value = false;
    }
  };
  
  /**
   * Get a professor's name by ID
   * @param {string} professorId - ID of the professor (which is the user ID)
   * @returns {Promise<Object|null>} - Object with user data including name or null if not found
   */
  const getProfessorName = async (professorId) => {
    try {
      // First check if the professor exists in the users collection
      const userRef = doc(db, 'users', professorId);
      const userDoc = await getDoc(userRef);
      
      if (userDoc.exists()) {
        const userData = userDoc.data();
        // Return an object with at least the name property
        return {
          name: userData.displayName || userData.name || 'Professor ' + professorId.substring(0, 5),
          email: userData.email || '',
          userId: professorId
        };
      }
      
      // If not found in users, try the professors collection as fallback
      const professorRef = doc(db, 'professors', professorId);
      const professorDoc = await getDoc(professorRef);
      
      if (professorDoc.exists()) {
        return professorDoc.data();
      }
      
      return { name: 'Professor ' + professorId.substring(0, 5) };
    } catch (err) {
      console.error('Error fetching professor name:', err);
      return { name: 'Professor (erro)' };
    }
  };
  
  /**
   * Add a new appointment
   * @param {Object} appointmentData - Appointment data object
   * @returns {Promise<string>} - ID of the new appointment
   */
  const addAppointment = async (appointmentData) => {
    try {
      loading.value = true;
      error.value = null;
      
      // Convert date string to Firestore Timestamp if needed
      if (appointmentData.date instanceof Date) {
        appointmentData.date = Timestamp.fromDate(appointmentData.date);
      }
      
      const docRef = await addDoc(collection(db, 'appointments'), appointmentData);
      return docRef.id;
    } catch (err) {
      console.error('Error adding appointment:', err);
      error.value = err.message;
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  /**
   * Update an existing appointment
   * @param {string} id - ID of the appointment to update
   * @param {Object} appointmentData - New appointment data
   * @returns {Promise<void>}
   */
  const updateAppointment = async (id, appointmentData) => {
    try {
      loading.value = true;
      error.value = null;
      
      // Convert date to Firestore Timestamp if needed
      if (appointmentData.date instanceof Date) {
        appointmentData.date = Timestamp.fromDate(appointmentData.date);
      }
      
      const appointmentRef = doc(db, 'appointments', id);
      await updateDoc(appointmentRef, appointmentData);
    } catch (err) {
      console.error('Error updating appointment:', err);
      error.value = err.message;
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  /**
   * Delete an appointment
   * @param {string} id - ID of the appointment to delete
   * @returns {Promise<void>}
   */
  const deleteAppointment = async (id) => {
    try {
      loading.value = true;
      error.value = null;
      
      const appointmentRef = doc(db, 'appointments', id);
      await deleteDoc(appointmentRef);
    } catch (err) {
      console.error('Error deleting appointment:', err);
      error.value = err.message;
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  /**
   * Fetch all scheduled appointments for a company within a date range
   * Optionally filter by studentId or professorId
   * @param {string} companyId - ID of the company
   * @param {Date} startDate - Start date for fetching appointments
   * @param {Date} endDate - End date for fetching appointments
   * @param {string} [studentId] - Optional student ID to filter by
   * @param {string} [professorId] - Optional professor ID to filter by
   * @returns {Promise<Array>} - Array of appointment objects
   */
  const fetchCompanySchedule = async (companyId, startDate, endDate, studentId = null, professorId = null) => {
    try {
      loading.value = true;
      error.value = null;
      
      // First, get all students and professors in the company
      const usersQuery = query(
        collection(db, 'users'),
        where('companyId', '==', companyId)
      );
      
      const usersSnapshot = await getDocs(usersQuery);
      const companyUserIds = usersSnapshot.docs.map(doc => doc.id);
      
      // If filtering by student or professor, validate they belong to the company
      if (studentId && !companyUserIds.includes(studentId)) {
        throw new Error('Student does not belong to this company');
      }
      if (professorId && !companyUserIds.includes(professorId)) {
        throw new Error('Professor does not belong to this company');
      }
      
      // Build query for scheduledClasses
      let classesQuery;
      if (studentId) {
        classesQuery = query(
          collection(db, 'scheduledClasses'),
          where('studentId', '==', studentId)
        );
      } else if (professorId) {
        classesQuery = query(
          collection(db, 'scheduledClasses'),
          where('professorId', '==', professorId)
        );
      } else {
        // Fetch all classes and filter by company users in memory
        classesQuery = query(collection(db, 'scheduledClasses'));
      }
      
      const classesSnapshot = await getDocs(classesQuery);
      
      // Transform scheduled classes into appointment objects
      const appointmentsList = [];
      
      for (const document of classesSnapshot.docs) {
        const data = document.data();
        
        // Filter by company if not already filtered by student/professor
        if (!studentId && !professorId) {
          if (!companyUserIds.includes(data.studentId) || !companyUserIds.includes(data.professorId)) {
            continue;
          }
        }
        
        // Use our date utilities to handle dates consistently
        const localAppointmentDate = firebaseTimestampToLocalDate(data.date);
        
        // Filter dates in memory - compare only the date part
        if (
          localAppointmentDate >= new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()) && 
          localAppointmentDate <= new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate())
        ) {
          // Fetch student and professor names
          const studentDoc = await getStudentName(data.studentId);
          const professorDoc = await getProfessorName(data.professorId);
          
          appointmentsList.push({
            id: document.id,
            studentId: data.studentId,
            studentName: studentDoc?.name || 'Aluno não encontrado',
            professorId: data.professorId,
            professorName: professorDoc?.name || 'Professor não encontrado',
            date: localAppointmentDate,
            time: data.startTime || data.time || data.date?.toDate().toISOString() || localAppointmentDate.toISOString(),
            duration: data.duration || 60,
            type: data.type || 'scheduled',
            status: data.status || 'scheduled',
            notes: data.notes || '',
            source: 'scheduled'
          });
        }
      }
      
      // Sort appointments by date and time
      appointmentsList.sort((a, b) => {
        const dateCompare = a.date - b.date;
        if (dateCompare === 0) {
          return new Date(a.time) - new Date(b.time);
        }
        return dateCompare;
      });
      
      appointments.value = appointmentsList;
      return appointmentsList;
    } catch (err) {
      console.error('Error fetching company schedule:', err);
      error.value = err.message;
      return [];
    } finally {
      loading.value = false;
    }
  };
  
  return {
    appointments,
    loading,
    error,
    fetchProfessorSchedule,
    fetchStudentSchedule,
    fetchCompanySchedule,
    addAppointment,
    updateAppointment,
    deleteAppointment
  };
});
