import emailjs from '@emailjs/browser';

/**
 * Service for handling email functionality using EmailJS
 */
export const EmailService = {
  /**
   * Sends an auto-reply email using EmailJS
   * @param {Object} contactData - Contact form data
   * @param {string} contactData.name - Contact's name
   * @param {string} contactData.email - Contact's email
   * @param {string} contactData.subject - Message subject
   * @param {string} contactData.message - Message content
   * @param {Object} companyData - Company information
   * @param {string} companyData.name - Company name
   * @param {string} companyData.logo - Company logo URL
   * @param {string} companyData.phone - Company phone
   * @param {string} companyData.whatsapp - Company WhatsApp
   * @param {string} companyData.website - Company website URL
   * @param {string} companyData.instagram - Company Instagram URL
   * @param {string} companyData.facebook - Company Facebook URL
   * @returns {Promise} - Promise with the result of the email sending operation
   */
  sendAutoReply: async (contactData, companyData) => {
    try {
      // Make sure required values exist
      if (!contactData || !contactData.email) {
        throw new Error('Recipient email is required');
      }

      // Get current year for template
      const currentYear = new Date().getFullYear();

      // Prepare template parameters
      // IMPORTANT: The parameter names must match exactly what's expected in your EmailJS template
      const templateParams = {
        to_name: contactData.name || 'Cliente',
        from_name: companyData?.name || 'Saúde Pilates',
        user_email: contactData.email, // Try this parameter name for recipient
        email: contactData.email, // Alternative parameter name
        recipient: contactData.email, // Another alternative
        to_email: contactData.email, // Standard parameter name
        reply_to: contactData.email, // For reply functionality
        subject: 'Recebemos sua mensagem - Saúde Pilates',
        name: contactData.name || 'Cliente',
        message: contactData.message || '',
        company_logo: companyData?.logo || '',
        company_phone: companyData?.phone || '',
        company_whatsapp: companyData?.whatsapp || '',
        website_url: companyData?.website || '',
        instagram_url: companyData?.instagram || '',
        facebook_url: companyData?.facebook || '',
        whatsapp_url: companyData?.whatsapp ? `https://wa.me/${companyData.whatsapp.replace(/\D/g, '')}` : '',
        current_year: currentYear.toString(),
      };
      
      const serviceId = 'service_6tlvlos';
      const templateId = 'template_p043u8o';
      const publicKey = '_DcuCvNtpYC2WN3Ia';

      // Initialize EmailJS if not already initialized
      if (!emailjs.init) {
        emailjs.init(publicKey);
      }
      
      // Send the email
      const response = await emailjs.send(
        serviceId,
        templateId,
        templateParams,
        publicKey
      );

      return response;
    } catch (error) {
      console.error('Failed to send email:', error);
      throw error;
    }
  },

  /**
   * Initializes EmailJS with your public key
   * @param {string} publicKey - Your EmailJS public key
   */
  init: (publicKey) => {
    emailjs.init(publicKey);
  }
};
