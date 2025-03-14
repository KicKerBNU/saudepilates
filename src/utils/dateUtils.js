/**
 * Utility functions for handling dates in the SaudePilates application
 * Using date-fns for reliable cross-timezone date operations
 */

import { Timestamp } from 'firebase/firestore';
import { parse, parseISO, format, setHours, startOfDay, isEqual, isSameDay as fnIsSameDay } from 'date-fns';

/**
 * Convert a date string or Date object to a Firebase Timestamp
 * @param {string|Date} date - Date to convert (YYYY-MM-DD string or Date object)
 * @returns {Timestamp} Firebase Timestamp object set to noon on the specified date
 */
export function dateToFirebaseTimestamp(date) {
  let dateObj;
  
  if (date instanceof Date) {
    dateObj = date;
  } else if (typeof date === 'string') {
    // Parse dates properly using date-fns
    if (date.includes('T')) {
      // ISO format string
      dateObj = parseISO(date);
    } else {
      // YYYY-MM-DD format
      dateObj = parse(date, 'yyyy-MM-dd', new Date());
    }
  } else {
    // Default to today
    dateObj = new Date();
  }
  
  // Set to noon to avoid any timezone boundary issues
  const normalizedDate = setHours(startOfDay(dateObj), 12);
  
  return Timestamp.fromDate(normalizedDate);
}

/**
 * Extract date components from a Firebase Timestamp and create a local date
 * @param {Timestamp} timestamp - Firebase Timestamp object
 * @returns {Date} Local date object at noon on the same day
 */
export function firebaseTimestampToLocalDate(timestamp) {
  if (!timestamp || typeof timestamp.toDate !== 'function') {
    console.warn('Invalid timestamp provided to firebaseTimestampToLocalDate');
    return new Date();
  }
  
  // Get the JavaScript Date from Firestore Timestamp
  const jsDate = timestamp.toDate();
  
  // Normalize to noon on the same day to avoid timezone issues
  const normalizedDate = setHours(startOfDay(jsDate), 12);
  
  return normalizedDate;
}

/**
 * Format a date as YYYY-MM-DD
 * @param {Date} date - Date to format
 * @returns {string} Formatted date string
 */
export function formatDateYYYYMMDD(date) {
  return format(date, 'yyyy-MM-dd');
}

/**
 * Compare two dates ignoring time components
 * @param {Date} date1 - First date
 * @param {Date} date2 - Second date
 * @returns {boolean} True if dates represent the same day
 */
export function isSameDay(date1, date2) {
  return fnIsSameDay(date1, date2);
}

/**
 * Create a normalized date object (noon) for consistent date comparisons
 * @param {number} year - Year
 * @param {number} month - Month (1-12)
 * @param {number} day - Day of month
 * @returns {Date} Date object set to noon on the specified date
 */
export function createNormalizedDate(year, month, day) {
  // Note: month is 1-12 in parameters but 0-11 in Date constructor
  const date = new Date(year, month - 1, day);
  return setHours(startOfDay(date), 12);
}
