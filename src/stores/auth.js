import { defineStore } from 'pinia';
import { 
  createUserWithEmailAndPassword, 
  signInWithEmailAndPassword, 
  signOut, 
  onAuthStateChanged 
} from 'firebase/auth';
import { doc, getDoc, setDoc, collection, addDoc, query, where, getDocs, updateDoc, deleteDoc } from 'firebase/firestore';
import { auth, db } from '../firebase/config';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    userProfile: null,
    companyInfo: null,
    loading: false,
    error: null
  }),
  
  getters: {
    isAdmin: (state) => state.userProfile?.role === 'admin',
    isProfessor: (state) => state.userProfile?.role === 'professor',
    isStudent: (state) => state.userProfile?.role === 'student',
    isAuthenticated: (state) => !!state.user,
    userId: (state) => state.user?.uid,
    companyId: (state) => state.userProfile?.companyId || null,
    companyName: (state) => state.companyInfo?.name || null
  },
  
  actions: {
    async register(email, password, companyData, userData) {
      this.loading = true;
      this.error = null;
      
      try {
        // For new registrations, role is always admin
        const role = 'admin';
        
        // Create user with Firebase Authentication
        const { user } = await createUserWithEmailAndPassword(auth, email, password);
        
        // Create a company first
        const companyRef = await addDoc(collection(db, 'companies'), {
          name: companyData.name,
          createdAt: new Date().toISOString(),
          ownerId: user.uid
        });
        
        // Create user profile in Firestore with role and company ID
        const userProfile = {
          email: user.email,
          role,
          companyId: companyRef.id,
          createdAt: new Date().toISOString(),
          ...userData
        };
        
        await setDoc(doc(db, 'users', user.uid), userProfile);
        
        // Update local state
        this.user = user;
        this.userProfile = userProfile;
        this.companyInfo = { 
          id: companyRef.id,
          name: companyData.name
        };
        
        return user;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async login(email, password) {
      this.loading = true;
      this.error = null;
      
      try {
        console.log('Attempting login for:', email);
        const { user } = await signInWithEmailAndPassword(auth, email, password);
        console.log('Firebase authentication successful:', user.uid);
        
        // Set user state immediately
        this.user = {
          uid: user.uid,
          email: user.email,
          emailVerified: user.emailVerified
        };
        
        // Fetch user profile
        await this.fetchUserProfile(user.uid);
        
        console.log('Login complete with profile:', this.userProfile);
        return user;
      } catch (error) {
        console.error('Login error:', error.code, error.message);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async logout() {
      try {
        await signOut(auth);
        this.user = null;
        this.userProfile = null;
      } catch (error) {
        this.error = error.message;
        throw error;
      }
    },
    
    async fetchUserProfile(userId) {
      // Validate userId first
      if (!userId) {
        console.error('fetchUserProfile called with invalid userId');
        this.error = 'Invalid user ID';
        this.loading = false;
        return;
      }

      this.loading = true;
      console.log('Fetching user profile for ID:', userId);
      
      try {
        // Create document reference
        const userRef = doc(db, 'users', userId);
        console.log('User document reference created');
        
        // Get the document
        const docSnapshot = await getDoc(userRef);
        console.log('Document snapshot retrieved:', docSnapshot);
        
        // Check if document exists and has data
        const exists = docSnapshot && typeof docSnapshot.exists === 'function' ? docSnapshot.exists() : false;
        console.log('Document exists?', exists);
        
        if (exists) {
          // Document exists, get data
          const data = docSnapshot.data();
          console.log('User profile data:', data);
          this.userProfile = data;
          
          // If the user has a companyId, fetch company information
          if (data.companyId) {
            await this.fetchCompanyInfo(data.companyId);
          }
        } else {
          // Document doesn't exist, create a basic profile
          console.log('User profile not found, creating a basic one');
          this.error = 'User profile not found';
          
          // Create a basic profile
          const basicProfile = {
            email: this.user?.email || 'unknown',
            role: 'student', // Default role
            createdAt: new Date().toISOString()
          };
          
          try {
            await setDoc(userRef, basicProfile);
            console.log('Basic profile created');
            this.userProfile = basicProfile;
          } catch (createError) {
            console.error('Failed to create basic profile:', createError);
          }
        }
      } catch (error) {
        console.error('Error in fetchUserProfile:', error);
        this.error = error.message;
      } finally {
        this.loading = false;
      }
    },
    
    async fetchCompanyInfo(companyId) {
      try {
        const companyRef = doc(db, 'companies', companyId);
        const companyDoc = await getDoc(companyRef);
        
        if (companyDoc.exists()) {
          this.companyInfo = {
            id: companyId,
            ...companyDoc.data()
          };
          console.log('Company info fetched:', this.companyInfo);
        } else {
          console.error('Company not found with ID:', companyId);
          this.companyInfo = null;
        }
      } catch (error) {
        console.error('Error fetching company info:', error);
        this.companyInfo = null;
      }
    },
    
    async createUserForCompany(email, password, role, userData) {
      if (!this.isAdmin) {
        throw new Error('Only administrators can create new users');
      }
      
      if (!this.userProfile.companyId) {
        throw new Error('Admin must belong to a company to add users');
      }
      
      this.loading = true;
      this.error = null;
      
      try {
        // Create user with Firebase Authentication
        const { user } = await createUserWithEmailAndPassword(auth, email, password);
        
        // Create user profile with the admin's company ID
        const userProfile = {
          email: user.email,
          role,
          companyId: this.userProfile.companyId,
          createdAt: new Date().toISOString(),
          ...userData
        };
        
        await setDoc(doc(db, 'users', user.uid), userProfile);
        
        return user;
      } catch (error) {
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    init() {
      // Already initialized, don't do it again
      if (this._initialized) return Promise.resolve(this.user);
      
      return new Promise((resolve) => {
        // Keep a reference to the unsubscribe function
        this._authUnsubscribe = onAuthStateChanged(auth, async (user) => {
          console.log('Auth state changed:', user ? 'Authenticated' : 'Not authenticated');
          this.loading = true;
          this.user = user;
          
          try {
            if (user) {
              await this.fetchUserProfile(user.uid);
            } else {
              this.userProfile = null;
            }
          } catch (error) {
            console.error('Error fetching user profile:', error);
            this.error = 'Failed to load user profile';
          } finally {
            this.loading = false;
            this._initialized = true;
            resolve(user);
          }
        });
      });
    },
    
    // Clean up auth listener when no longer needed (e.g., app unmount)
    cleanup() {
      if (this._authUnsubscribe) {
        this._authUnsubscribe();
        this._authUnsubscribe = null;
      }
      this._initialized = false;
    },
    
    async getUsersByCompany(role = null) {
      if (!this.userProfile?.companyId) {
        console.error('No company ID found for current user');
        return [];
      }
      
      try {
        let q;
        if (role) {
          // Query users with specific role in the company
          q = query(
            collection(db, 'users'), 
            where('companyId', '==', this.userProfile.companyId),
            where('role', '==', role)
          );
        } else {
          // Query all users in the company
          q = query(
            collection(db, 'users'), 
            where('companyId', '==', this.userProfile.companyId)
          );
        }
        
        const querySnapshot = await getDocs(q);
        const users = [];
        
        querySnapshot.forEach((doc) => {
          users.push({
            id: doc.id,
            ...doc.data()
          });
        });
        
        return users;
      } catch (error) {
        console.error('Error fetching company users:', error);
        this.error = error.message;
        return [];
      }
    },

    async updateUser(userId, userData) {
      if (!this.userProfile?.companyId) {
        throw new Error('No company ID found for current user');
      }
      
      if (!this.isAdmin) {
        throw new Error('Only admins can update users');
      }
      
      this.loading = true;
      this.error = null;
      
      try {
        // Add updated timestamp
        const dataToUpdate = {
          ...userData,
          updatedAt: new Date().toISOString()
        };
        
        // Update the user document in Firestore
        const userRef = doc(db, 'users', userId);
        await updateDoc(userRef, dataToUpdate);
        
        return { id: userId, ...dataToUpdate };
      } catch (error) {
        console.error('Error updating user:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    
    async deleteUser(userId) {
      if (!this.userProfile?.companyId) {
        throw new Error('No company ID found for current user');
      }
      
      if (!this.isAdmin) {
        throw new Error('Only admins can delete users');
      }
      
      this.loading = true;
      this.error = null;
      
      try {
        // Delete the user document from Firestore
        const userRef = doc(db, 'users', userId);
        await deleteDoc(userRef);
        
        return true;
      } catch (error) {
        console.error('Error deleting user:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Plans Management
    async getPlans() {
      if (!this.userProfile?.companyId) {
        throw new Error('No company ID found for current user');
      }

      this.loading = true;
      this.error = null;

      try {
        const plansRef = collection(db, 'plans');
        const q = query(plansRef, where('companyId', '==', this.userProfile.companyId));
        const querySnapshot = await getDocs(q);

        return querySnapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data()
        }));
      } catch (error) {
        console.error('Error fetching plans:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async addPlan(planData) {
      if (!this.userProfile?.companyId) {
        throw new Error('No company ID found for current user');
      }

      if (!this.isAdmin) {
        throw new Error('Only admins can add plans');
      }

      this.loading = true;
      this.error = null;

      try {
        const plansRef = collection(db, 'plans');
        const newPlan = {
          ...planData,
          companyId: this.userProfile.companyId,
          createdAt: new Date().toISOString()
        };

        const docRef = await addDoc(plansRef, newPlan);
        return {
          id: docRef.id,
          ...newPlan
        };
      } catch (error) {
        console.error('Error adding plan:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async updatePlan(planId, planData) {
      if (!this.userProfile?.companyId) {
        throw new Error('No company ID found for current user');
      }

      if (!this.isAdmin) {
        throw new Error('Only admins can update plans');
      }

      this.loading = true;
      this.error = null;

      try {
        const planRef = doc(db, 'plans', planId);
        const dataToUpdate = {
          ...planData,
          updatedAt: new Date().toISOString()
        };

        await updateDoc(planRef, dataToUpdate);
        return {
          id: planId,
          ...dataToUpdate
        };
      } catch (error) {
        console.error('Error updating plan:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async deletePlan(planId) {
      if (!this.userProfile?.companyId) {
        throw new Error('No company ID found for current user');
      }

      if (!this.isAdmin) {
        throw new Error('Only admins can delete plans');
      }

      this.loading = true;
      this.error = null;

      try {
        const planRef = doc(db, 'plans', planId);
        await deleteDoc(planRef);
        return true;
      } catch (error) {
        console.error('Error deleting plan:', error);
        this.error = error.message;
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});
