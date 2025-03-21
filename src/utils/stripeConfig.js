// Stripe payment URLs configuration

// Determine if we're in production environment
const isProduction = import.meta.env.PROD;

// Plan configurations using environment variables
export const STRIPE_PLANS = {
  anual: {
    name: 'Saude Pilates Anual',
    productId: import.meta.env.VITE_STRIPE_PRODUCT_ID_ANUAL,
    test: import.meta.env.VITE_STRIPE_TEST_URL_ANUAL,
    production: import.meta.env.VITE_STRIPE_PROD_URL_ANUAL
  },
  trimestral: {
    name: 'Saude Pilates Trimestral',
    productId: import.meta.env.VITE_STRIPE_PRODUCT_ID_TRIMESTRAL,
    test: import.meta.env.VITE_STRIPE_TEST_URL_TRIMESTRAL,
    production: import.meta.env.VITE_STRIPE_PROD_URL_TRIMESTRAL
  },
  mensal: {
    name: 'Saude Pilates Mensal',
    productId: import.meta.env.VITE_STRIPE_PRODUCT_ID_MENSAL,
    test: import.meta.env.VITE_STRIPE_TEST_URL_MENSAL,
    production: import.meta.env.VITE_STRIPE_PROD_URL_MENSAL
  }
};

/**
 * Get the appropriate Stripe URL based on environment
 * @param {string} planType - The plan type (anual, trimestral, mensal)
 * @returns {string} The Stripe URL for the plan
 */
export const getStripeUrl = (planType) => {
  const plan = STRIPE_PLANS[planType];
  if (!plan) return null;
  
  return isProduction ? plan.production : plan.test;
}; 