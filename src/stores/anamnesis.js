import { defineStore } from 'pinia';
import { doc, getDoc, setDoc, addDoc, updateDoc, getDocs, collection, query, where } from 'firebase/firestore';
import { db } from '../firebase/config';
import { useAuthStore } from './auth';

export const useAnamnesisStore = defineStore('anamnesis', {
  state: () => ({
    loading: false,
    error: null
  }),

  actions: {
    /** Get a single anamnesis by document id (for editing) */
    async getById(anamnesisId) {
      if (!anamnesisId) return null;
      this.loading = true;
      this.error = null;
      try {
        const docRef = doc(db, 'anamnesis', anamnesisId);
        const snap = await getDoc(docRef);
        if (snap.exists()) {
          const data = snap.data();
          return { id: snap.id, studentId: data.studentId || snap.id, ...data };
        }
        return null;
      } catch (err) {
        this.error = err.message;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    /** Get all anamneses for a student, sorted by date (newest first) */
    async getAnamnesesByStudentId(studentId) {
      if (!studentId) return [];
      this.loading = true;
      this.error = null;
      try {
        const q = query(
          collection(db, 'anamnesis'),
          where('studentId', '==', studentId)
        );
        const snapshot = await getDocs(q);
        const byId = new Map();
        snapshot.docs.forEach(d => {
          const data = d.data();
          byId.set(d.id, { id: d.id, studentId: data.studentId || d.id, ...data });
        });
        // Legacy: doc id was studentId (one doc per student); fetch it if not in query
        if (!byId.has(studentId)) {
          const legacyRef = doc(db, 'anamnesis', studentId);
          const legacySnap = await getDoc(legacyRef);
          if (legacySnap.exists()) {
            const data = legacySnap.data();
            byId.set(legacySnap.id, { id: legacySnap.id, studentId: data.studentId || studentId, ...data });
          }
        }
        const list = Array.from(byId.values());
        list.sort((a, b) => {
          const dateA = a.performedAt || a.updatedAt || a.createdAt || '';
          const dateB = b.performedAt || b.updatedAt || b.createdAt || '';
          return dateB.localeCompare(dateA);
        });
        return list;
      } catch (err) {
        this.error = err.message;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    /** Save: if anamnesisId provided, update existing; otherwise create new */
    async save(studentId, data, anamnesisId = null) {
      const authStore = useAuthStore();
      if (!authStore.userProfile?.companyId) throw new Error('Company not found');
      if (!studentId) throw new Error('Student is required');

      this.loading = true;
      this.error = null;
      const now = new Date().toISOString();
      const performedAt = data.performedAt || now.slice(0, 10);
      const payload = {
        studentId,
        companyId: authStore.userProfile.companyId,
        performedAt,
        patientIdentification: data.patientIdentification || '',
        mainComplaint: data.mainComplaint || '',
        currentDiseaseHistory: data.currentDiseaseHistory || '',
        socialHistory: data.socialHistory || '',
        perimeterData: data.perimeterData || '',
        posturalAssessment: data.posturalAssessment || '',
        treatment: data.treatment || '',
        updatedAt: now
      };

      try {
        if (anamnesisId) {
          const docRef = doc(db, 'anamnesis', anamnesisId);
          const existing = await getDoc(docRef);
          if (existing.exists()) {
            payload.createdAt = existing.data().createdAt;
          }
          await updateDoc(docRef, payload);
          return { id: anamnesisId, ...payload };
        } else {
          payload.createdAt = now;
          const docRef = await addDoc(collection(db, 'anamnesis'), payload);
          return { id: docRef.id, ...payload };
        }
      } catch (err) {
        this.error = err.message;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async getAllForCompany() {
      const authStore = useAuthStore();
      if (!authStore.userProfile?.companyId) return [];
      this.loading = true;
      this.error = null;
      try {
        const q = query(
          collection(db, 'anamnesis'),
          where('companyId', '==', authStore.userProfile.companyId)
        );
        const snapshot = await getDocs(q);
        return snapshot.docs.map(d => {
          const data = d.data();
          return { id: d.id, studentId: data.studentId || d.id, ...data };
        });
      } catch (err) {
        this.error = err.message;
        throw err;
      } finally {
        this.loading = false;
      }
    }
  }
});
