import { defineStore } from 'pinia';
import { 
  createUserWithEmailAndPassword, 
  signInWithEmailAndPassword, 
  signOut, 
  onAuthStateChanged 
} from 'firebase/auth';
import { doc, getDoc, setDoc } from 'firebase/firestore';
import { auth, db } from '../firebase/config';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    userProfile: null,
    loading: false,
    error: null
  }),
  
  getters: {
    isAdmin: (state) => state.userProfile?.role === 'admin',
    isProfessor: (state) => state.userProfile?.role === 'professor',
    isStudent: (state) => state.userProfile?.role === 'student',
    isAuthenticated: (state) => !!state.user,
    userId: (state) => state.user?.uid
  },
  
  actions: {
    async register(email, password, role, userData) {
      this.loading = true;
      this.error = null;
      
      try {
        // Create user with Firebase Authentication
        const { user } = await createUserWithEmailAndPassword(auth, email, password);
        
        // Create user profile in Firestore with role
        const userProfile = {
          email: user.email,
          role,
          createdAt: new Date().toISOString(),
          ...userData
        };
        
        await setDoc(doc(db, 'users', user.uid), userProfile);
        
        // Update local state
        this.user = user;
        this.userProfile = userProfile;
        
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
    }
  }
});
