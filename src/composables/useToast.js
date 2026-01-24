/**
 * Toast notification composable
 * Provides methods to show toast notifications throughout the app
 */

let toastInstance = null;

export const useToast = () => {
  const showToast = (message, type = 'info', duration = 5000) => {
    if (toastInstance) {
      return toastInstance.addToast(message, type, duration);
    }
    // Fallback to window methods if composable not available
    if (typeof window !== 'undefined') {
      if (window.showToast) {
        return window.showToast(message, type, duration);
      }
    }
    console.warn('Toast system not initialized');
  };

  const showSuccess = (message, duration = 5000) => {
    return showToast(message, 'success', duration);
  };

  const showError = (message, duration = 5000) => {
    return showToast(message, 'error', duration);
  };

  const showWarning = (message, duration = 5000) => {
    return showToast(message, 'warning', duration);
  };

  const showInfo = (message, duration = 5000) => {
    return showToast(message, 'info', duration);
  };

  return {
    showToast,
    showSuccess,
    showError,
    showWarning,
    showInfo
  };
};

// Register toast instance (called by Toast component)
export const registerToastInstance = (instance) => {
  toastInstance = instance;
};
