import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';

export const firebaseConfig = {
  apiKey: "AIzaSyB6xfteJelZLxiszYwiprugoSRSnZn4YGM",
  authDomain: "saudepilates-170df.firebaseapp.com",
  projectId: "saudepilates-170df",
  storageBucket: "saudepilates-170df.appspot.com",
  messagingSenderId: "311012649134",
  appId: "1:311012649134:web:698eec2274ff1c0583e53f",
  measurementId: "G-B0KTCDJHD8"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const db = getFirestore(app);

let _storage = null;
let _analytics = null;

export async function getStorageLazy() {
  if (!_storage) {
    const { getStorage } = await import('firebase/storage');
    _storage = getStorage(app);
  }
  return _storage;
}

export async function getAnalyticsLazy() {
  if (!_analytics) {
    const { getAnalytics } = await import('firebase/analytics');
    _analytics = getAnalytics(app);
  }
  return _analytics;
}

export { auth, db };
