import { defineStore } from 'pinia';
import { doc, getDoc, setDoc, updateDoc } from 'firebase/firestore';
import { db } from '../firebase/config';
import { useAuthStore } from './auth';

export const useSubscriptionStore = defineStore('subscription', {
  state: () => ({
    isSubscribed: false,
    expirationDate: null,
    subscriptionPlan: null,
    loading: false,
    error: null
  }),
  
  getters: {
    isValid: (state) => {
      if (!state.isSubscribed) return false;
      if (!state.expirationDate) return false;
      
      const now = new Date();
      const expDate = new Date(state.expirationDate);
      return expDate > now;
    },
    
    daysUntilExpiration: (state) => {
      if (!state.expirationDate) return 0;
      
      const now = new Date();
      const expDate = new Date(state.expirationDate);
      const diffTime = expDate - now;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      return diffDays > 0 ? diffDays : 0;
    }
  },
  
  actions: {
    async fetchSubscription() {
      this.loading = true;
      this.error = null;
      
      try {
        const authStore = useAuthStore();
        
        // Make sure the user is authenticated and has a company
        if (!authStore.isAuthenticated || !authStore.companyId) {
          throw new Error('User not authenticated or no company association');
        }
        
        // Get subscription document from Firestore
        const subscriptionRef = doc(db, 'subscriptions', authStore.companyId);
        const subscriptionDoc = await getDoc(subscriptionRef);
        
        if (subscriptionDoc.exists()) {
          const data = subscriptionDoc.data();
          this.isSubscribed = data.isSubscribed;
          this.expirationDate = data.expirationDate;
          this.subscriptionPlan = data.plan;
        } else {
          // No subscription document exists - create a default one with 30-day trial
          const trialExpiration = new Date();
          trialExpiration.setDate(trialExpiration.getDate() + 30);
          
          const subscriptionData = {
            isSubscribed: true,
            expirationDate: trialExpiration.toISOString(),
            plan: 'trial',
            createdAt: new Date().toISOString(),
            companyId: authStore.companyId
          };
          
          await setDoc(subscriptionRef, subscriptionData);
          
          this.isSubscribed = true;
          this.expirationDate = subscriptionData.expirationDate;
          this.subscriptionPlan = 'trial';
        }
        
        return {
          isSubscribed: this.isSubscribed,
          expirationDate: this.expirationDate,
          plan: this.subscriptionPlan
        };
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async updateSubscription(subscriptionData) {
      this.loading = true;
      this.error = null;
      
      try {
        const authStore = useAuthStore();
        
        if (!authStore.isAuthenticated || !authStore.companyId) {
          throw new Error('User not authenticated or no company association');
        }
        
        const subscriptionRef = doc(db, 'subscriptions', authStore.companyId);
        
        // Update subscription in Firestore
        await updateDoc(subscriptionRef, {
          ...subscriptionData,
          updatedAt: new Date().toISOString()
        });
        
        // Update local state
        if (subscriptionData.isSubscribed !== undefined) {
          this.isSubscribed = subscriptionData.isSubscribed;
        }
        
        if (subscriptionData.expirationDate) {
          this.expirationDate = subscriptionData.expirationDate;
        }
        
        if (subscriptionData.plan) {
          this.subscriptionPlan = subscriptionData.plan;
        }
        
        return {
          isSubscribed: this.isSubscribed,
          expirationDate: this.expirationDate,
          plan: this.subscriptionPlan
        };
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async renewSubscription(monthsDuration = 1) {
      this.loading = true;
      this.error = null;
      
      try {
        // Calculate new expiration date
        let newExpirationDate;
        
        if (this.expirationDate && new Date(this.expirationDate) > new Date()) {
          // If subscription is still valid, extend from current expiration
          newExpirationDate = new Date(this.expirationDate);
        } else {
          // If subscription has expired, start from today
          newExpirationDate = new Date();
        }
        
        // Add months to the expiration date
        newExpirationDate.setMonth(newExpirationDate.getMonth() + monthsDuration);
        
        // Update subscription
        return await this.updateSubscription({
          isSubscribed: true,
          expirationDate: newExpirationDate.toISOString(),
          plan: 'premium'
        });
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
}); 