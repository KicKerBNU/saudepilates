// Import Firebase modules
import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';
import { getStorage } from 'firebase/storage';
import { getAnalytics } from 'firebase/analytics';

// Your Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyB6xfteJelZLxiszYwiprugoSRSnZn4YGM",
  authDomain: "saudepilates-170df.firebaseapp.com",
  projectId: "saudepilates-170df",
  storageBucket: "saudepilates-170df.appspot.com",
  messagingSenderId: "311012649134",
  appId: "1:311012649134:web:698eec2274ff1c0583e53f",
  measurementId: "G-B0KTCDJHD8"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const db = getFirestore(app);
const storage = getStorage(app);
const analytics = getAnalytics(app);

export { auth, db, storage, analytics };
