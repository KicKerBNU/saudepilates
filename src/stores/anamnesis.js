import { defineStore } from 'pinia';
import { doc, getDoc, setDoc, getDocs, collection, query, where } from 'firebase/firestore';
import { db } from '../firebase/config';
import { useAuthStore } from './auth';

export const useAnamnesisStore = defineStore('anamnesis', {
  state: () => ({
    loading: false,
    error: null
  }),

  actions: {
    async getByStudentId(studentId) {
      if (!studentId) return null;
      this.loading = true;
      this.error = null;
      try {
        const docRef = doc(db, 'anamnesis', studentId);
        const snap = await getDoc(docRef);
        if (snap.exists()) {
          return { id: snap.id, ...snap.data() };
        }
        return null;
      } catch (err) {
        this.error = err.message;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async save(studentId, data) {
      const authStore = useAuthStore();
      if (!authStore.userProfile?.companyId) throw new Error('Company not found');
      if (!studentId) throw new Error('Student is required');

      this.loading = true;
      this.error = null;
      try {
        const docRef = doc(db, 'anamnesis', studentId);
        const existing = await getDoc(docRef);
        const now = new Date().toISOString();
        const payload = {
          studentId,
          companyId: authStore.userProfile.companyId,
          patientIdentification: data.patientIdentification || '',
          mainComplaint: data.mainComplaint || '',
          currentDiseaseHistory: data.currentDiseaseHistory || '',
          socialHistory: data.socialHistory || '',
          perimeterData: data.perimeterData || '',
          posturalAssessment: data.posturalAssessment || '',
          updatedAt: now
        };
        if (!existing.exists()) {
          payload.createdAt = now;
        } else {
          payload.createdAt = existing.data().createdAt;
        }
        await setDoc(docRef, payload, { merge: true });
        return { id: studentId, ...payload };
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
        return snapshot.docs.map(d => ({ id: d.id, ...d.data() }));
      } catch (err) {
        this.error = err.message;
        throw err;
      } finally {
        this.loading = false;
      }
    }
  }
});
