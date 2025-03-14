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
      
      // Fetch from attendanceRecords AND scheduledClasses collections
      // First get attendance records
      const attendanceQuery = query(
        collection(db, 'attendanceRecords'),
        where('professorId', '==', professorId)
      );
      
      const querySnapshot = await getDocs(attendanceQuery);
      
      // Then get scheduled classes
      const classesQuery = query(
        collection(db, 'scheduledClasses'),
        where('professorId', '==', professorId)
      );
      
      const classesSnapshot = await getDocs(classesQuery);
      
      // Transform the attendance records into appointment objects
      const appointmentsList = [];
      
      // Process attendance records
      for (const document of querySnapshot.docs) {
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
            type: data.type || 'individual',
            status: data.status || 'confirmed',
            notes: data.notes || '',
            source: 'attendance'
          });
        }
      }
      
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
  
  return {
    appointments,
    loading,
    error,
    fetchProfessorSchedule,
    addAppointment,
    updateAppointment,
    deleteAppointment
  };
});
